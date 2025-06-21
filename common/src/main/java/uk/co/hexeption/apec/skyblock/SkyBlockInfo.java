package uk.co.hexeption.apec.skyblock;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.client.ClientChatEvent;
import dev.architectury.event.events.client.ClientSystemMessageEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import uk.co.hexeption.apec.EventIDs;
import uk.co.hexeption.apec.MC;
import uk.co.hexeption.apec.api.SBAPI;
import uk.co.hexeption.apec.mixins.accessors.PlayerTabOverlayAccessor;
import uk.co.hexeption.apec.utils.ApecUtils;

public class SkyBlockInfo implements SBAPI, MC {

    private SBScoreBoard scoreboard = SBScoreBoard.EMPTY;
    private boolean onSkyblock;

    private boolean usesPiggyBank;

    // Cache for purse and bits components
    private Component cachedPurse = Component.empty();
    private Component cachedBits = Component.empty();

    private ObjectArrayList<String> stringScoreboard = new ObjectArrayList<>();
    private ObjectArrayList<Component> componenetScoreboard = new ObjectArrayList<>();

    private PlayerStats playerStats = PlayerStats.EMPTY;
    private Component clientOverlay = Component.empty();
    private Component clientTabFooter = Component.empty();

    // Private fields for the overlay parsing
    private int lastHp = 1, lastBaseHp = 1;
    private int lastMn = 1;
    private int lastBaseMn = 1;
    private int lastDefence = 0;
    private int baseAp = 0;
    private int lastAp = 1, lastBaseAp = 1;
    private int baseOp = 1;

    private final String endRaceSymbol = "THE END RACE";
    private final String woodRacingSymbol = "WOODS RACING";
    private final String dpsSymbol = "DPS";
    private final String secSymbol = "second";
    private final String secretSymbol = "Secrets";
    private final String chickenRaceSymbol = "CHICKEN RACING";
    private final String jumpSymbol = "JUMP";
    private final String crystalRaceSymbol = "CRYSTAL CORE RACE";
    private final String giantMushroomSymbol = "GIANT MUSHROOM RACE";
    private final String precursorRuinsSymbol = "PRECURSOR RUINS RACE";
    private final String reviveSymbol = "Revive";
    private final String armadilloName = "Armadillo";
    private final String treasureMetalDetectorSymbol = "TREASURE:";
    private OtherData otherData;

    private boolean isInRift = false;
    private boolean isInDungeon = false;

    private final char riftSymbol = 'ф';

    public void init() {

        ClientTickEvent.CLIENT_PRE.register(this::clientTick);
        // Stupid hack as ClientSystemMessageEvent not working on NeoForge
        if (Platform.isFabric()) {
            ClientSystemMessageEvent.RECEIVED.register((message) -> clientChatMessage(null, message));
        }
        if (Platform.isNeoForge()) {
            ClientChatEvent.RECEIVED.register(this::clientChatMessage);
        }
    }

    private CompoundEventResult<Component> clientChatMessage(ChatType.Bound bound, Component component) {

        if (component.getString().contains("❤") || component.getString().contains("✎") || component.getString().contains("Revive") || component.getString().contains("CHICKEN RACING") || component.getString().contains("Armadillo")) {
            this.clientOverlay = component;
        }

        this.isInRift = component.getString().contains(String.valueOf(riftSymbol));

        return CompoundEventResult.pass();
    }

    private void clientTick(Minecraft minecraft) {

        Component clientScoreboardTitle = getClientScoreboardTitle();
        if (!clientScoreboardTitle.getString().isEmpty()) {
            this.onSkyblock = clientScoreboardTitle.getString().contains("SKYBLOCK");

            getScoreboardLines();

            parseScoreboardData();

            parsePlayerStats();

            this.otherData = this.ProcessOtherData(scoreboard);

            this.clientTabFooter = ((PlayerTabOverlayAccessor) mc.gui.getTabList()).getFooter();
        }
    }

