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


}
