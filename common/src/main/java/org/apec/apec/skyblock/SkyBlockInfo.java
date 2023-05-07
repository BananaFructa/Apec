package org.apec.apec.skyblock;

import me.zero.alpine.event.EventPriority;
import me.zero.alpine.listener.EventSubscriber;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import org.apec.apec.MC;
import org.apec.apec.api.SBAPI;
import org.apec.apec.events.ChatMessage;
import org.apec.apec.events.ClientTick;
import org.apec.apec.mixins.accessors.PlayerTabOverlayAccessor;
import org.apec.apec.utils.ApecUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SkyBlockInfo implements SBAPI, EventSubscriber, MC {

    private static final SkyBlockInfo INSTANCE = new SkyBlockInfo();

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
    private final int lastDefence = 0;
    private int baseAp = 0;
    private int lastAp = 1, lastBaseAp = 1;
    private int baseOp = 1;

    @Subscribe
    Listener<ClientTick> clientTickListener = new Listener<>(event -> {
        Component clientScoreboardTitle = getClientScoreboardTitle();
        if (!clientScoreboardTitle.getString().isEmpty()) {
            this.onSkyblock = clientScoreboardTitle.getString().contains("SKYBLOCK");

            this.scoreboardLines = getScoreboardLines();

            parseScoreboardData();

            parsePlayerStats();

            this.clientTabFooter = ((PlayerTabOverlayAccessor) mc.gui.getTabList()).getFooter();
        }
    }, EventPriority.LOWEST);

    @Subscribe
    Listener<ChatMessage> chatMessageListener = new Listener<>(event -> {
        if (event.isOverlay()) {
            if (event.component().getString().contains("❤")) {
                this.clientOverlay = event.component();
            }
        }
    }, EventPriority.LOWEST);


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
                0,
                "",
                0,
                false,
                false);
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

    private Component getClientScoreboardTitle() {
        try {
            Scoreboard scoreboard = mc.level.getScoreboard();
            Objective displayObjective = scoreboard.getDisplayObjective(1);
            return displayObjective.getDisplayName();
        } catch (Exception e) {
            return Component.empty();
        }
    }

    private List<String> getScoreboardLines() {
        List<String> lines = new ArrayList<>();
        Scoreboard scoreboard = mc.level.getScoreboard();
        if (scoreboard == null) return lines;

        Objective displayObjective = scoreboard.getDisplayObjective(1);
        if (displayObjective == null) return lines;

        Collection<Score> playerScores = scoreboard.getPlayerScores(displayObjective);
        List<Score> scores = new ArrayList<>(playerScores);

        scores.sort((o1, o2) -> {
            if (o1.getScore() > o2.getScore()) return -1;
            if (o1.getScore() < o2.getScore()) return 1;
            return 0;
        });

        for (Score score : scores) {
            PlayerTeam playerTeam = scoreboard.getPlayersTeam(score.getOwner());
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

    public static SkyBlockInfo getInstance() {
        return INSTANCE;
    }
}
