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
    public PotionFetcher potionFetcher = new PotionFetcher(this);

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

    public boolean isInTheCatacombs = false;
    private boolean wasInTheCatacombs = false;

    public boolean IsDeadInTheCatacombs = false;

    private int tradeTicks = 0;

    String actionBarData  = ""; // Holds the data that is in the action bar
    String footerTabData = ""; // Holds the data that is in the

    private PlayerStats playerStats;
    private List<String> scoreBoardLines;
    private ScoreBoardData scoreBoardData = new ScoreBoardData();
    private OtherData otherData;

    public boolean isInSkyblock = false; // This flag is true if the player is in skyblock

    // Gets the action bar data
    // The priority is set on highest so the data is getting parsed before it's modified by sba
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMsg(ClientChatReceivedEvent event) {
        if (
                (event.message.getUnformattedText().contains(String.valueOf(HpSymbol))
                || event.message.getUnformattedText().contains(String.valueOf(MnSymbol))
                || event.message.getUnformattedText().contains(reviveSymbol)
                || event.message.getUnformattedText().contains(chickenRaceSymbol)) // For some reason this race doesn't show the mana
        ) {
            IsDeadInTheCatacombs = event.message.getUnformattedText().contains(reviveSymbol);
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
                if (wasInTheCatacombs ^ isInTheCatacombs) {
                    wasInTheCatacombs = isInTheCatacombs;
                    potionFetcher.ClearAll();
                }
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

            ProcessScoreBoardData();

            this.playerStats = this.ProcessPlayerStats();

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

    public boolean ShouldHaveSpaceBefore(String s) {
        return ApecUtils.containedByCharSequence(s,"Objective") || //Objectives
               ApecUtils.containedByCharSequence(s,"Contest") || // Jacob's contest
               ApecUtils.containedByCharSequence(s,"Year") || // New year and the vote thing
               ApecUtils.containedByCharSequence(s,"Zoo") || // Traveling Zoo
               ApecUtils.containedByCharSequence(s,"Festival") || // Spooky Festival
               ApecUtils.containedByCharSequence(s,"Season") || // Jerry season
               ApecUtils.containedByCharSequence(s,"Election") || // Idk at this point im just putting some things from the wiki
               ApecUtils.containedByCharSequence(s,"Slayer") || // Slayer quest
               ApecUtils.containedByCharSequence(s,"Keys") || // Keys in dungeons
               ApecUtils.containedByCharSequence(s,"Time Elapsed") || // time elapsed dungeons
               ApecUtils.containedByCharSequence(s,"Starting in:") ||
               ApecUtils.containedByCharSequence(s,"Wave") ||
               ApecUtils.containedByCharSequence(s,"Festival");
    }

    public boolean ShouldHaveSpaceAfter(String s) {
        return ApecUtils.containedByCharSequence(s,"Dungeon Cleared"); // Dungeon cleared in dungeons
    }

    public boolean RepresentsIRLDate(String s) {
        return ApecUtils.containedByCharSequence(s,"//");
    }

    public boolean RepresentsDate(String s) {
        return (ApecUtils.containedByCharSequence(s,"Autumn") ||
                ApecUtils.containedByCharSequence(s,"Winter") ||
                ApecUtils.containedByCharSequence(s,"Spring") ||
                ApecUtils.containedByCharSequence(s,"Summer")) &&
                (ApecUtils.containedByCharSequence(s,"st") ||
                 ApecUtils.containedByCharSequence(s,"nd") ||
                 ApecUtils.containedByCharSequence(s,"rd") ||
                 ApecUtils.containedByCharSequence(s,"th"));
    }

    public boolean RepresentsTime(String s) {
        return (ApecUtils.containedByCharSequence(s,"am") ||
                ApecUtils.containedByCharSequence(s,"pm")) &&
                ApecUtils.containedByCharSequence(s,":");
    }

    public boolean RepresentsZone(String s) {
        return ApecUtils.containedByCharSequence(s,"\u23E3");
    }

    public boolean RepresentsPurse(String s) {
        return ApecUtils.containedByCharSequence(s,"Purse: ");
    }

    public boolean RepresentsPiggy(String s){
        return ApecUtils.containedByCharSequence(s,"Piggy: ");
    }

    public boolean RepresentsBits(String s) {
        return ApecUtils.containedByCharSequence(s,"Bits: ");
    }

    /**
     * ScoreBoardData - Contains the things that used to be displayed in the scoreboard
     * -server
     * -in game date and time
     * -purse
     * NOTE! Anything else that is not constant on the scoreboard and appears or disappears due to events or context are in ScoreBoardData.ExtraInfo saved as a list
     */

    private void ProcessScoreBoardData() {
        try {
            scoreBoardData.ExtraInfo.clear();

            int size = scoreBoardLines.size() - 1;

            isInTheCatacombs = false;

            for (int i = 0;i < scoreBoardLines.size();i++){
                if (ApecUtils.containedByCharSequence(scoreBoardLines.get(size-i).toLowerCase(),clearedName)) {
                    isInTheCatacombs = true;
                }
                if (ApecUtils.containedByCharSequence(scoreBoardLines.get(size-i).toLowerCase(),combatZoneName) && !scoreBoardLines.get(size-i).toLowerCase().contains("to")) {
                    isInTheCatacombs = true;
                }
            }

            ScoreboardParser(scoreBoardData, scoreBoardLines);

            scoreBoardData.ExtraInfo.add(" ");

            alreadyShowedScrErr = false;
            lastDate = scoreBoardData.Date;
            lastZone = scoreBoardData.Zone;
            lastHour = scoreBoardData.Hour;
        } catch (Exception err) {
            if (!alreadyShowedScrErr) {
                ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error processing scoreboard data!");
                alreadyShowedScrErr = true;
            }
            err.printStackTrace();
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
     * @param l = All the lines of the scoreboard
     */

    private void ScoreboardParser(ScoreBoardData sd,List<String> l) {
        List<String> rl = Lists.reverse(l);
        for (String line : rl) {
            if (RepresentsIRLDate(line)) {
                sd.IRL_Date = ApecUtils.removeFirstSpaces(line);
                if (line.contains("\u00a78") && ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_CURRENT_SERVER)) {
                    sd.ExtraInfo.add("Currently in: " + ApecUtils.segmentString(scoreBoardLines.get(scoreBoardLines.size() - 1), "\u00a78", '\u00a7', '~', 1, 1, false,true));
                }
            }
            else if (RepresentsDate(line)) sd.Date = ApecUtils.removeFirstSpaces(line);
            else if (RepresentsTime(line)) sd.Hour = ApecUtils.removeFirstSpaces(line);
            else if (RepresentsZone(line)) sd.Zone = ApecUtils.removeFirstSpaces(line);
            else if (RepresentsPurse(line)) {
                sd.Purse = ApecUtils.removeFirstSpaces(line);
                usesPiggyBank = false;
            }
            else if (RepresentsPiggy(line)) {
                sd.Purse = ApecUtils.removeFirstSpaces(line);
                usesPiggyBank = true;
            }
            else if (RepresentsBits(line)) sd.Bits = ApecUtils.removeFirstSpaces(line);
            else if (!line.contains("www")) {
                if (line.replaceAll("[^a-zA-Z0-9]", "").length() != 0) {
                    if (ShouldHaveSpaceBefore(line)) sd.ExtraInfo.add(" ");
                    sd.ExtraInfo.add(ApecUtils.removeFirstSpaces(line));
                    if (ShouldHaveSpaceAfter(line)) sd.ExtraInfo.add(" ");
                }
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
                String segmentString = ApecUtils.segmentString(actionBarData, String.valueOf(HpSymbol), '\u00a7', HpSymbol, 1, 1, false,false);
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
                String segmentedString = ApecUtils.segmentString(actionBarData, String.valueOf(MnSymbol), '\u00a7', MnSymbol, 1, 1, false,false);
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
                String secmentedString = ApecUtils.segmentString(actionBarData, ")", '+', ' ', 1, 1, false,false);
                if (secmentedString != null) {
                    lastSkillXp = secmentedString;
                    String wholeString = ApecUtils.removeAllCodes(secmentedString);
                    Tuple<Float, Float> t = formatStringFractF(ApecUtils.segmentString(secmentedString, "(", '(', ')', 1, 1, true,false));

                    playerStats.SkillIsShown = true;
                    playerStats.SkillInfo = wholeString;
                    playerStats.SkillExp = t.getFirst();
                    playerStats.BaseSkillExp = t.getSecond();

                } else {
                    if (ApecMain.Instance.settingsManager.getSettingState(SettingID.ALWAYS_SHOW_SKILL) && !lastSkillXp.equals("")) {
                        String wholeString = ApecUtils.removeAllCodes(lastSkillXp);
                        Tuple<Float, Float> t = formatStringFractF(ApecUtils.segmentString(lastSkillXp, "(", '(', ')', 1, 1, true,false));

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
            String segmentedString = ApecUtils.segmentString(actionBarData, ")",'\u00a7',' ',3,1,false,false);
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
                String segmentedString = ApecUtils.segmentString(actionBarData, String.valueOf(DfSymbol), '\u00a7', DfSymbol, 2, 1, false,false);
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
        String endRace = ApecUtils.segmentString(actionBarData,endRaceSymbol,'\u00a7',' ',2,2,false,false);
        String woodRacing = ApecUtils.segmentString(actionBarData,woodRacingSymbol,'\u00a7',' ',2,2,false,false);
        String dps = ApecUtils.segmentString(actionBarData,dpsSymbol,'\u00a7',' ',1,1,false,false);
        String sec = ApecUtils.segmentString(actionBarData,secSymbol,'\u00a7',' ',1,2,false,false);
        String secrets = ApecUtils.segmentString(actionBarData,secretSymbol,'\u00a7','\u00a7',1,1,false,false);
        String chickenRace = ApecUtils.segmentString(actionBarData,chickenRaceSymbol,'\u00a7',' ',2,2,false,false);
        String jump = ApecUtils.segmentString(actionBarData,jumpSymbol,'\u00a7','\u00a7',3,1,false,false);
        String crystalRace = ApecUtils.segmentString(actionBarData,crystalRaceSymbol,'\u00a7',' ',2,2,false,false);
        String mushroomRace = ApecUtils.segmentString(actionBarData,giantMushroomSymbol,'\u00a7',' ',2,2,false,false);
        String precursorRace = ApecUtils.segmentString(actionBarData,precursorRuinsSymbol,'\u00a7',' ',2,2,false,false);

        if ((endRace != null || woodRacing != null || dps != null || sec != null) && !otherData.ExtraInfo.isEmpty()) otherData.ExtraInfo.add(" ");

        if (endRace != null) otherData.ExtraInfo.add(endRace);
        if (woodRacing != null) otherData.ExtraInfo.add(woodRacing);
        if (dps != null) otherData.ExtraInfo.add(dps);
        if (secrets != null) otherData.ExtraInfo.add(secrets);
        // The condition is like this to make sure the actionData is not null
        if (actionBarData != null ? actionBarData.contains("Revive") : false) {
            otherData.ExtraInfo.add(actionBarData);
        }
        // The revive message also contais the word second so we have to be sure is not that one
        else if (sec != null) otherData.ExtraInfo.add(sec);
        if (chickenRace != null) otherData.ExtraInfo.add(chickenRace);
        if (jump != null) otherData.ExtraInfo.add(jump);
        if (crystalRace != null) otherData.ExtraInfo.add(crystalRace);
        if (mushroomRace != null) otherData.ExtraInfo.add(mushroomRace);
        if (precursorRace != null) otherData.ExtraInfo.add(precursorRace);

        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_POTIONS_EFFECTS)) {

            List<String> effects = getPlayerEffects();
            if (!effects.isEmpty()) {
                if (!otherData.ExtraInfo.isEmpty()) otherData.ExtraInfo.add(" ");
                otherData.ExtraInfo.addAll(effects);
            }

        }

        otherData.currentEvents = getEvents(sd);

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

    private List<String> getPlayerEffects () {

        try {
            List<String> effects = new ArrayList<String>();
            String[] lines = footerTabData.split("\n");
            if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_EFFECTS_AS_IN_TAB)) {
                if (!lines[2].contains("No effects")) {
                    for (int i = 3; i < lines.length; i++) {
                        if (lines[i].contains(":")) {
                            effects.add(lines[i]);
                            break;
                        }
                    }
                }
            } else {
                effects = potionFetcher.GetPotionEffects();
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
                        String hpBaseHp = ApecUtils.segmentString(name, String.valueOf(HpSymbol), ' ', HpSymbol, 1, 1, true,false);
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
        public String Server = "";
        public String Purse = "";
        public String Bits = "";
        public List<String> ExtraInfo = new ArrayList<String>();
        public String Zone = "";
        public String Date = "";
        public String Hour = "";
        public String IRL_Date = "";
        public String scoreBoardTitle = "";
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