    private void parseScoreboardData() {

        String irl_date = "";
        String serverShard = "";
        String date = "";
        String hour = "";
        Component zone = Component.empty();
        Component purse = Component.empty();
        Component bits = Component.empty();
        Component gameType = Component.empty();
        ArrayList<Component> extra = new ArrayList<>();

        this.isInDungeon = false;

        for (Component component : this.componenetScoreboard) {
            String line = component.getString();

            if(ApecUtils.isContainedIn(line, "The Catacombs")){
                this.isInDungeon = true;
            }

            if (ApecUtils.isContainedIn(line, "//")) {
                irl_date = ApecUtils.removeFirstSpaces(line).split(" ")[0];
                serverShard = ApecUtils.removeFirstSpaces(line).split(" ")[1];
            } else if (ApecUtils.containedByCharSequence(line, "♲") || ApecUtils.containedByCharSequence(line, "☀ Stranded") || ApecUtils.containedByCharSequence(line, "Ⓑ")) {
                gameType = component;
            } else if (isDate(line)) {
                date = ApecUtils.removeFirstSpaces(line);
            } else if (isTime(line)) {
                hour = ApecUtils.removeFirstSpaces(line);
            } else if (ApecUtils.containedByCharSequence(line, "⏣")) {
                zone = component;
            } else if (ApecUtils.containedByCharSequence(line, "Purse: ")) {
                purse = component;
                this.cachedPurse = component; // Cache the purse component
                this.usesPiggyBank = false;
            } else if (ApecUtils.containedByCharSequence(line, "Piggy: ")) {
                purse = component;
                this.cachedPurse = component; // Cache the purse component
                this.usesPiggyBank = true;
            } else if (ApecUtils.containedByCharSequence(line, "Bits: ")) {
                bits = component;
                this.cachedBits = component; // Cache the bits component
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

        this.scoreboard = new SBScoreBoard(
                serverShard,
                purse,
                bits,
                extra,
                zone,
                date,
                hour,
                irl_date,
                getClientScoreboardTitle().getString(),
                gameType);

    }

    private void parsePlayerStats() {

        // 2,964/2,664❤     683❈ Defense     327/327✎ Mana
        String actionBar = this.clientOverlay.getString();

        int play_hp = 0;
        int play_base_hp = 0;
        int play_absorption = 0;
        int play_base_absorption = 0;
        int play_heal_duration = 0;
        char play_heal_duration_ticker = 0;
        int play_mp = 0;
        int play_base_mp = 0;
        int play_overflow = 0;
        int play_base_overflow = 0;
        int play_defence = 0;

        // HP
        {
            String segment = ApecUtils.segmentString(actionBar, "❤", '§', '❤', 1, 1);
            if (segment != null) {
                Tuple<Integer, Integer> hpTuple = formatStringFractI(ApecUtils.removeAllColourCodes(segment));
                play_hp = hpTuple.getA();
                play_base_hp = hpTuple.getB();

                if (play_hp > play_base_hp) {
                    play_absorption = play_hp - play_base_hp;
                    play_hp = play_base_hp;
                } else {
                    play_absorption = 0;
                    play_base_absorption = 0;
                }
                if (play_absorption > baseAp) {
                    baseAp = play_absorption;
                }
                play_base_absorption = baseAp;

                lastAp = play_absorption;
                lastBaseAp = play_base_absorption;

                lastHp = play_hp;
                lastBaseHp = play_base_hp;
            } else {
                play_hp = lastHp;
                play_base_hp = lastBaseHp;
                play_absorption = lastAp;
                play_base_absorption = lastBaseAp;
            }
        }

        // Heal Duration
        {
            char[] healDurationSymbols = new char[] { '▆', '▅', '▄', '▃', '▂', '▁' };
            String segmentedSrtring = null;
            char ticker = '\0';
            for (char _c : healDurationSymbols) {
                ticker = _c;
                segmentedSrtring = ApecUtils.segmentString(actionBar, String.valueOf(_c), '+', _c, 1, 1);
                if (segmentedSrtring != null) break;
            }
            if (segmentedSrtring != null) {
                play_heal_duration = Integer.parseInt(ApecUtils.removeAllColourCodes(segmentedSrtring).replace("+", ""));
                play_heal_duration_ticker = ticker;
            } else {
                play_heal_duration = 0;
            }
        }

        // Mana
        {
            String segmentedString = ApecUtils.segmentString(actionBar, String.valueOf('✎'), '§', '✎', 1, 1);
            if (segmentedString != null) {
                Tuple<Integer, Integer> t = formatStringFractI(ApecUtils.removeAllColourCodes(segmentedString));
                play_mp = t.getA();
                play_base_mp = t.getB();
                lastMn = play_mp;
                lastBaseMn = play_base_mp;
            } else {
                play_mp = lastMn;
                play_base_mp = lastBaseMn;
            }
        }

        // Overflow mana
        {
            String segmentedString = ApecUtils.segmentString(actionBar, String.valueOf('ʬ'), '\u00a7', 'ʬ', 1, 1);
            if (segmentedString != null) {
                int value = Integer.parseInt(ApecUtils.removeAllColourCodes(segmentedString.replace(",", "")));
                play_overflow = value;
                if (baseOp < value) {
                    baseOp = value;
                }
                play_base_overflow = baseOp;
            } else {
                play_overflow = 0;
                play_base_overflow = 0;
                baseOp = 0;
            }
        }

        {
            String segmentedString = ApecUtils.segmentString(actionBar, String.valueOf('❈'), '§', '❈', 2, 1);
            if (segmentedString != null) {
                play_defence = Integer.parseInt(ApecUtils.removeAllColourCodes(segmentedString.replace(",", "")));
                lastDefence = play_defence;
            } else {
                play_defence = lastDefence;
            }

        }

        this.playerStats = new PlayerStats(
                play_hp,
                play_base_hp,
                play_heal_duration,
                play_heal_duration_ticker,
                play_absorption,
                play_base_absorption,
                play_overflow,
                play_base_overflow,
                play_mp,
                play_base_mp,
                play_defence,
                "",
                0,
                false,
                false);
    }

    private OtherData ProcessOtherData(SBScoreBoard sd) {

        OtherData otherData = new OtherData();
        String actionBar = this.clientOverlay.getString();
        if (actionBar == null ? true : isFromChat(actionBar)) return otherData;
        String endRace = ApecUtils.segmentString(actionBar, endRaceSymbol, '\u00a7', ' ', 2, 2);
        String woodRacing = ApecUtils.segmentString(actionBar, woodRacingSymbol, '\u00a7', ' ', 2, 2);
        String dps = ApecUtils.segmentString(actionBar, dpsSymbol, '\u00a7', ' ', 1, 1);
        String sec = ApecUtils.segmentString(actionBar, secSymbol, '\u00a7', ' ', 1, 2);
        String secrets = ApecUtils.segmentString(actionBar, secretSymbol, '\u00a7', '\u00a7', 1, 1);
        String chickenRace = ApecUtils.segmentString(actionBar, chickenRaceSymbol, '\u00a7', ' ', 2, 2);
        String jump = ApecUtils.segmentString(actionBar, jumpSymbol, '\u00a7', '\u00a7', 3, 1);
        String crystalRace = ApecUtils.segmentString(actionBar, crystalRaceSymbol, '\u00a7', ' ', 2, 2);
        String mushroomRace = ApecUtils.segmentString(actionBar, giantMushroomSymbol, '\u00a7', ' ', 2, 2);
        String precursorRace = ApecUtils.segmentString(actionBar, precursorRuinsSymbol, '\u00a7', ' ', 2, 2);

        if ((endRace != null || woodRacing != null || dps != null || sec != null) && !otherData.ExtraInfo.isEmpty())
            otherData.ExtraInfo.add(" ");

        if (endRace != null) otherData.ExtraInfo.add(endRace);
        if (woodRacing != null) otherData.ExtraInfo.add(woodRacing);
        if (dps != null) otherData.ExtraInfo.add(dps);
        if (secrets != null) otherData.ExtraInfo.add(secrets);
        // The condition is like this to make sure the actionData is not null
        if (actionBar.contains("Revive")) {
            otherData.ExtraInfo.add(actionBar);
        }
        // The revive message also contais the word second so we have to be sure is not that one
        else if (sec != null) otherData.ExtraInfo.add(sec);
        if (chickenRace != null) otherData.ExtraInfo.add(chickenRace);
        if (jump != null) otherData.ExtraInfo.add(jump);
        if (crystalRace != null) otherData.ExtraInfo.add(crystalRace);
        if (mushroomRace != null) otherData.ExtraInfo.add(mushroomRace);
        if (precursorRace != null) otherData.ExtraInfo.add(precursorRace);

        if (actionBar.contains(armadilloName)) {
            String segmentEnergy = ApecUtils.segmentString(actionBar, "/", '\u00a7', '\0' /*placeholder*/, 2, 1, ApecUtils.SegmentationOptions.TOTALLY_INCLUSIVE);
            if (segmentEnergy != null) {
                String[] values = ApecUtils.removeAllColourCodes(segmentEnergy).split("/");
                otherData.ArmadilloEnergy = Float.parseFloat(values[0]);
                otherData.ArmadilloBaseEnergy = Float.parseFloat(values[1]);
            }
        }

        if (actionBar.contains(treasureMetalDetectorSymbol)) {
            String segmentedString = ApecUtils.segmentString(actionBar, treasureMetalDetectorSymbol, '\u00a7', 'm', 2, 1, ApecUtils.SegmentationOptions.TOTALLY_INCLUSIVE);
            if (segmentedString != null) {
                otherData.ExtraInfo.add(segmentedString);
            }
        }

//        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_POTIONS_EFFECTS)) {
//
//            List<String> effects = getPlayerEffects();
//            if (!effects.isEmpty()) {
//                if (!otherData.ExtraInfo.isEmpty()) otherData.ExtraInfo.add(" ");
//                otherData.ExtraInfo.addAll(effects);
//            }
//
//        }
//
//        otherData.currentEvents = getEvents(sd);

        return otherData;

    }

    public Tuple<Integer, Integer> formatStringFractI(String s) {

        String[] tempSplit = s.replace(",", "").split("/");
        return new Tuple<>(Integer.parseInt(tempSplit[0]), Integer.parseInt(tempSplit[1]));
    }

    private boolean isDate(String s) {

        return (ApecUtils.containedByCharSequence(s, "Autumn") || ApecUtils.containedByCharSequence(s, "Winter") || ApecUtils.containedByCharSequence(s, "Spring") || ApecUtils.containedByCharSequence(s, "Summer")) && (ApecUtils.containedByCharSequence(s, "st") || ApecUtils.containedByCharSequence(s, "nd") || ApecUtils.containedByCharSequence(s, "rd") || ApecUtils.containedByCharSequence(s, "th"));
    }

    private boolean isTime(String s) {

        return (ApecUtils.containedByCharSequence(s, "am") || ApecUtils.containedByCharSequence(s, "pm")) &&
                (ApecUtils.containedByCharSequence(s, ":")) &&
                (ApecUtils.containedByCharSequence(s, "☽") || ApecUtils.containedByCharSequence(s, "☀"));
    }

    public boolean isFromChat(String s) {

        return (s.contains("[") && s.contains("]")) || (s.startsWith("\u00a77") && s.contains(": "));
    }

    private Component getClientScoreboardTitle() {

        try {
            Scoreboard scoreboard = mc.level.getScoreboard();
            Objective displayObjective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
            return displayObjective.getDisplayName();
        } catch (Exception e) {
            return Component.empty();
        }
    }

    private void getScoreboardLines() {

        this.stringScoreboard.clear();
        this.componenetScoreboard.clear();

        var player = mc.player;
        if (player == null) {
            return;
        }

        Scoreboard scoreboard = player.getScoreboard();
        Objective displayObjective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
        ObjectArrayList<Component> componentLine = new ObjectArrayList<>();
        ObjectArrayList<String> stringLine = new ObjectArrayList<>();

        for (ScoreHolder scoreHolder : scoreboard.getTrackedPlayers()) {
            if (scoreboard.listPlayerScores(scoreHolder).containsKey(displayObjective)) {
                PlayerTeam team = scoreboard.getPlayersTeam(scoreHolder.getScoreboardName());

                if (team != null) {
                    Component component = Component.empty().append(team.getPlayerPrefix().copy()).append(team.getPlayerSuffix().copy());
                    String string = team.getPlayerPrefix().getString() + team.getPlayerSuffix().getString();
                    if (!string.trim().isEmpty()) {
                        componentLine.add(component);
                        stringLine.add(string);
                    }
                }
            }
        }

        if (displayObjective != null) {
            Collections.reverse(stringLine);
            Collections.reverse(componentLine);
        }

        this.stringScoreboard.addAll(stringLine);
        this.componenetScoreboard.addAll(componentLine);
    }

    /**
     * @param s = Input string
     * @return Return true if the specific text should have an empty line before in the left side display
     */

    public boolean ShouldHaveSpaceBefore(String s) {

        return ApecUtils.containedByCharSequence(s, "Objective") || //Objectives
                ApecUtils.containedByCharSequence(s, "Contest") || // Jacob's contest
                ApecUtils.containedByCharSequence(s, "Year") || // New year and the vote thing
                ApecUtils.containedByCharSequence(s, "Zoo") || // Traveling Zoo
                ApecUtils.containedByCharSequence(s, "Festival") || // Spooky Festival
                ApecUtils.containedByCharSequence(s, "Season") || // Jerry season
                ApecUtils.containedByCharSequence(s, "Election") || // Idk at this point im just putting some things from the wiki
                ApecUtils.containedByCharSequence(s, "Slayer") || // Slayer quest
                ApecUtils.containedByCharSequence(s, "Keys") || // Keys in dungeons
                ApecUtils.containedByCharSequence(s, "Time Elapsed") || // time elapsed dungeons
                ApecUtils.containedByCharSequence(s, "Starting in:") ||
                ApecUtils.containedByCharSequence(s, "Wave") ||
                ApecUtils.containedByCharSequence(s, "Festival");
    }

    /**
     * @param s = Input string
     * @return Return true if the specific text should have an empty line after in the left side display
     */

    public boolean ShouldHaveSpaceAfter(String s) {

        return ApecUtils.containedByCharSequence(s, "Dungeon Cleared"); // Dungeon cleared in dungeons
    }

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

    public class OtherData {

        public ArrayList<String> ExtraInfo = new ArrayList<String>();
        public ArrayList<EventIDs> currentEvents = new ArrayList<EventIDs>();
        public float ArmadilloEnergy;
        public float ArmadilloBaseEnergy;

    }

    public OtherData getOtherData() {

        return otherData;
    }

}
