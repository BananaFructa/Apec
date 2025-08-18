package uk.co.hexeption.apec.skyblock;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import uk.co.hexeption.apec.Apec;
import uk.co.hexeption.apec.EventIDs;
import uk.co.hexeption.apec.MC;
import uk.co.hexeption.apec.api.SBAPI;
import uk.co.hexeption.apec.mixins.accessors.PlayerTabOverlayAccessor;
import uk.co.hexeption.apec.settings.SettingID;
import uk.co.hexeption.apec.utils.ApecUtils;

/**
 * Main class for processing and storing SkyBlock information.
 * Handles scoreboard data, player stats, and other game-related information.
 */
public class SkyBlockInfo implements SBAPI, MC {

    // Scoreboard related fields
    private SBScoreBoard scoreboard = SBScoreBoard.EMPTY;
    private boolean onSkyblock;
    private boolean isInRift = false;
    private boolean isInDungeon = false;
    private boolean usesPiggyBank;

    // Cache for components
    private Component cachedPurse = Component.empty();
    private Component cachedBits = Component.empty();

    // Scoreboard data storage
    private final ObjectArrayList<String> stringScoreboard = new ObjectArrayList<>();
    private final ObjectArrayList<Component> componentScoreboard = new ObjectArrayList<>();

    // Player stats and UI components
    private PlayerStats playerStats = PlayerStats.EMPTY;
    private Component clientOverlay = Component.empty();
    private Component clientTabFooter = Component.empty();

    // Player stats caching fields
    private int lastHp = 1, lastBaseHp = 1;
    private int lastMana = 1, lastBaseMana = 1;
    private int lastDefence = 0;
    private int baseAbsorption = 0;
    private int lastAbsorption = 1, lastBaseAbsorption = 1;
    private int baseOverflow = 1;

    // Additional data
    private OtherData otherData;

    // Game symbols and identifiers as constants
    private static final class GameSymbols {
        // Race symbols
        static final String END_RACE = "THE END RACE";
        static final String WOODS_RACING = "WOODS RACING";
        static final String CHICKEN_RACE = "CHICKEN RACING";
        static final String CRYSTAL_RACE = "CRYSTAL CORE RACE";
        static final String GIANT_MUSHROOM = "GIANT MUSHROOM RACE";
        static final String PRECURSOR_RUINS = "PRECURSOR RUINS RACE";

        // Other game indicators
        static final String DPS = "DPS";
        static final String SECOND = "second";
        static final String SECRETS = "Secrets";
        static final String JUMP = "JUMP";
        static final String REVIVE = "Revive";
        static final String ARMADILLO = "Armadillo";
        static final String TREASURE = "TREASURE:";

        // Kuudra symbols
        static final char DOMINUS = '\u1750';
        static final char ARCANE = '\u046A';
        static final char FERVOR = '\u0489';
        static final char SPIRIT = '\u26B6';
        static final char HYDRA = '\u2051';

        // Rift symbol
        static final char RIFT = 'ф';

        // Health symbols
        static final char HEALTH = '❤';
        static final char DEFENSE = '❈';
        static final char MANA = '✎';
        static final char OVERFLOW_MANA = 'ʬ';
    }

