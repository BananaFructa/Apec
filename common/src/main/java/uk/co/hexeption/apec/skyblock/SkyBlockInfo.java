package uk.co.hexeption.apec.skyblock;


import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.client.ClientSystemMessageEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.scores.*;
import uk.co.hexeption.apec.EventIDs;
import uk.co.hexeption.apec.MC;
import uk.co.hexeption.apec.api.SBAPI;
import uk.co.hexeption.apec.mixins.accessors.PlayerTabOverlayAccessor;
import uk.co.hexeption.apec.utils.ApecUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SkyBlockInfo implements SBAPI, MC {

    private SBScoreBoard scoreboard = SBScoreBoard.EMPTY;
    private boolean onSkyblock;
    private List<String> scoreboardLines = new ArrayList<>();
    private boolean usesPiggyBank;

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

    public void init() {
        ClientTickEvent.CLIENT_PRE.register(this::clientTick);
        ClientSystemMessageEvent.RECEIVED.register(this::clientChatMessage);
    }

    private CompoundEventResult<Component> clientChatMessage(Component component) {
        if (component.getString().contains("❤") || component.getString().contains("✎") || component.getString().contains("Revive") || component.getString().contains("CHICKEN RACING") || component.getString().contains("Armadillo")){
            this.clientOverlay = component;
        }
        return CompoundEventResult.pass();
    }

    private void clientTick(Minecraft minecraft) {
        Component clientScoreboardTitle = getClientScoreboardTitle();
        if (!clientScoreboardTitle.getString().isEmpty()) {
            this.onSkyblock = clientScoreboardTitle.getString().contains("SKYBLOCK");

            this.scoreboardLines = getScoreboardLines();

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
        String zone = "";
        String purse = "";
        String bits = "";
        String gameType = "";

        for (String e : this.scoreboardLines) {
            // IRL Date and Server Shard
            if (ApecUtils.isContainedIn(e, "//")) {
                irl_date = ApecUtils.removeFirstSpaces(e).split(" ")[0];     // "05/04/23"
                serverShard = ApecUtils.removeFirstSpaces(e).split(" ")[1];  // "m80DE"
            }

            // Date
            if (isDate(e)) {
                date = ApecUtils.removeFirstSpaces(e); // "Late Summer 8th"
            }

            // Time
            if (isTime(e)) {
                hour = ApecUtils.removeFirstSpaces(e); // "11:30pm ☽"
            }

            // Zone
            if (ApecUtils.containedByCharSequence(e, "⏣")) {
                zone = ApecUtils.removeFirstSpaces(e); // "⏣ Auction House"
            }

            if (ApecUtils.containedByCharSequence(e, "Purse: ")) {
                purse = ApecUtils.removeFirstSpaces(e); // "Purse: 0.0"
                this.usesPiggyBank = false;
            }

            if (ApecUtils.containedByCharSequence(e, "Piggy: ")) {
                purse = ApecUtils.removeFirstSpaces(e); // "Piggy: 0.0"
                this.usesPiggyBank = true;
            }

            if (ApecUtils.containedByCharSequence(e, "Bits: ")) {
                bits = ApecUtils.removeFirstSpaces(e); // "Bits: 0.0"
            }

            // Game Type (Ironman, Stranded, Bingo)
            if (ApecUtils.containedByCharSequence(e, "♲") || ApecUtils.containedByCharSequence(e, "☀ Stranded") || ApecUtils.containedByCharSequence(e, "Ⓑ")) {
                gameType = ApecUtils.removeFirstSpaces(e); // "♲ Ironman"
            }
        }

        this.scoreboard = new SBScoreBoard(
                serverShard,
                purse,
                bits,
                new ArrayList<>(),
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
            char[] healDurationSymbols = new char[]{'▆', '▅', '▄', '▃', '▂', '▁'};
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
                play_defence = Integer.parseInt(ApecUtils.removeAllColourCodes(segmentedString));
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

    private List<String> getScoreboardLines() {
        List<String> lines = new ArrayList<>();
        Scoreboard scoreboard = mc.level.getScoreboard();
        if (scoreboard == null) return lines;

        Objective displayObjective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
        if (displayObjective == null) return lines;

        Collection<PlayerScoreEntry> playerScores = scoreboard.listPlayerScores(displayObjective);
        ArrayList<PlayerScoreEntry> scores = new ArrayList<>(playerScores);

        scores.sort((o1, o2) -> {
            if (o1.value() > o2.value()) return -1;
            if (o1.value() < o2.value()) return 1;
            return 0;
        });

        for (PlayerScoreEntry score : scores) {
            PlayerTeam playerTeam = scoreboard.getPlayersTeam(score.owner());
            if (playerTeam != null) {

                Component component = PlayerTeam.formatNameForTeam(playerTeam, Component.empty());

                lines.add(component.getString());
            }
        }
        return lines;
    }

    @Override
    public boolean isOnSkyblock() {
        return onSkyblock;
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

        public ArrayList<String> ExtraInfo  = new ArrayList<String>();
        public ArrayList<EventIDs> currentEvents = new ArrayList<EventIDs>();
        public float ArmadilloEnergy;
        public float ArmadilloBaseEnergy;

    }

    public OtherData getOtherData() {
        return otherData;
    }
}
