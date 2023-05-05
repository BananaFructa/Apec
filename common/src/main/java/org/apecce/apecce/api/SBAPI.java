package org.apecce.apecce.api;

import java.util.List;

public interface SBAPI {

    boolean isOnSkyblock();

    record SBScoreBoard(String serverShard,
                        String purse,
                        String bits,
                        List<String> extra,
                        String zone,
                        String date,
                        String Hour,
                        String IRL_Date,
                        String scoreboardTitle
    ) {

        public static final SBScoreBoard EMPTY = new SBScoreBoard("", "", "", List.of(), "", "", "", "", "");
    }

    SBScoreBoard getScoreboard();

    boolean usesPiggyBank();

    record PlayerStats(
            int hp,
            int base_hp,
            int heal_duration,
            int heal_duration_tick,
            int absorption,
            int base_absorption,
            int overflow,
            int mana,
            int base_mana,
            int defense,
            String skill_info,
            float skill_xp_percentage,
            boolean skill_shown,
            boolean ability_shown
    ) {
        public static final PlayerStats EMPTY = new PlayerStats(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 0, false, false);
    }

    PlayerStats getPlayerStats();

}