    /**
     * Initializes the SkyBlockInfo class by registering event handlers.
     */
    public void init() {
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
            processTick(client);
        });

        net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (overlay) {
                processChatMessage(null, message);
            }
        });

        net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents.GAME_CANCELED.register((message, overlay) -> {
            if (overlay) {
                processChatMessage(null, message);
            }
        });
    }

    /**
     * Processes chat messages for relevant game information.
     *
     * @param bound The chat message bound, possibly null on Fabric
     * @param component The message component
     * @return An unmodified event result
     */
    private Component processChatMessage(ChatType.Bound bound, Component component) {
        String message = component.getString();

        // Process overlay messages (health, mana, special events)
        if (message.contains("❤") || message.contains("✎") ||
            message.contains(GameSymbols.REVIVE) ||
            message.contains(GameSymbols.CHICKEN_RACE) ||
            message.contains(GameSymbols.ARMADILLO)) {
            this.clientOverlay = component;
        }

        // Check if player is in the Rift dimension
        this.isInRift = message.contains(String.valueOf(GameSymbols.RIFT));

        return component;
    }

    /**
     * Main game tick processing method that updates all SkyBlock information.
     *
     * @param minecraft The Minecraft instance
     */
    private void processTick(Minecraft minecraft) {
        Component scoreboardTitle = getScoreboardTitle();
        if (scoreboardTitle.getString().isEmpty()) {
            return;
        }

        this.onSkyblock = scoreboardTitle.getString().contains("SKYBLOCK");

        // Process information in this order
        collectScoreboardLines();
        parseScoreboardData();
        parsePlayerStats();
        this.otherData = processOtherData(scoreboard);
        this.clientTabFooter = ((PlayerTabOverlayAccessor) mc.gui.getTabList()).getFooter();
    }

    /**
     * Parses scoreboard data to extract game information.
     */
    private void parseScoreboardData() {
        String irlDate = "";
        String serverShard = "";
        String date = "";
        String hour = "";
        Component zone = Component.empty();
        Component purse = Component.empty();
        Component bits = Component.empty();
        Component gameType = Component.empty();
        ArrayList<Component> extra = new ArrayList<>();

        this.isInDungeon = false;

        for (Component component : this.componentScoreboard) {
            String line = component.getString();

            // Check for dungeon indicator
            if (ApecUtils.isContainedIn(line, "The Catacombs")) {
                this.isInDungeon = true;
            }

            // Parse various scoreboard sections
            if (ApecUtils.isContainedIn(line, "//")) {
                String[] parts = ApecUtils.removeFirstSpaces(line).split(" ");
                irlDate = parts.length > 0 ? parts[0] : "";
                serverShard = parts.length > 1 ? parts[1] : "";
            } else if (isGameTypeIndicator(line)) {
                gameType = component;
            } else if (isDate(line)) {
                date = ApecUtils.removeFirstSpaces(line);
            } else if (isTime(line)) {
                hour = ApecUtils.removeFirstSpaces(line);
            } else if (ApecUtils.containedByCharSequence(line, "⏣")) {
                zone = component;
            } else if (ApecUtils.containedByCharSequence(line, "Purse: ")) {
                purse = component;
                this.cachedPurse = component;
                this.usesPiggyBank = false;
            } else if (ApecUtils.containedByCharSequence(line, "Piggy: ")) {
                purse = component;
                this.cachedPurse = component;
                this.usesPiggyBank = true;
            } else if (ApecUtils.containedByCharSequence(line, "Bits: ")) {
                bits = component;
                this.cachedBits = component;
            } else if (!line.isEmpty() && !line.contains("www")) {
                extra.add(component);
            }
        }

        // Use cached values if none were found in the current scoreboard
        if (purse.equals(Component.empty()) && !this.cachedPurse.equals(Component.empty())) {
            purse = this.cachedPurse;
        }

        if (bits.equals(Component.empty()) && !this.cachedBits.equals(Component.empty())) {
            bits = this.cachedBits;
        }

        // Create a new scoreboard instance with updated data
        this.scoreboard = new SBScoreBoard(
                serverShard,
                purse,
                bits,
                extra,
                zone,
                date,
                hour,
                irlDate,
                getScoreboardTitle().getString(),
                gameType);
    }

    /**
     * Determines if a line indicates a game type.
     *
     * @param line The line to check
     * @return True if the line indicates a game type
     */
    private boolean isGameTypeIndicator(String line) {
        return ApecUtils.containedByCharSequence(line, "♲") ||
               ApecUtils.containedByCharSequence(line, "☀ Stranded") ||
               ApecUtils.containedByCharSequence(line, "Ⓑ");
    }

    /**
     * Parses player stats from the action bar overlay.
     */
    private void parsePlayerStats() {
        String actionBar = this.clientOverlay.getString();

        // Initialize variables
        int hp = 0;
        int baseHp = 0;
        int absorption = 0;
        int baseAbsorption = 0;
        int healDuration = 0;
        char healDurationTicker = 0;
        int mana = 0;
        int baseMana = 0;
        int overflow = 0;
        int baseOverflow = 0;
        int defence = 0;
        String lastSkillXp = "";
        float skillXpPercentage = -1f;
        String skillInfo = "";
        boolean skillShown = false;
        boolean abilityShown = false;
        String abilityText = "";
        String riftTimer = "";
        String kuudraTieredBonus = "";
        int drillFuelRemaining = 0;
        int drillFuelCapacity = 0;

        // Parse health
        try {
            if (!isInRift) {
                String segment = ApecUtils.segmentString(actionBar, String.valueOf(GameSymbols.HEALTH), '§', GameSymbols.HEALTH, 1, 1);
                if (segment != null) {
                    Tuple<Integer, Integer> hpTuple = parseStringFraction(ApecUtils.removeAllColourCodes(segment));
                    hp = hpTuple.getA();
                    baseHp = hpTuple.getB();

                    if (hp > baseHp) {
                        absorption = hp - baseHp;
                        hp = baseHp;
                    } else {
                        absorption = 0;
                        this.baseAbsorption = 0;
                    }
                    if (absorption > this.baseAbsorption) {
                        this.baseAbsorption = absorption;
                    }
                    baseAbsorption = this.baseAbsorption;

                    lastAbsorption = absorption;
                    lastBaseAbsorption = baseAbsorption;

                    lastHp = hp;
                    lastBaseHp = baseHp;
                } else {
                    hp = lastHp;
                    baseHp = lastBaseHp;
                    absorption = lastAbsorption;
                    baseAbsorption = lastBaseAbsorption;
                }
            } else {
                LocalPlayer player = mc.player;
                assert player != null;

                hp = lastHp = (int)player.getHealth();
                baseHp = lastBaseHp = (int)player.getMaxHealth();
                baseAbsorption = lastBaseAbsorption = absorption = lastAbsorption = (int)player.getAbsorptionAmount();
            }
        } catch (Exception err) {
            hp = lastHp;
            baseHp = lastBaseHp;
            absorption = lastAbsorption;
            baseAbsorption = lastBaseAbsorption;
        }

        // Parse heal duration
        try {
            char[] healDurationSymbols = new char[] { '▆', '▅', '▄', '▃', '▂', '▁' };
            String segmentedString = null;
            char ticker = '\0';
            for (char symbol : healDurationSymbols) {
                ticker = symbol;
                segmentedString = ApecUtils.segmentString(actionBar, String.valueOf(symbol), '+', symbol, 1, 1);
                if (segmentedString != null) break;
            }
            if (segmentedString != null) {
                healDuration = Integer.parseInt(ApecUtils.removeAllColourCodes(segmentedString).replace("+", ""));
                healDurationTicker = ticker;
            } else {
                healDuration = 0;
            }
        } catch (Exception err) {
            healDuration = 0;
        }

        // Parse mana
        try {
            String segmentedString = ApecUtils.segmentString(actionBar, String.valueOf(GameSymbols.MANA), '§', GameSymbols.MANA, 1, 1);
            if (segmentedString != null) {
                Tuple<Integer, Integer> t = parseStringFraction(ApecUtils.removeAllColourCodes(segmentedString));
                mana = t.getA();
                baseMana = t.getB();
                lastMana = mana;
                lastBaseMana = baseMana;
            } else {
                mana = lastMana;
                baseMana = lastBaseMana;
            }
        } catch (Exception err) {
            mana = lastMana;
            baseMana = lastBaseMana;
        }

        // Parse overflow mana
        try {
            String segmentedString = ApecUtils.segmentString(actionBar, String.valueOf(GameSymbols.OVERFLOW_MANA), '§', GameSymbols.OVERFLOW_MANA, 1, 1);
            if (segmentedString != null) {
                int value = Integer.parseInt(ApecUtils.removeAllColourCodes(segmentedString).replace(",", ""));
                overflow = value;
                if (baseOverflow < value) {
                    baseOverflow = value;
                }
                baseOverflow = this.baseOverflow;
            } else {
                overflow = 0;
                baseOverflow = 0;
                this.baseOverflow = 0;
            }
        } catch (Exception err) {
            overflow = 0;
            baseOverflow = 0;
        }

        // Parse defense
        try {
            String segmentedString = ApecUtils.segmentString(actionBar, String.valueOf(GameSymbols.DEFENSE), '§', GameSymbols.DEFENSE, 2, 1);
            if (segmentedString != null) {
                defence = Integer.parseInt(ApecUtils.removeAllColourCodes(segmentedString).replace(",", ""));
                lastDefence = defence;
            } else if (!isActionBarContainingSpecialText(actionBar)) {
                // Makes sure defense is actually 0 rather than replaced by racing text
                defence = 0;
                lastDefence = defence;
            } else {
                defence = lastDefence;
            }
        } catch (Exception err) {
            defence = lastDefence;
        }

        // Parse ability text
        try {
            String segmentedString = ApecUtils.segmentString(actionBar, ")", '§', ' ', 3, 1);
            if (segmentedString != null) {
                if (segmentedString.contains("-") && actionBar.contains(String.valueOf(GameSymbols.MANA))) {
                    abilityShown = true;
                    abilityText = segmentedString;
                }
            }
        } catch (Exception err) {
            abilityShown = false;
        }

        // Parse rift timer
        try {
            String segmentedString = ApecUtils.segmentString(actionBar, String.valueOf(GameSymbols.RIFT), '§', GameSymbols.RIFT, 1, 1);
            if (segmentedString != null) {
                riftTimer = ApecUtils.removeAllColourCodes(segmentedString);
            }
        } catch (Exception err) {
            riftTimer = "";
        }

        // Parse Kuudra tiered bonus
        try {
            kuudraTieredBonus = parseKuudraTieredBonus(actionBar);
        } catch (Exception err) {
            kuudraTieredBonus = "";
        }

        // Parse skill information
        try {
            String segmentString = ApecUtils.segmentString(actionBar, ")", '+', ' ', 1, 1, ApecUtils.SegmentationOptions.ALL_INSTANCES_LEFT);

            String inBetweenBrackets = null;

            if (segmentString != null) {
                inBetweenBrackets = ApecUtils.segmentString(segmentString, "(", '(', ')', 1, 1, ApecUtils.SegmentationOptions.TOTALLY_EXCLUSIVE);
            }

            if (inBetweenBrackets != null) {
                skillXpPercentage = parseSkillPercentage(inBetweenBrackets);
            }

            if (skillXpPercentage != -1f) {
                lastSkillXp = segmentString;
                skillInfo = ApecUtils.removeAllColourCodes(segmentString);
                skillShown = true;
            } else if (Apec.INSTANCE.settingsManager.getSettingState(SettingID.ALWAYS_SHOW_SKILL) && !lastSkillXp.isEmpty()) {
                skillInfo = ApecUtils.removeAllColourCodes(lastSkillXp);
                skillXpPercentage = parseSkillPercentage(ApecUtils.segmentString(lastSkillXp, "(", '(', ')', 1, 1, ApecUtils.SegmentationOptions.TOTALLY_EXCLUSIVE));
                skillShown = true;
            }
        } catch (Exception err) {
            skillShown = false;
        }

        // Parse drill fuel information
        try {
            String segmentFuel = ApecUtils.segmentString(actionBar, "Drill Fuel", '§', 'D', 1, 1);
            if (segmentFuel != null) {
                String cleanedFuel = ApecUtils.removeAllColourCodes(segmentFuel).trim();
                if (cleanedFuel.contains("/")) {
                    String[] fuelParts = cleanedFuel.split("\\s+");
                    for (String part : fuelParts) {
                        if (part.contains("/") && !part.contains("Drill")) {
                            String[] values = part.split("/");
                            if (values.length == 2) {
                                drillFuelRemaining = (int) ApecUtils.hypixelShortValueFormattingToFloat(values[0]);
                                drillFuelCapacity = (int) ApecUtils.hypixelShortValueFormattingToFloat(values[1]);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception err) {
            drillFuelRemaining = 0;
            drillFuelCapacity = 0;
        }


        // Create a new PlayerStats instance with all the updated values
        this.playerStats = new PlayerStats(
                hp,
                baseHp,
                healDuration,
                healDurationTicker,
                absorption,
                baseAbsorption,
                overflow,
                baseOverflow,
                mana,
                baseMana,
                defence,
                skillInfo,
                skillXpPercentage,
                skillShown,
                abilityShown,
                kuudraTieredBonus,
                drillFuelRemaining,
                drillFuelCapacity);
    }

    /**
     * Checks if the action bar contains special text that would replace defense value.
     *
     * @param actionBar The action bar text
     * @return True if the action bar contains special text
     */
    private boolean isActionBarContainingSpecialText(String actionBar) {
        return actionBar.contains(GameSymbols.END_RACE) ||
               actionBar.contains(GameSymbols.WOODS_RACING) ||
               actionBar.contains(GameSymbols.DPS) ||
               actionBar.contains(GameSymbols.SECOND) ||
               actionBar.contains(GameSymbols.SECRETS) ||
               actionBar.contains(GameSymbols.CHICKEN_RACE) ||
               actionBar.contains(GameSymbols.JUMP) ||
               actionBar.contains(GameSymbols.CRYSTAL_RACE) ||
               actionBar.contains(GameSymbols.GIANT_MUSHROOM) ||
               actionBar.contains(GameSymbols.PRECURSOR_RUINS);
    }

    /**
     * Parses Kuudra tiered bonus information from the action bar.
     *
     * @param actionBar The action bar text
     * @return Kuudra tiered bonus text or empty string if not found
     */
    private String parseKuudraTieredBonus(String actionBar) {
        // Check for Dominus
        String segmentedString = ApecUtils.segmentString(actionBar, String.valueOf(GameSymbols.DOMINUS), '§', GameSymbols.DOMINUS, 1, 1);
        if (segmentedString != null) {
            return GameSymbols.DOMINUS + " Dominus " + ApecUtils.removeAllColourCodes(segmentedString);
        }

        // Check for Arcane
        segmentedString = ApecUtils.segmentString(actionBar, String.valueOf(GameSymbols.ARCANE), '§', GameSymbols.ARCANE, 1, 1);
        if (segmentedString != null) {
            return GameSymbols.ARCANE + " Arcane " + ApecUtils.removeAllColourCodes(segmentedString);
        }

        // Check for Fervor
        segmentedString = ApecUtils.segmentString(actionBar, String.valueOf(GameSymbols.FERVOR), '§', GameSymbols.FERVOR, 1, 1);
        if (segmentedString != null) {
            return GameSymbols.FERVOR + " Fervor " + ApecUtils.removeAllColourCodes(segmentedString);
        }

        // Check for Spirit
        segmentedString = ApecUtils.segmentString(actionBar, String.valueOf(GameSymbols.SPIRIT), '§', GameSymbols.SPIRIT, 1, 1);
        if (segmentedString != null) {
            return GameSymbols.SPIRIT + " Spirit " + ApecUtils.removeAllColourCodes(segmentedString);
        }

        // Check for Hydra
        segmentedString = ApecUtils.segmentString(actionBar, String.valueOf(GameSymbols.HYDRA), '§', GameSymbols.HYDRA, 1, 1);
        if (segmentedString != null) {
            return GameSymbols.HYDRA + " Hydra " + ApecUtils.removeAllColourCodes(segmentedString);
        }

        return "";
    }

    /**
     * Processes other game data from the scoreboard and action bar.
     *
     * @param scoreboard The SkyBlock scoreboard
     * @return An OtherData instance with processed information
     */
    private OtherData processOtherData(SBScoreBoard scoreboard) {
        OtherData otherData = new OtherData();
        String actionBar = this.clientOverlay.getString();

        if (actionBar == null || isFromChat(actionBar)) {
            return otherData;
        }

        // Extract various special text segments from the action bar
        String endRace = ApecUtils.segmentString(actionBar, GameSymbols.END_RACE, '\u00a7', ' ', 2, 2);
        String woodRacing = ApecUtils.segmentString(actionBar, GameSymbols.WOODS_RACING, '\u00a7', ' ', 2, 2);
        String dps = ApecUtils.segmentString(actionBar, GameSymbols.DPS, '\u00a7', ' ', 1, 1);
        String sec = ApecUtils.segmentString(actionBar, GameSymbols.SECOND, '\u00a7', ' ', 1, 2);
        String secrets = ApecUtils.segmentString(actionBar, GameSymbols.SECRETS, '\u00a7', '\u00a7', 1, 1);
        String chickenRace = ApecUtils.segmentString(actionBar, GameSymbols.CHICKEN_RACE, '\u00a7', ' ', 2, 2);
        String jump = ApecUtils.segmentString(actionBar, GameSymbols.JUMP, '\u00a7', '\u00a7', 3, 1);
        String crystalRace = ApecUtils.segmentString(actionBar, GameSymbols.CRYSTAL_RACE, '\u00a7', ' ', 2, 2);
        String mushroomRace = ApecUtils.segmentString(actionBar, GameSymbols.GIANT_MUSHROOM, '\u00a7', ' ', 2, 2);
        String precursorRace = ApecUtils.segmentString(actionBar, GameSymbols.PRECURSOR_RUINS, '\u00a7', ' ', 2, 2);

        // Add a space if we're going to add race information
        if ((endRace != null || woodRacing != null || dps != null || sec != null) && !otherData.ExtraInfo.isEmpty()) {
            otherData.ExtraInfo.add(" ");
        }

        // Add all relevant information to the extra info list
        addToExtraInfoIfNotNull(otherData.ExtraInfo, endRace);
        addToExtraInfoIfNotNull(otherData.ExtraInfo, woodRacing);
        addToExtraInfoIfNotNull(otherData.ExtraInfo, dps);
        addToExtraInfoIfNotNull(otherData.ExtraInfo, secrets);

        // Handle revive message which has priority over "second" text
        if (actionBar.contains(GameSymbols.REVIVE)) {
            otherData.ExtraInfo.add(actionBar);
        } else if (sec != null) {
            otherData.ExtraInfo.add(sec);
        }

        addToExtraInfoIfNotNull(otherData.ExtraInfo, chickenRace);
        addToExtraInfoIfNotNull(otherData.ExtraInfo, jump);
        addToExtraInfoIfNotNull(otherData.ExtraInfo, crystalRace);
        addToExtraInfoIfNotNull(otherData.ExtraInfo, mushroomRace);
        addToExtraInfoIfNotNull(otherData.ExtraInfo, precursorRace);

        // Parse Armadillo energy if present
        if (actionBar.contains(GameSymbols.ARMADILLO)) {
            String segmentEnergy = ApecUtils.segmentString(actionBar, "/", '\u00a7', '\0', 2, 1, ApecUtils.SegmentationOptions.TOTALLY_INCLUSIVE);
            if (segmentEnergy != null) {
                String[] values = ApecUtils.removeAllColourCodes(segmentEnergy).split("/");
                otherData.ArmadilloEnergy = Float.parseFloat(values[0]);
                otherData.ArmadilloBaseEnergy = Float.parseFloat(values[1]);
            }
        }

        // Parse treasure metal detector info
        if (actionBar.contains(GameSymbols.TREASURE)) {
            String segmentedString = ApecUtils.segmentString(actionBar, GameSymbols.TREASURE, '\u00a7', 'm', 2, 1, ApecUtils.SegmentationOptions.TOTALLY_INCLUSIVE);
            if (segmentedString != null) {
                otherData.ExtraInfo.add(segmentedString);
            }
        }

        // Add current events
        otherData.currentEvents = getCurrentEvents(scoreboard);

        return otherData;
    }

    /**
     * Adds a string to the extra info list if it's not null.
     *
     * @param list The list to add to
     * @param value The value to add if not null
     */
    private void addToExtraInfoIfNotNull(List<String> list, String value) {
        if (value != null) {
            list.add(value);
        }
    }

    /**
     * Determines current events based on scoreboard data.
     *
     * @param scoreboard The SkyBlock scoreboard
     * @return A list of current events
     */
    private ArrayList<EventIDs> getCurrentEvents(SBScoreBoard scoreboard) {
        ArrayList<EventIDs> events = new ArrayList<>();

        if (Apec.INSTANCE.settingsManager.getSettingState(SettingID.SHOW_WARNING)) {
            try {
                // Check if inventory is full
                if (mc.player != null && isInventoryFull()) {
                    events.add(EventIDs.INV_FULL);
                }

                // Check coin count for warning
                if (scoreboard.purse() != null) {
                    String purse = ApecUtils.removeAllColourCodes(scoreboard.purse().getString());
                    if (purse.contains("(")) {
                        purse = purse.substring(0, purse.indexOf("("));
                    }
                    purse = ApecUtils.removeNonNumericalChars(purse);
                    if (!purse.isEmpty()) {
                        if (Float.parseFloat(purse) >= 5000000f && !usesPiggyBank) {
                            events.add(EventIDs.COIN_COUNT);
                        }
                    }
                }

                // Check for server reboot
                if (!stringScoreboard.isEmpty() &&
                    ApecUtils.removeAllColourCodes(stringScoreboard.get(stringScoreboard.size() - 1))
                             .contains("Server closing")) {
                    events.add(EventIDs.SERVER_REBOOT);
                }

                // Check ping
                if (mc.player != null) {
                    int pingThreshold = 80;
                    int ping = Optional.ofNullable(mc.player.connection.getPlayerInfo(mc.player.getGameProfile().getId()))
                                      .map(info -> info.getLatency())
                                      .orElse(0);
                    if (ping > pingThreshold) {
                        events.add(EventIDs.HIGH_PING);
                    }
                }
            } catch (Exception e) {
                // Error handling is silent by design
            }
        }

        return events;
    }

    /**
     * Determines if the player's inventory is full.
     *
     * @return True if inventory is full
     */
    private boolean isInventoryFull() {
        if (mc.player == null) {
            return false;
        }
        Inventory inventory = mc.player.getInventory();
        return inventory.getFreeSlot() == -1;
    }

    /**
     * Parses skill percentage from skill information text.
     *
     * @param skillInfo The text between brackets of the skill xp string
     * @return The percentage of completion until next skill level (-1f if invalid)
     */
    private float parseSkillPercentage(String skillInfo) {
        if (skillInfo == null) {
            return -1f;
        }

        if (skillInfo.contains("%")) {
            skillInfo = skillInfo.replace("%", "");
            try {
                return Float.parseFloat(skillInfo) / 100f;
            } catch (NumberFormatException e) {
                return -1f;
            }
        } else if (skillInfo.contains("/")) {
            String[] twoValues = skillInfo.split("/");
            try {
                float first = ApecUtils.hypixelShortValueFormattingToFloat(twoValues[0]);
                float second = ApecUtils.hypixelShortValueFormattingToFloat(twoValues[1]);
                return first / second;
            } catch (Exception e) {
                return -1f;
            }
        }
        return -1f;
    }

    /**
     * Parses a fraction string into two integers.
     *
     * @param s The string fraction in format "number/number"
     * @return A tuple with the numerator and denominator
     */
    private Tuple<Integer, Integer> parseStringFraction(String s) {
        try {
            String[] tempSplit = s.replace(",", "").split("/");
            return new Tuple<>(Integer.parseInt(tempSplit[0]), Integer.parseInt(tempSplit[1]));
        } catch (Exception e) {
            return new Tuple<>(0, 0);
        }
    }

    /**
     * Determines if a string represents a SkyBlock date.
     *
     * @param s The string to check
     * @return True if the string is a date
     */
    private boolean isDate(String s) {
        return (ApecUtils.containedByCharSequence(s, "Autumn") ||
                ApecUtils.containedByCharSequence(s, "Winter") ||
                ApecUtils.containedByCharSequence(s, "Spring") ||
                ApecUtils.containedByCharSequence(s, "Summer")) &&
               (ApecUtils.containedByCharSequence(s, "st") ||
                ApecUtils.containedByCharSequence(s, "nd") ||
                ApecUtils.containedByCharSequence(s, "rd") ||
                ApecUtils.containedByCharSequence(s, "th"));
    }

    /**
     * Determines if a string represents SkyBlock time.
     *
     * @param s The string to check
     * @return True if the string is a time
     */
    private boolean isTime(String s) {
        return (ApecUtils.containedByCharSequence(s, "am") || ApecUtils.containedByCharSequence(s, "pm")) &&
               (ApecUtils.containedByCharSequence(s, ":")) &&
               (ApecUtils.containedByCharSequence(s, "☽") || ApecUtils.containedByCharSequence(s, "☀"));
    }

    /**
     * Determines if a message is from chat rather than the action bar.
     *
     * @param s The string to check
     * @return True if the string is from chat
     */
    private boolean isFromChat(String s) {
        return (s.contains("[") && s.contains("]")) || (s.startsWith("\u00a77") && s.contains(": "));
    }

    /**
     * Gets the scoreboard title from the client.
     *
     * @return The scoreboard title component
     */
    private Component getScoreboardTitle() {
        try {
            if (mc.level == null) {
                return Component.empty();
            }

            Scoreboard scoreboard = mc.level.getScoreboard();
            Objective displayObjective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);

            if (displayObjective == null) {
                return Component.empty();
            }

            return displayObjective.getDisplayName();
        } catch (Exception e) {
            return Component.empty();
        }
    }

    /**
     * Collects and parses the scoreboard lines from the client.
     */
    private void collectScoreboardLines() {
        this.stringScoreboard.clear();
        this.componentScoreboard.clear();

        var player = mc.player;
        if (player == null) {
            return;
        }

        Scoreboard scoreboard = player.getScoreboard();
        Objective displayObjective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);

        if (displayObjective == null) {
            return;
        }

        ObjectArrayList<Component> componentLine = new ObjectArrayList<>();
        ObjectArrayList<String> stringLine = new ObjectArrayList<>();

        Collection<PlayerScoreEntry> scores = scoreboard.listPlayerScores(displayObjective);
        List<PlayerScoreEntry> list = scores.stream().filter(input -> input != null && !input.isHidden()).collect(Collectors.toList());

        list.sort((a, b) -> Integer.compare(b.value(), a.value()));

        if (list.size() > 15) {
            scores = Lists.newArrayList(Iterables.skip(list, scores.size() - 15));
        } else {
            scores = list;
        }

        for (PlayerScoreEntry score : scores) {
            PlayerTeam team = scoreboard.getPlayersTeam(score.owner());

            if (team != null) {
                Component component = Component.empty().append(team.getPlayerPrefix().copy()).append(team.getPlayerSuffix().copy());
                String string = team.getPlayerPrefix().getString() + team.getPlayerSuffix().getString();
                if (!string.trim().isEmpty()) {
                    componentLine.add(component);
                    stringLine.add(string);
                }
            }
        }

        this.stringScoreboard.addAll(stringLine);
        this.componentScoreboard.addAll(componentLine);


    }

    /**
     * Determines if a specific text should have an empty line before it in the left side display.
     *
     * @param s The text to check
     * @return True if it should have a space before
     */
    public boolean shouldHaveSpaceBefore(String s) {
        return ApecUtils.containedByCharSequence(s, "Objective") ||
               ApecUtils.containedByCharSequence(s, "Contest") ||
               ApecUtils.containedByCharSequence(s, "Year") ||
               ApecUtils.containedByCharSequence(s, "Zoo") ||
               ApecUtils.containedByCharSequence(s, "Festival") ||
               ApecUtils.containedByCharSequence(s, "Season") ||
               ApecUtils.containedByCharSequence(s, "Election") ||
               ApecUtils.containedByCharSequence(s, "Slayer") ||
               ApecUtils.containedByCharSequence(s, "Keys") ||
               ApecUtils.containedByCharSequence(s, "Time Elapsed") ||
               ApecUtils.containedByCharSequence(s, "Starting in:") ||
               ApecUtils.containedByCharSequence(s, "Wave");
    }

    /**
     * Determines if a specific text should have an empty line after it in the left side display.
     *
     * @param s The text to check
     * @return True if it should have a space after
     */
    public boolean shouldHaveSpaceAfter(String s) {
        return ApecUtils.containedByCharSequence(s, "Dungeon Cleared");
    }

    /**
     * Class representing additional SkyBlock data.
     */
    public class OtherData {
        public ArrayList<String> ExtraInfo = new ArrayList<>();
        public ArrayList<EventIDs> currentEvents = new ArrayList<>();
        public float ArmadilloEnergy;
        public float ArmadilloBaseEnergy;
    }

    // SBAPI implementation methods

    @Override
    public boolean isOnSkyblock() {
        return onSkyblock;
    }

    @Override
    public boolean isInRift() {
        return isInRift;
    }

    @Override
    public boolean isInDungeon() {
        return isInDungeon;
    }

    @Override
    public SBScoreBoard getScoreboard() {
        return scoreboard;
    }

    @Override
    public boolean usesPiggyBank() {
        return usesPiggyBank;
    }

    @Override
    public PlayerStats getPlayerStats() {
        return playerStats;
    }

    @Override
    public Component getTabListFooter() {
        return clientTabFooter;
    }

    /**
     * Gets other data related to SkyBlock.
     *
     * @return The OtherData instance
     */
    public OtherData getOtherData() {
        return otherData;
    }
}
