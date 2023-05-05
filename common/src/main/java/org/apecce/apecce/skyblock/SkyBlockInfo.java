package org.apecce.apecce.skyblock;

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
import org.apecce.apecce.MC;
import org.apecce.apecce.api.SBAPI;
import org.apecce.apecce.events.ChatMessage;
import org.apecce.apecce.events.ClientTick;
import org.apecce.apecce.utils.ApecUtils;

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

    @Subscribe
    Listener<ClientTick> clientTickListener = new Listener<>(event -> {
        Component clientScoreboardTitle = getClientScoreboardTitle();
        if (!clientScoreboardTitle.getString().isEmpty()) {
            this.onSkyblock = clientScoreboardTitle.getString().contains("SKYBLOCK");

            this.scoreboardLines = getScoreboardLines();

            parseScoreboardData();

            parsePlayerStats();
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
                getClientScoreboardTitle().getString());

    }

    private void parsePlayerStats() {

        // 2,964/2,664❤     683❈ Defense     327/327✎ Mana
        String actionBar = this.clientOverlay.getString();

        int hp = 0;
        int base_hp = 0;
        int absorption = 0;
        int base_absorption = 0;

        // HP
        {
            String segment = ApecUtils.segmentString(actionBar, "❤", '§', '❤', 1, 1);
            if (segment != null) {
                Tuple<Integer, Integer> hpTuple = formatStringFractI(ApecUtils.removeAllColourCodes(segment));
                hp = hpTuple.getA();
                base_hp = hpTuple.getB();
            }
        }


        this.playerStats = new PlayerStats(
                hp,
                base_hp,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
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

    public static SkyBlockInfo getInstance() {
        return INSTANCE;
    }
}
