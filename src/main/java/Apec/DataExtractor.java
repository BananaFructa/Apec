package Apec;

import Apec.Components.Gui.ApecGuiIngame;
import com.google.common.base.Converter;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Tuple;
import scala.tools.nsc.doc.model.Def;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DataExtractor {

    private Minecraft mc = Minecraft.getMinecraft();

    private final String HpPrefix = "\u00a7c";
    private final String DefPrefix = "\u00a7a";
    private final String MpPrefix = "\u00a7b";
    private final String SkillPrefix = "\u00a73";

    private int lastHp = 1, lastBaseHp = 1;
    private int lastMn = 1, lastBaseMn = 1;
    private int lastDefence = 0;


    /** Thanks for this very cool
     * From https://gist.github.com/aaron1998ish/33c4e1836bd5cf79501d163a1b5c8304
     *
     * Fetching lines are based on how they're visually seen on your sidebar
     * and not based on the actual value score.
     *
     * Written around Minecraft 1.8 Scoreboards, modify to work with your
     * current version of Minecraft.
     *
     * <3 aaron1998ish
     *
     * @return a list of lines for a given scoreboard or empty
     *         if the worlds not loaded, scoreboard isnt present
     *         or if the sidebar objective isnt created.
     */
    public List<String> getSidebarLines() {
        List<String> lines = new ArrayList<String>();
        Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
        if (scoreboard == null) {
            return lines;
        }

        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);

        if (objective == null) {
            return lines;
        }

        Collection<Score> scores = scoreboard.getSortedScores(objective);
        List<Score> list = Lists.newArrayList(Iterables.filter(scores,new Predicate<Score>()  {
            public boolean apply(Score s) {
                return s.getPlayerName() != null;
            }
        }));

        if (list.size() > 15) {
            scores = Lists.newArrayList(Iterables.skip(list, scores.size() - 15));
        } else {
            scores = list;
        }

        for (Score score : scores) {
            ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
            lines.add(ScorePlayerTeam.formatPlayerName(team, score.getPlayerName()));
        }

        return lines;
    }

    public ScoreBoardData ProcessScoreBoardData() {
        ScoreBoardData scoreBoardData = new ScoreBoardData();
        List<String> Lines = this.getSidebarLines();
        int size = Lines.size() - 1;
        for (int i = 0;i < Lines.size();i++) {
            String s;
            switch (i) {
                case 0:
                    s = Lines.get(size-i);
                    String[] dns = s.split(" ");
                    scoreBoardData.IRL_Date = dns[0];
                    scoreBoardData.Server = dns[1];
                    break;
                case 2:
                    s = Lines.get(size-i);
                    scoreBoardData.Date = s;
                    break;
                case 3:
                    s = Lines.get(size-i);
                    scoreBoardData.Hour = s;
                    break;
                case 4:
                    s = Lines.get(size-i);
                    scoreBoardData.Zone = s;
                    break;
                default:
                    s = Lines.get(size-i);
                    ScoreboardParser(scoreBoardData,s,i,Lines);
                    break;
            }
        }
        return scoreBoardData;
    }

    private void ScoreboardParser(ScoreBoardData sd,String s,int idx,List<String > l) {
        if (s.contains("Purse") || s.contains("Piggy")) {
            sd.Purse = s;
            if (!l.get(l.size() - 1 - idx - 2).contains("www")) {
                for (int i = idx + 2;i < l.size()-1;i++) {
                    String _s = l.get(l.size()-i);
                    if (_s.charAt(0) == ' ') _s = _s.replaceFirst(" ","");
                    sd.ExtraInfo.add(_s);
                }
            }
        } else if (s.contains("Flight")) {
            sd.ExtraInfo.add(s);
            sd.ExtraInfo.add(" ");
        } /*else if (s.contains("Objective")) {
            sd.ExtraInfo.add(s);
            while (l.get(l.size() - 1 - idx - 1).contains("[a-zA-z]+")) {
                sd.ExtraInfo.add(l.get(l.size() - 1 - idx - 1));
                idx++;
            }
            sd.ExtraInfo.add(" ");
        } else if (s.contains("Slayer")) {
            sd.ExtraInfo.add(s);
            sd.ExtraInfo.add(l.get(l.size() - 1 - idx - 1));
            sd.ExtraInfo.add(l.get(l.size() - 1 - idx - 2));
            sd.ExtraInfo.add(" ");
        } else if (s.contains("Dragon HP")) {
            sd.ExtraInfo.add(s);
            sd.ExtraInfo.add(l.get(l.size() - 1 - idx - 1));
            sd.ExtraInfo.add(" ");
        } else if (s.contains("New Year")) {
            sd.ExtraInfo.add(s);
            sd.ExtraInfo.add(" ");
        }*/
    }

    public PlayerStats ProcessPlayerStats () {
        PlayerStats playerStats = new PlayerStats();

        String actionBarData = ((ApecGuiIngame)mc.ingameGUI).getActionBarData();

        // HP
        try {
            int hpIdx = actionBarData.indexOf(HpPrefix) + HpPrefix.length();
            Tuple<Integer,Integer> t = getValBaseVal(hpIdx,actionBarData);
            playerStats.Hp = t.getFirst();
            playerStats.BaseHp = t.getSecond();
            lastHp = playerStats.Hp;
            lastBaseHp = playerStats.BaseHp;

        } catch (Exception e) {
            playerStats.Hp = lastHp;
            playerStats.BaseHp = lastBaseHp;
        }

        // MP
        try {
            int mpIdx = actionBarData.indexOf(MpPrefix) + MpPrefix.length();
            Tuple<Integer,Integer> t = getValBaseVal(mpIdx,actionBarData);
            playerStats.Mp = t.getFirst();
            playerStats.BaseMp = t.getSecond();
            lastMn = playerStats.Mp;
            lastBaseMn = playerStats.BaseMp;
        } catch (Exception e) {
            playerStats.Mp = lastMn;
            playerStats.BaseMp = lastBaseMn;
        }

        // DEF
        try {
            int dfIdx = actionBarData.indexOf(DefPrefix) + DefPrefix.length();
            if (dfIdx != -1) {
                String defVal = "";
                for (int i = dfIdx; i < 50 && actionBarData.charAt(i + 1) != ' '; i++) {
                    defVal += actionBarData.charAt(i);
                }

                while (defVal.contains("\u00a7")) {
                    defVal = defVal.replace("\u00a7" + defVal.charAt(defVal.indexOf("\u00a7") + 1), "");
                }

                playerStats.Defence = Integer.parseInt(defVal);
                lastDefence = playerStats.Defence;
            } else {
                playerStats.Defence = lastDefence;
            }
        } catch (Exception e) {
            playerStats.Defence = lastDefence;
        }

        // The two ( ) means that there is some skill shown since nothing else shows the parantecies in the action bar
        if (actionBarData.contains(SkillPrefix)) {
            playerStats.SkillIsShown = true;
            String skillInfo = actionBarData.substring(actionBarData.indexOf(SkillPrefix) + SkillPrefix.length(), actionBarData.indexOf(")") + 1);
            playerStats.SkillInfo = skillInfo;
            Tuple<Float,Float> t = getValBaseValF(actionBarData.indexOf("(")+1,actionBarData);
            playerStats.SkillExp = t.getFirst();
            playerStats.BaseSkillExp = t.getSecond();
        } else {
            playerStats.SkillIsShown = false;
        }

        return playerStats;
    }

    // i < 50 is a halting safety net, just in case
    public Tuple<Integer,Integer> getValBaseVal(int idx,String s) {
        String temp = "";
        String[] tempSplit;
        for (int i = 0; i < 50 && s.charAt(idx + i + 1) != ' '; i++) {
            temp += s.charAt(idx + i);
        }
        tempSplit = temp.split("/");
        return new Tuple<Integer, Integer>(Integer.parseInt(tempSplit[0]),Integer.parseInt(tempSplit[1]));
    }

    public Tuple<Float,Float> getValBaseValF(int idx,String s) {
        String temp = "";
        String[] tempSplit;
        for (int i = 0; i < 50 && s.charAt(idx + i + 1) != ' '; i++) {
            temp += s.charAt(idx + i);
        }
        tempSplit = temp.split("/");
        return new Tuple<Float, Float>(Float.parseFloat(tempSplit[0].replace(",","")),Float.parseFloat(tempSplit[1].replace(",","")));
    }

    public class ScoreBoardData {
        public String Server;
        public String Purse;
        public List<String> ExtraInfo = new ArrayList<String>();
        public String Zone;
        public String Date;
        public String Hour;
        public String IRL_Date;
    }

    public class PlayerStats {
        public int Hp;
        public int BaseHp;
        public int Mp;
        public int BaseMp;
        public int Defence;
        public String SkillInfo;
        public float SkillExp;
        public float BaseSkillExp;
        public boolean SkillIsShown;
    }

}
