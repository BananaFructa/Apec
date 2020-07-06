package Apec;

import Apec.Components.Gui.ApecGuiIngame;
import akka.io.Inet;
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
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.reflect.FieldUtils;
import scala.tools.nsc.doc.model.Def;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DataExtractor {

    private Minecraft mc = Minecraft.getMinecraft();

    private final char HpSymbol = '\u2764';
    private final char DfSymbol = '\u2748';
    private final char MnSymbol = '\u270e';

    private final String endRaceSymbol = "THE END RACE";
    private final String woodRacingSymbol = "WOODS RACING";
    private final String dpsSymbol = "DPS";
    private final String secSymbol = "second";

    private boolean alreadyShowedError = false;
    private boolean alreadyShowedTabError = false;

    private int lastHp = 1, lastBaseHp = 1;
    private int lastMn = 1, lastBaseMn = 1;
    private int lastDefence = 0;
    private int baseAp = 0;
    private int lastAp = 1 , lastBaseAp = 1;

    String actionBarData  = "";
    String footerTabData = "";

    // Gets the action bar data
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onChatMsg(ClientChatReceivedEvent event) {
        actionBarData = event.message.getUnformattedText();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.ingameGUI instanceof ApecGuiIngame) {
            try {
                IChatComponent icc = (IChatComponent) FieldUtils.readField(mc.ingameGUI.getTabList(), ApecUtils.unObfedFieldNames.get("footer"), true);
                if (icc == null) {
                    this.footerTabData = "";
                } else {
                    this.footerTabData = icc.getFormattedText();
                    alreadyShowedTabError = false;
                }
            } catch (Exception e) {
                if (!alreadyShowedTabError) {
                    ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error processing tab footer data!");
                    alreadyShowedTabError = true;
                }
            }
        }
    }


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
        try {
            ScoreBoardData scoreBoardData = new ScoreBoardData();
            List<String> Lines = this.getSidebarLines();
            int size = Lines.size() - 1;
            for (int i = 0; i < Lines.size(); i++) {
                String s;
                switch (i) {
                    case 0:
                        s = Lines.get(size - i);
                        String[] dns = s.split(" ");
                        scoreBoardData.IRL_Date = dns[0];
                        scoreBoardData.Server = dns[1];
                        break;
                    case 2:
                        s = Lines.get(size - i);
                        scoreBoardData.Date = s;
                        break;
                    case 3:
                        s = Lines.get(size - i);
                        scoreBoardData.Hour = s;
                        break;
                    case 4:
                        s = Lines.get(size - i);
                        scoreBoardData.Zone = s;
                        break;
                    default:
                        s = Lines.get(size - i);
                        ScoreboardParser(scoreBoardData, s, i, Lines);
                        break;
                }
            }

            String endRace = segmentString(actionBarData,endRaceSymbol,'\u00a7',' ',2,2,false);
            String woodRacing = segmentString(actionBarData,woodRacingSymbol,'\u00a7',' ',2,2,false);
            String dps = segmentString(actionBarData,dpsSymbol,'\u00a7',' ',1,1,false);
            String sec = segmentString(actionBarData,secSymbol,'\u00a7',' ',1,2,false);

            if ((endRace != null || woodRacing != null || dps != null || sec != null) && !scoreBoardData.ExtraInfo.isEmpty()) scoreBoardData.ExtraInfo.add(" ");

            if (endRace != null) scoreBoardData.ExtraInfo.add(endRace);
            if (woodRacing != null) scoreBoardData.ExtraInfo.add(woodRacing);
            if (dps != null) scoreBoardData.ExtraInfo.add(dps);
            if (sec != null) scoreBoardData.ExtraInfo.add(sec);

            ArrayList<String> effects = getPlayerEffects();
            if (!effects.isEmpty()) {
                if (!scoreBoardData.ExtraInfo.isEmpty()) scoreBoardData.ExtraInfo.add(" ");
                scoreBoardData.ExtraInfo.addAll(effects);
            }

            alreadyShowedError = false;
            return scoreBoardData;
        } catch (Exception err) {
            ScoreBoardData noReponse = new ScoreBoardData();
            noReponse.Purse = "N/A";
            noReponse.Zone = "N/A";
            noReponse.Hour = "N/A";
            noReponse.Date = "N/A";
            noReponse.Server = "N/A";
            noReponse.IRL_Date = "N/A";

            if (!alreadyShowedError) {
                alreadyShowedError = true;
                ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error processing scoreboard data!");
            }
            return noReponse;
        }
    }

    private ArrayList<String> getPlayerEffects () {
        ArrayList<String> effects = new ArrayList<String>();
        String[] lines = footerTabData.split("\n");
        if (!lines[2].contains("No effects")) {
            for (int i = 2;i < lines.length && lines[i].contains(":");i++) {
                effects.add(lines[i]);
            }
        }
        return effects;
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
        }

    }

    public PlayerStats ProcessPlayerStats () {
        PlayerStats playerStats = new PlayerStats();

        try {
            //HP
            {
                String segmentString = segmentString(actionBarData, String.valueOf(HpSymbol), '\u00a7', HpSymbol, 1, 1, false);
                if (segmentString != null) {
                    Tuple<Integer, Integer> t = formatStringFractI(ApecUtils.removeAllCodes(segmentString));
                    playerStats.Hp = t.getFirst();
                    playerStats.BaseHp = t.getSecond();

                    if (playerStats.Hp > playerStats.BaseHp) {
                        playerStats.Ap = playerStats.Hp - playerStats.BaseHp;
                        playerStats.Hp = playerStats.BaseHp;
                    } else {
                        playerStats.Ap = 0;
                        playerStats.BaseAp = 0;
                    }
                    if (playerStats.Ap > baseAp) {
                        baseAp = playerStats.Ap;
                    }
                    playerStats.BaseAp = baseAp;

                    lastAp = playerStats.Ap;
                    lastBaseAp = playerStats.BaseAp;

                    lastHp = playerStats.Hp;
                    lastBaseHp = playerStats.BaseHp;
                } else {
                    playerStats.Hp = lastHp;
                    playerStats.BaseHp = lastBaseHp;
                    playerStats.Ap = lastAp;
                    playerStats.BaseAp = lastBaseAp;
                }
            }
        } catch (Exception err) {
            playerStats.Hp = lastHp;
            playerStats.BaseHp = lastBaseHp;
            playerStats.Ap = lastAp;
            playerStats.BaseAp = lastBaseAp;
        }

        try {
            // MP
            {
                String segmentedString = segmentString(actionBarData, String.valueOf(MnSymbol), '\u00a7', MnSymbol, 1, 1, false);
                if (segmentedString != null) {
                    Tuple<Integer, Integer> t = formatStringFractI(ApecUtils.removeAllCodes(segmentedString));
                    playerStats.Mp = t.getFirst();
                    playerStats.BaseMp = t.getSecond();
                    lastMn = playerStats.Mp;
                    lastBaseMn = playerStats.BaseMp;
                } else {
                    playerStats.Mp = lastMn;
                    playerStats.BaseMp = lastBaseMn;
                }
            }
        } catch (Exception err) {
            playerStats.Mp = lastMn;
            playerStats.BaseMp = lastBaseMn;
        }

        try {
            // DEF
            {
                String segmentedString = segmentString(actionBarData, String.valueOf(DfSymbol), '\u00a7', DfSymbol, 2, 1, false);
                if (segmentedString != null) {
                    playerStats.Defence = Integer.parseInt(ApecUtils.removeAllCodes(segmentedString));
                    lastDefence = playerStats.Defence;
                } else {
                    playerStats.Defence = lastDefence;
                }
            }
        } catch (Exception err) {
            playerStats.Defence = lastDefence;
        }

        try {
            // Skill
            {
                String secmentedString = segmentString(actionBarData, ")", '+', ' ', 1, 1, false);
                if (secmentedString != null) {
                    String wholeString = ApecUtils.removeAllCodes(secmentedString);
                    Tuple<Float, Float> t = formatStringFractF(segmentString(actionBarData, "(", '(', ')', 1, 1, true));

                    playerStats.SkillIsShown = true;
                    playerStats.SkillInfo = wholeString;
                    playerStats.SkillExp = t.getFirst();
                    playerStats.BaseSkillExp = t.getSecond();

                } else {
                    playerStats.SkillIsShown = false;
                }
            }
        } catch (Exception err) {
            playerStats.SkillIsShown = false;
        }

        return playerStats;
    }

    public String segmentString(String string,String symbol,char leftChar,char rightChar,int allowedInstancesL,int allowedInstancesR,boolean totallyExclusive) {

        int leftIdx = 0,rightIdx = 0;

        if (string.contains(symbol)) {

            int symbolIdx = string.indexOf(symbol);

            for (int i = 0; symbolIdx - i > -1; i++) {
                leftIdx = symbolIdx - i;
                if (string.charAt(symbolIdx - i) == leftChar) allowedInstancesL--;
                if (allowedInstancesL == 0) {
                    break;
                }
            }

            symbolIdx += symbol.length() - 1;

            for (int i = 0; symbolIdx + i < string.length(); i++) {
                rightIdx = symbolIdx + i;
                if (string.charAt(symbolIdx + i) == rightChar) allowedInstancesR--;
                if (allowedInstancesR == 0) {
                    break;
                }
            }

            return string.substring(leftIdx + (totallyExclusive ? 1 : 0), rightIdx);
        } else {
            return null;
        }

    }

    public Tuple<Integer,Integer> formatStringFractI(String s) {
        String[] tempSplit = s.split("/");
        return new Tuple<Integer, Integer>(Integer.parseInt(tempSplit[0]),Integer.parseInt(tempSplit[1]));
    }

    public Tuple<Float,Float> formatStringFractF(String s) {
        s = s.replace(",","");
        String[] tempSplit = s.split("/");
        return new Tuple<Float, Float>(Float.parseFloat(tempSplit[0]),Float.parseFloat(tempSplit[1]))
;    }

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
        public int Ap; // Absorption Points
        public int BaseAp; // Base Absorption
        public int Mp;
        public int BaseMp;
        public int Defence;
        public String SkillInfo;
        public float SkillExp;
        public float BaseSkillExp;
        public boolean SkillIsShown;
    }

}