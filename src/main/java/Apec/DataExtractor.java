package Apec;

import Apec.Components.Gui.GuiIngame.ApecGuiIngameForge;
import Apec.Components.Gui.GuiIngame.ApecGuiIngameVanilla;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.Settings.SettingID;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
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
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Arrays;
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
    private final String secretSymbol = "Secrets";
    private final String chickenRaceSymbol = "CHICKEN RACING";
    private final String jumpSymbol = "JUMP";
    private final String crystalRaceSymbol = "CRYSTAL CORE RACE";
    private final String giantMushroomSymbol = "GIANT MUSHROOM RACE";
    private final String precursorRuinsSymbol = "PRECURSOR RUINS RACE";
    private final String reviveSymbol = "Revive";

    private boolean alreadyShowedTabError = false;
    private boolean alreadyShowedScrErr = false;

    private int lastHp = 1, lastBaseHp = 1;
    private int lastMn = 1, lastBaseMn = 1;
    private int lastDefence = 0;
    private int baseAp = 0;
    private int lastAp = 1 , lastBaseAp = 1;

    private String lastHour = "",lastDate = "",lastZone = "";

    private String lastSkillXp = "";
    private String lastPurse = "";

    private final String magmaBossName = "magma cube boss";

    private final String sendTradeRequestMsg = "You have sent a trade request to";
    private final String expireSentTradeRequest = "Your /trade request to";

    private final String tradeCancelled = "You cancelled the trade!";

    private final String recieveTradeRequestMsg = "has sent you a trade request";
    private final String expireRecieveTradeRequest = "The /trade request from";

    private final String tradeCompleted = "Trade completed";

    private final String combatZoneName = "the catacombs";
    private final String clearedName = "dungeon cleared";

    private boolean usesPiggyBank = false;

    private boolean hasSentATradeRequest = false;
    private boolean hasRecievedATradeRequest = false;

    private int tradeTicks = 0;

    String actionBarData  = ""; // Holds the data that is in the action bar
    String footerTabData = ""; // Holds the data that is in the

    private PlayerStats playerStats;
    private List<String> scoreBoardLines;
    private ScoreBoardData scoreBoardData;
    private OtherData otherData;

    public boolean isInSkyblock = false; // This flag is true if the player is in skyblock

    // Gets the action bar data
    // The priority is set on highest so the data is getting parsed before it's modified by sba
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMsg(ClientChatReceivedEvent event) {
        if (event.message.getUnformattedText().contains(String.valueOf(HpSymbol))
                || event.message.getUnformattedText().contains(String.valueOf(MnSymbol))
                || event.message.getUnformattedText().contains(reviveSymbol)) {
            actionBarData = event.message.getUnformattedText();
        } else if (!event.message.getUnformattedText().contains("<") && !event.message.getUnformattedText().contains(":")){

            String msg = ApecUtils.removeAllCodes(event.message.getUnformattedText());
            if (msg.contains(sendTradeRequestMsg)) hasSentATradeRequest = true;
            if (msg.contains(expireSentTradeRequest)) hasSentATradeRequest = false;

            if (msg.contains(recieveTradeRequestMsg)) hasRecievedATradeRequest = true;
            if (msg.contains(expireRecieveTradeRequest)) hasRecievedATradeRequest = false;

            if (msg.contains(tradeCancelled) || msg.contains(tradeCompleted)) {
                if (hasSentATradeRequest) hasSentATradeRequest = false;
                if (hasRecievedATradeRequest) hasRecievedATradeRequest = false;
            }
        }
    }

    // The priority is on lowest since the gui swapping that happens if sidebar mod swiched it happens on priority low
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTick(TickEvent.ClientTickEvent event) {

        try {
            String s = getScoreBoardTitle();
            if (!s.equals("")) {
                this.isInSkyblock = ApecUtils.removeAllCodes(s).toLowerCase().contains("skyblock");
            }
        } catch (Exception e) { }
        if (((GUIModifier)ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER)).shouldTheGuiAppear) {
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

            try {
                this.scoreBoardLines = getSidebarLines();
                this.scoreBoardLines = addDataFixesScoreboard(this.scoreBoardLines);
            } catch (Exception e) {

            }

            this.playerStats = this.ProcessPlayerStats();
            this.scoreBoardData = this.ProcessScoreBoardData();
            this.otherData = this.ProcessOtherData(this.scoreBoardData);

            if (hasRecievedATradeRequest || hasSentATradeRequest) {
                tradeTicks++;
                if (tradeTicks == 600){
                    hasSentATradeRequest = false;
                    hasRecievedATradeRequest = false;
                }
            } else {
                tradeTicks = 0;
            }

        }
    }

    /**
     * This only includes adding missing brackets in dungeons
     */
    public List<String> addDataFixesScoreboard(List<String> s) {
        for (String _s: s) {
            if (_s.contains("[") && !_s.contains("]")) _s.concat("\u00a7]");
        }
        return s;
    }

    /**
     * ScoreBoardData - Contains the things that used to be displayed in the scoreboard
     * -server
     * -in game date and time
     * -purse
     * NOTE! Anything else that is not constant on the scoreboard and appears or disappears due to events or context are in ScoreBoardData.ExtraInfo saved as a list
     */

    private ScoreBoardData ProcessScoreBoardData() {
        try {
            ScoreBoardData scoreBoardData = new ScoreBoardData();

            int size = scoreBoardLines.size() - 1;

            boolean isInTheCatacombs = false;
            int catacombsIndex = -1;
            int clearedIndex = -1;

            for (int i = 0;i < scoreBoardLines.size();i++){
                if (ApecUtils.containedByCharSequence(scoreBoardLines.get(size-i).toLowerCase(),clearedName)) {
                    clearedIndex = i;
                    isInTheCatacombs = true;
                }
                if (ApecUtils.containedByCharSequence(scoreBoardLines.get(size-i).toLowerCase(),combatZoneName) && !scoreBoardLines.get(size-i).toLowerCase().contains("to")) {
                    catacombsIndex = i;
                    isInTheCatacombs = true;
                }
            }

            if (isInTheCatacombs) {
                if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_CACHED_PURSE_IN_DUNGEONS)) {
                    scoreBoardData.Purse = lastPurse;
                } else {
                    scoreBoardData.Purse = "Purse: \u00a76Data Unavailable";
                }
            }

            if (!scoreBoardLines.isEmpty()) {
                if (scoreBoardLines.get(scoreBoardLines.size() - 1).contains("\u00a78") && ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_CURRENT_SERVER)) {
                    scoreBoardData.ExtraInfo.add("Currently in: " + segmentString(scoreBoardLines.get(scoreBoardLines.size() - 1), "\u00a78", '\u00a7', ' ', 1, 1, false));
                }
            }

            if (isInTheCatacombs) {
                ScoreBoardParserWhileInDungeons(scoreBoardData,clearedIndex,catacombsIndex,scoreBoardLines);
            } else {

                for (int i = 0; i < scoreBoardLines.size(); i++) {
                    String s;
                    switch (i) {
                        case 2:
                            s = scoreBoardLines.get(size - i);
                            scoreBoardData.Date = s;
                            break;
                        case 3:
                            s = scoreBoardLines.get(size - i);
                            scoreBoardData.Hour = s;
                            break;
                        case 4:
                            s = scoreBoardLines.get(size - i);
                            scoreBoardData.Zone = s;
                            break;
                        default:
                            s = scoreBoardLines.get(size - i);
                            ScoreboardParser(scoreBoardData, s, i, scoreBoardLines);
                            break;
                    }
                }
            }

            scoreBoardData.ExtraInfo.add(" ");

            alreadyShowedScrErr = false;
            lastDate = scoreBoardData.Date;
            lastZone = scoreBoardData.Zone;
            lastHour = scoreBoardData.Hour;
            return scoreBoardData;
        } catch (Exception err) {
            ScoreBoardData noReponse = new ScoreBoardData();
            noReponse.Purse = "N/A";
            noReponse.Zone = "N/A";
            noReponse.Hour = "N/A";
            noReponse.Date = "N/A";
            noReponse.Server = "N/A";
            noReponse.IRL_Date = "N/A";
            if (!alreadyShowedScrErr) {
                ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error processing scoreboard data!");
                alreadyShowedScrErr = true;
            }
            err.printStackTrace();

            return noReponse;
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

    public String getScoreBoardTitle() {
        try {
            Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
            ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
            return objective.getDisplayName();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @param sd = Current ScoreBoardData class
     * @param s = The current line
     * @param idx = The index of the scoreboard line
     * @param l = All the lines of the scoreboard
     */

    private void ScoreboardParser(ScoreBoardData sd,String s,int idx,List<String > l) {
        if (s.contains("Purse") || s.contains("Piggy")) {
            usesPiggyBank = s.contains("Piggy");
            sd.Purse = s;
            lastPurse = sd.Purse;
            if (!l.get(l.size() - 1 - idx - 2).contains("www")) {
                for (int i = idx + 2;i < l.size()-1;i++) {
                    String _s = l.get(l.size()-i);
                    sd.ExtraInfo.add(ApecUtils.removeFirstSpaces(_s));
                }
            }
        } else if (ApecUtils.containedByCharSequence(s,"Flight")) {
            sd.ExtraInfo.add(ApecUtils.removeFirstSpaces(s));
            // Detects the guesting slots
        } else if (ApecUtils.containedByCharSequence(s,"Redstone:")) {
            sd.ExtraInfo.add(ApecUtils.removeFirstSpaces(s));
        } else if (s.contains("\u270c")) {
            sd.ExtraInfo.add(ApecUtils.removeFirstSpaces(s));
        }

    }

    private void ScoreBoardParserWhileInDungeons(ScoreBoardData sd,int clrearedIdx,int cIdx,List<String> l) {
        if (cIdx != -1) {
            sd.Hour = ApecUtils.removeFirstSpaces(l.get((l.size() - 1 - cIdx + 1)));
            sd.Date = ApecUtils.removeFirstSpaces(l.get(l.size() - 1 - cIdx + 2));
            sd.Zone = ApecUtils.removeFirstSpaces(l.get(l.size() - 1 - cIdx));
        } else {
            sd.Hour = lastHour;
            sd.Zone = lastZone;
            sd.Date = lastDate;
        }
        if (clrearedIdx != -1) {
            List<String> ei = new ArrayList<String>();
            for (int i = clrearedIdx; i < l.size() - 2; i++) {
                ei.add(ApecUtils.removeFirstSpaces(l.get(l.size() - 1 - i)));
            }
            for (int i = clrearedIdx - 1; cIdx != -1 ? i > cIdx + 1 : i > -1; i--) {
                ei.add(0, ApecUtils.removeFirstSpaces(l.get(l.size() - 1 - i)));
            }
            sd.ExtraInfo.addAll(ei);
        } else if (cIdx != -1) {
            for (int i = cIdx + 2;i < l.size() - 2;i++) {
                sd.ExtraInfo.add(ApecUtils.removeFirstSpaces(l.get(l.size() - 1 - i)));
            }
        }
    }

    public ScoreBoardData getScoreBoardData () {
        return this.scoreBoardData;
    }

    /**
     * Player Stats
     * -health
     * -mana
     * -defence
     * -absorption
     * -skill xp
     * -air
     */

    private PlayerStats ProcessPlayerStats () {
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
            // Skill
                String secmentedString = segmentString(actionBarData, ")", '+', ' ', 1, 1, false);
                if (secmentedString != null) {
                    lastSkillXp = secmentedString;
                    String wholeString = ApecUtils.removeAllCodes(secmentedString);
                    Tuple<Float, Float> t = formatStringFractF(segmentString(secmentedString, "(", '(', ')', 1, 1, true));

                    playerStats.SkillIsShown = true;
                    playerStats.SkillInfo = wholeString;
                    playerStats.SkillExp = t.getFirst();
                    playerStats.BaseSkillExp = t.getSecond();

                } else {
                    if (ApecMain.Instance.settingsManager.getSettingState(SettingID.ALWAYS_SHOW_SKILL) && !lastSkillXp.equals("")) {
                        String wholeString = ApecUtils.removeAllCodes(lastSkillXp);
                        Tuple<Float, Float> t = formatStringFractF(segmentString(lastSkillXp, "(", '(', ')', 1, 1, true));

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

        try {
            // ABILITY
            String segmentedString = segmentString(actionBarData, ")",'\u00a7',' ',3,1,false);
            if (segmentedString != null) {
                if (segmentedString.contains("-") && actionBarData.contains(String.valueOf(MnSymbol)) /* This is to make sure that the data is indeed from the action bar */ ) {
                    playerStats.IsAbilityShown = true;
                    playerStats.AbilityText = segmentedString;
                }
            }
        } catch (Exception err) {
            playerStats.IsAbilityShown = false;
        }

        try {
            // DEF
            {
                String segmentedString = segmentString(actionBarData, String.valueOf(DfSymbol), '\u00a7', DfSymbol, 2, 1, false);
                if (segmentedString != null) {
                    playerStats.Defence = Integer.parseInt(ApecUtils.removeAllCodes(segmentedString));
                    lastDefence = playerStats.Defence;
                } else if (!actionBarData.contains(endRaceSymbol) &&
                        !actionBarData.contains(woodRacingSymbol) &&
                        !actionBarData.contains(dpsSymbol) &&
                        !actionBarData.contains(secSymbol) &&
                        !actionBarData.contains(secretSymbol) &&
                        !actionBarData.contains(chickenRaceSymbol) &&
                        !actionBarData.contains(jumpSymbol) &&
                        !actionBarData.contains(crystalRaceSymbol) &&
                        !actionBarData.contains(giantMushroomSymbol) &&
                        !actionBarData.contains(precursorRuinsSymbol) &&
                        !playerStats.IsAbilityShown &&
                        !playerStats.SkillIsShown)
                // Makes sure that the defence is not replace by something else in the auction bar and it is really 0
                {
                    playerStats.Defence = 0;
                    lastDefence = playerStats.Defence;
                } else {
                    playerStats.Defence = lastDefence;
                }
            }
        } catch (Exception err) {
            playerStats.Defence = lastDefence;
        }

        return playerStats;
    }

    public PlayerStats getPlayerStats () {
        return this.playerStats;
    }

    /**
     * Other Data Processing - This contains data that appears in special contexts that aren't really that big to have their own functions and what not
     * In extra info ###
     *    -potion effects
     *    -trials of fire
     *    -wood race
     *    -the end race
     * ##################
     * -boss data
     */

    private OtherData ProcessOtherData (ScoreBoardData sd) {
        OtherData otherData = new OtherData();
        String endRace = segmentString(actionBarData,endRaceSymbol,'\u00a7',' ',2,2,false);
        String woodRacing = segmentString(actionBarData,woodRacingSymbol,'\u00a7',' ',2,2,false);
        String dps = segmentString(actionBarData,dpsSymbol,'\u00a7',' ',1,1,false);
        String sec = segmentString(actionBarData,secSymbol,'\u00a7',' ',1,2,false);
        String secrets = segmentString(actionBarData,secretSymbol,'\u00a7','\u00a7',1,1,false);
        String chickenRace = segmentString(actionBarData,chickenRaceSymbol,'\u00a7',' ',2,2,false);
        String jump = segmentString(actionBarData,jumpSymbol,'\u00a7','P',3,1,false);
        String crystalRace = segmentString(actionBarData,crystalRaceSymbol,'\u00a7',' ',2,2,false);
        String mushroomRace = segmentString(actionBarData,giantMushroomSymbol,'\u00a7',' ',2,2,false);
        String precursorRace = segmentString(actionBarData,precursorRuinsSymbol,'\u00a7',' ',2,2,false);

        if ((endRace != null || woodRacing != null || dps != null || sec != null) && !otherData.ExtraInfo.isEmpty()) otherData.ExtraInfo.add(" ");

        if (endRace != null) otherData.ExtraInfo.add(endRace);
        if (woodRacing != null) otherData.ExtraInfo.add(woodRacing);
        if (dps != null) otherData.ExtraInfo.add(dps);
        if (secrets != null) otherData.ExtraInfo.add(secrets);
        // The condition is like this to make sure the actionData is not null
        if (actionBarData != null ? actionBarData.contains("Revive") : false) otherData.ExtraInfo.add(actionBarData);
        // The revive message also contais the word second so we have to be sure is not that one
        else if (sec != null) otherData.ExtraInfo.add(sec);
        if (chickenRace != null) otherData.ExtraInfo.add(chickenRace);
        if (jump != null) otherData.ExtraInfo.add(jump);
        if (crystalRace != null) otherData.ExtraInfo.add(crystalRace);
        if (mushroomRace != null) otherData.ExtraInfo.add(mushroomRace);
        if (precursorRace != null) otherData.ExtraInfo.add(precursorRace);

        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_POTIONS_EFFECTS)) {

            ArrayList<String> effects = getPlayerEffects();
            if (!effects.isEmpty()) {
                if (!otherData.ExtraInfo.isEmpty()) otherData.ExtraInfo.add(" ");
                otherData.ExtraInfo.addAll(effects);
            }

        }

        otherData.currentEvents = getEvents(sd);

        //otherData.bossData = checkForBosses();

        return otherData;

    }

    /**
     * @param sd = ScoreBoardData
     * @return A list of the current events (see EventIDs.class for the events)
     */

    private ArrayList<EventIDs> getEvents (ScoreBoardData sd) {
        ArrayList<EventIDs> events = new ArrayList<EventIDs>();

        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_WARNING)) {
            try {
                if (mc.thePlayer != null) if (isInvFull()) events.add(EventIDs.INV_FULL);
                if (sd.Purse != null) {
                    String purse = ApecUtils.removeAllCodes(sd.Purse);
                    if (purse.contains("(")) purse = purse.substring(0,purse.indexOf("("));
                    purse = ApecUtils.removeNonNumericalChars(purse);
                    if (!purse.equals("")) {
                        if (Float.parseFloat(purse) >= 5000000f && !usesPiggyBank) events.add(EventIDs.COIN_COUNT);
                    }
                }
                if (hasSentATradeRequest) events.add(EventIDs.TRADE_OUT);
                if (hasRecievedATradeRequest) events.add(EventIDs.TRADE_IN);
                if (!scoreBoardLines.isEmpty())
                    if (ApecUtils.removeAllCodes(scoreBoardLines.get(scoreBoardLines.size() - 1)).contains("Server closing"))
                        events.add(EventIDs.SERVER_REBOOT);
                if (mc.thePlayer != null) {
                    int pingThreshold = 80;
                    int ping = mc.thePlayer.sendQueue.getPlayerInfo(mc.thePlayer.getGameProfile().getId()).getResponseTime();
                    if (ping > pingThreshold) events.add(EventIDs.HIGH_PING);
                }
            } catch (Exception e) {
            }
        }

        return events;
    }

    /**
     * @return Retuns the effects of the player
     */

    private ArrayList<String> getPlayerEffects () {
        try {
            ArrayList<String> effects = new ArrayList<String>();
            String[] lines = footerTabData.split("\n");
            if (!lines[2].contains("No effects")) {
                for (int i = 3; i < lines.length && lines[i].contains(":"); i++) {
                    if (ApecMain.Instance.settingsManager.getSettingState(SettingID.COMPACT_POTION)) {
                        if(ApecMain.Instance.settingsManager.getSettingState(SettingID.HIDE_NIGHT_VISION)) {
                            String[] split = lines[i].split(" {5}");
                            if (split.length > 1) {
                                if (split[0].contains("Night Vision")) effects.add(split[1]);
                                else if (split[1].contains("Night Vision")) effects.add(split[0]);
                                else effects.add(lines[i]);
                            } else if (!lines[i].contains("Night Vision")) {
                                effects.add(lines[i]);
                            }
                        } else {
                            effects.add(lines[i]);
                        }
                    } else {

                        String[] split = lines[i].split(" {5}");
                        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.HIDE_NIGHT_VISION)) {
                            for (int j = 0; j < split.length; j++) {
                                if (!ApecUtils.removeAllCodes(split[j]).contains("Night Vision")) {
                                    effects.add(split[j]);
                                }
                            }
                        } else {
                            effects.addAll(Arrays.asList(split));
                        }

                    }
                }
            }
            ApecUtils.orderByWidth(effects);
            return effects;
        } catch (Exception err) {
            return new ArrayList<String>();
        }
    }

    /**
     * @return Returns true if the inventory of the player is full
     */

    private boolean isInvFull () {
        InventoryPlayer ip = mc.thePlayer.inventory;
        for (int i = 0;i < 36;i++) {
            if (ip.getStackInSlot(i) == null) return false;
        }
        return true;
    }

    /**
     * @return The data of the current boss , if there is none then bossId = BossId.NONE
     */

    private BossData checkForBosses() {
        try {
            BossData bossData = new BossData();
            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (!(entity instanceof EntityPlayer)) {
                    String name = ApecUtils.removeAllCodes(entity.getCustomNameTag());
                    System.out.println(name);
                    if (name.toLowerCase().contains(magmaBossName)) {
                        bossData.bossId = BossId.MAGMA_BOSS;
                        String hpBaseHp = segmentString(name, String.valueOf(HpSymbol), ' ', HpSymbol, 1, 1, true);
                        Tuple<Integer, Integer> t = formatStringFractI(hpBaseHp);
                        bossData.Hp = t.getFirst();
                        bossData.BaseHp = t.getSecond();
                        System.out.println(name + " " + bossData.Hp + " " + bossData.BaseHp);
                        break;
                    }
                }
            }
            if (bossData.bossId == null) bossData.bossId = BossId.NONE;
            return bossData;
        } catch (Exception e) {
            BossData bossData = new BossData();
            bossData.bossId = BossId.NONE;
            return bossData;
        }
    }

    public OtherData getOtherData() {
        return otherData;
    }

    /**
     * @param string = The string you want to extract data from
     * @param symbol = A string that will act as a pivot
     * @param leftChar = It will copy all the character from the left of the pivot until it encounters this character
     * @param rightChar = It will copy all the character from the right of the pivot until it encounters this character
     * @param allowedInstancesL = How many times can it encounter the left char before it stops copying the characters
     * @param allowedInstancesR = How many times can it encounter the right char before it stops copying the characters
     * @param totallyExclusive = Makes so that the substring wont include the character from the left index
     * @return Returns the string that is defined by the bounds of leftChar and rightChar encountered allowedInstacesL  respectively allowedInctancesR - 1 within it
     *         allowedInsracesL only if totallyExclusive = false else allowedInstacesL - 1
     */

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

    /**
     * @param s = The string that you input
     * @return From two integers divide by '/' (ex 230/500) it returns the vale and the base value (ex 230 is the value and 500 is the base value)
     */

    public Tuple<Integer,Integer> formatStringFractI(String s) {
        String[] tempSplit = s.split("/");
        return new Tuple<Integer, Integer>(Integer.parseInt(tempSplit[0]),Integer.parseInt(tempSplit[1]));
    }

    /**
     * @param s = The string that you input
     * @return From two floats divide by '/' (ex 230.5/500) it returns the vale and the base value (ex 230.5 is the value and 500 is the base value)
     */

    public Tuple<Float,Float> formatStringFractF(String s) {
        s = s.replace(",","");
        String[] tempSplit = s.split("/");
        return new Tuple<Float, Float>(Float.parseFloat(tempSplit[0]),Float.parseFloat(tempSplit[1]));
    }

    /**
     * The classes that encapsulate all the different types of data
     */

    public class ScoreBoardData {
        public String Server;
        public String Purse;
        public List<String> ExtraInfo = new ArrayList<String>();
        public String Zone;
        public String Date;
        public String Hour;
        public String IRL_Date;
        public String scoreBoardTitle;
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
        public boolean IsAbilityShown;
        public String AbilityText;
    }

    public class OtherData {

        public ArrayList<String> ExtraInfo  = new ArrayList<String>();
        public ArrayList<EventIDs> currentEvents = new ArrayList<EventIDs>();
        public BossData bossData = new BossData();

    }

    public class BossData {
        public BossId bossId;
        public int Hp = 0,BaseHp = 0;
    }

}