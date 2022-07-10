package Apec.Settings;

import Apec.ApecMain;
import Apec.Events.ApecSettingChangedState;
import Apec.Utils.ApecUtils;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.MinecraftForge;
import org.json.*;
import org.lwjgl.Sys;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static Apec.Settings.SettingID.*;

public class SettingsManager {

    /** List which contains all settings */
    public List<Setting> settings = new ArrayList<Setting>() {{
        add(new Setting("autoEnable", AUTO_ENABLE, CategoryID.GENERAL,true));
        add(new Setting("showHPBar",HP_BAR, CategoryID.GUI,true));
        add(new Setting("showHPText",HP_TEXT, CategoryID.GUI,true));
        add(new Setting("showAPBar", SHOW_ABSORPTION_BAR, CategoryID.GUI,true));
        add(new Setting("showOPBar", SHOW_OP_BAR, CategoryID.GUI,true));
        add(new Setting("showMPBar", MP_BAR, CategoryID.GUI,true));
        add(new Setting("showMPText", MP_TEXT, CategoryID.GUI,true));
        add(new Setting("showXPBar", XP_BAR, CategoryID.GUI,true));
        add(new Setting("showXPText", XP_TEXT, CategoryID.GUI,true));
        add(new Setting("showAirBar", SHOW_AIR_BAR, CategoryID.GUI,true));
        add(new Setting("showAirText", AIR_TEXT, CategoryID.GUI,true));
        add(new Setting("showSkillXP", SHOW_SKILL_XP, CategoryID.GUI,true));
        add(new Setting("showSkillText", SKILL_TEXT, CategoryID.GUI,true));
        add(new Setting("coloredSkillXP",COLORED_SKILL_XP, CategoryID.GUI,true));
        add(new Setting("alwaysShowskill", ALWAYS_SHOW_SKILL, CategoryID.GUI,false));
        add(new Setting("showWarnings", SHOW_WARNING, CategoryID.GUI,true));
        add(new Setting("showInventoryTraffic", INVENTORY_TRAFFIC, CategoryID.GUI,true));
        add(new Setting("showPotionEffects", SHOW_POTIONS_EFFECTS, CategoryID.GUI,true));
        add(new Setting("compactPotionEffects", COMPACT_POTION, CategoryID.GUI,false));
        add(new Setting("showObjective",SHOW_OBJECTIVE,CategoryID.GUI,true));
        //add(new Setting(HIDE_NIGHT_VISION,false));
        add(new Setting("customTooltips", CUSTOM_TOOL_TIP, CategoryID.GUI,true));
        add(new Setting("showEffectsAsTab", SHOW_EFFECTS_AS_IN_TAB, CategoryID.GUI,true));
        add(new Setting("showCurrentServer", SHOW_CURRENT_SERVER, CategoryID.GUI,false));
        add(new Setting("showItemHighlightText", ITEM_HIGHLIGHT_TEXT, CategoryID.GUI,false));
        add(new Setting("enableCustomNPCGUI", NPC_GUI, CategoryID.GUI,true));
        add(new Setting("enableCustomMenuGUI",MENU_GUI, CategoryID.GUI,true));
        add(new Setting("allowGUIWhenDisabled", GUIS_WHEN_DISABLED, CategoryID.GENERAL, false));
        add(new Setting("showAbilityText", SHOW_ABILITY_TEXT, CategoryID.GUI,true));
        add(new Setting("infoBarOnTop", BB_ON_TOP, CategoryID.GUI,false));
        add(new Setting("separateDefence", USE_DEFENCE_OUT_OF_BB, CategoryID.GUI,false));
        add(new Setting("autoScaleInfoBar", USE_AUTO_SCALING_BB, CategoryID.GUI,true));
        add(new Setting("enableCompatibilitySafety", COMPATIBILITY_SAFETY, CategoryID.GENERAL,true));
        add(new Setting("hideGUIInF3", HIDE_IN_F3, CategoryID.GENERAL,false));
        add(new Setting("enableSnapInEditing", SNAP_IN_EDITING, CategoryID.GENERAL,true));
        //add(new Setting(SHOW_CACHED_PURSE_IN_DUNGEONS,true));
        add(new Setting("enableInfoBarAnimation", INFO_BOX_ANIMATION, CategoryID.GUI,true));
        add(new Setting("useInfoBarIcons", INFO_BOX_ICONS, CategoryID.GUI,true));
        add(new Setting("useOutlinedText", BORDER_TYPE, CategoryID.GUI,true));
        //add(new Setting(OVERWRITE_GUI,false));
        add(new Setting("showDebugMessages", SHOW_DEBUG_MESSAGES, CategoryID.GENERAL,true));
        add(new Setting("showHealText", HEAL_TEXT, CategoryID.GUI,false));
        add(new Setting("showEditGrid", EDIT_GRID, CategoryID.GENERAL,false));
        add(new Setting("separatePowderDisplay", SEPARATE_POWDER_DISPLAY, CategoryID.GUI, false));
        add(new Setting("showMithrilPowder", SHOW_MITHRIL_POWDER, CategoryID.GUI, false));
        add(new Setting("showGemstonePowder", SHOW_GEMSTONE_POWDER, CategoryID.GUI, false));
        add(new Setting("showSpeed", SHOW_SPEED, CategoryID.GUI, false));
        add(new Setting("showStrength", SHOW_STRENGTH, CategoryID.GUI, false));
        add(new Setting("showCritChance", SHOW_CRIT_CHANCE, CategoryID.GUI, false));
        add(new Setting("showCritDamage", SHOW_CRIT_DAMAGE, CategoryID.GUI, false));
        add(new Setting("showAttackSpeed", SHOW_ATTACK_SPEED, CategoryID.GUI, false));
        add(new Setting("showDrillFuelBar", DRILL_FUEL_BAR, CategoryID.GUI, true));
        add(new Setting("showSoulflow", SHOW_SOULFLOW, CategoryID.GUI, false));
        add(new Setting("separateSoulflowDisplay", SEPARATE_SOULFLOW_DISPLAY, CategoryID.GUI, false));
        add(new Setting("useCenteredAbilityText", CENTER_ABILITY_TEXT, CategoryID.GUI, false));
        add(new Setting("showWitherShield", WITHER_SHIELD, CategoryID.GUI, false));
        add(new Setting("useCenteredWitherShield", CENTER_WITHER_SHIELD, CategoryID.GUI, false));
    }};

    /** Hashmap that holds the titles and descriptions of each setting */
    public static HashMap<SettingID,Tuple<String,String>> settingData = new HashMap<SettingID, Tuple<String, String>>() {{
        put(AUTO_ENABLE,new Tuple<String, String>("Auto Toggle","The GUI will automatically enable/disable when you join/leave skyblock"));
        put(SHOW_WARNING,new Tuple<String, String>("Show Warning Icons","Show the warning icons under the status bars"));
        put(SHOW_POTIONS_EFFECTS,new Tuple<String, String>("Show potion effects","Show potion effects on the left side of the screen"));
        put(SHOW_DEBUG_MESSAGES,new Tuple<String, String>("Show Debug Messages","Shows messages in the chat about different events in the code"));
        put(OVERWRITE_GUI,new Tuple<String, String>("Overwrite GUIs","Overwrites any other mod's ingame gui (close and reopen when enabled)"));
        put(NPC_GUI,new Tuple<String, String>("Npc GUIs", "Custom GUIs for Npcs"));
        put(MENU_GUI,new Tuple<String, String>("Menu GUIs", "Custom GUIs for menus"));
        put(INVENTORY_TRAFFIC,new Tuple<String, String>("Show Inventory Traffic", "Show what items are going in and out of the inventory"));
        put(HIDE_IN_F3,new Tuple<String, String>("Hide in F3", "Hides certain elements while in f3"));
        put(SHOW_SKILL_XP,new Tuple<String, String>("Show skill xp", "When you get skill xp it will appear on the screen"));
        put(BORDER_TYPE,new Tuple<String, String>("Black border text", "When disabled the text will show without the black border"));
        put(COMPACT_POTION,new Tuple<String, String>("Compact potion display","Shows 2 potions per line"));
        put(ITEM_HIGHLIGHT_TEXT,new Tuple<String, String>("Normal tooltip text","Shows the tooltip text of an item centered with the hotbar"));
        put(HP_BAR,new Tuple<String, String>("Show HP Bar","Toggles on or off the hp bar"));
        put(MP_BAR,new Tuple<String, String>("Show MP Bar","Toggles on or off the mp bar"));
        put(XP_BAR,new Tuple<String, String>("Show XP Bar","Toggles on or off the xp bar"));
        put(HIDE_NIGHT_VISION,new Tuple<String, String>("Don't show night vision","Don't show the night vision effect in the effect list"));
        put(ALWAYS_SHOW_SKILL,new Tuple<String, String>("Always show skill xp","Constantly shows skill xp, using cached values when not normally shown"));
        put(SHOW_CACHED_PURSE_IN_DUNGEONS,new Tuple<String, String>("Show cached purse","Shows a cached value of the purse while in dungeons"));
        put(SHOW_AIR_BAR,new Tuple<String, String>("Show air bar","Toggles on or off the air bar"));
        put(SNAP_IN_EDITING,new Tuple<String, String>("Snap in gui editing","Activates snapping while in the gui customization menu"));
        put(INFO_BOX_ANIMATION,new Tuple<String, String>("Bottom Bar Animation","A slide down animation for the bottom box when i chat"));
        put(SHOW_ABILITY_TEXT,new Tuple<String, String>("Show ability text","Shows the ability text at the mana bar"));
        put(SHOW_ABSORPTION_BAR,new Tuple<String, String>("Show absorption bar","Shows the absorption bar"));
        put(SHOW_OP_BAR,new Tuple<String, String>("Show overflow mana bar","Shows the overflow mana bar"));
        put(HP_TEXT,new Tuple<String, String>("Show HP Text","Shows the HP text"));
        put(MP_TEXT,new Tuple<String, String>("Show MP Text","Shows the MP text"));
        put(XP_TEXT,new Tuple<String, String>("Show XP Text","Shows the XP text"));
        put(AIR_TEXT,new Tuple<String, String>("Show Air text","Shows the air text"));
        put(SKILL_TEXT,new Tuple<String, String>("Show Skill text","Shows the skill text"));
        put(SHOW_CURRENT_SERVER,new Tuple<String, String>("Show current server","Show in which server you are in"));
        put(COLORED_SKILL_XP,new Tuple<String,String>("Colored skill xp","Shows a different colored bar for each skill xp"));
        put(SHOW_EFFECTS_AS_IN_TAB,new Tuple<String,String>("Show tab effect","Show the one rolling effect that appears in tab"));
        put(INFO_BOX_ICONS,new Tuple<String, String>("Bottom bar icons", "Shows icons for each stat instead of text"));
        put(USE_AUTO_SCALING_BB,new Tuple<String, String>("Bottom bar autoscaling","The bottom bar auto-scales based on the gui scale"));
        put(GUIS_WHEN_DISABLED,new Tuple<String, String>("Allow GUIs when inactive","Allow GUIs when the Ingame GUI is not enabled"));
        put(USE_DEFENCE_OUT_OF_BB,new Tuple<String, String>("Defence outside bar","Shows the defence outside the bottom bar"));
        put(COMPATIBILITY_SAFETY,new Tuple<String, String>("Compatibility Safety","Ensures that certain features that might break some mods are disabled"));
        put(BB_ON_TOP,new Tuple<String,String>("Bottom bar on top","Puts the bottom bar on top instead of on the bottom"));
        put(CUSTOM_TOOL_TIP,new Tuple<String,String>("Custom tool tip","Shows extra icons above the item tool tip."));
        put(HEAL_TEXT,new Tuple<String, String>("Separate Healing Text","Separates the Healing text from the HP Display."));
        put(EDIT_GRID,new Tuple<String,String>("Edit Grid","Shows a grid in editing mode."));
        put(SEPARATE_POWDER_DISPLAY, new Tuple<String,String> ("Seperate Powder Display","Separates mithril/gemstone powder from the bottom bar."));
        put(SHOW_MITHRIL_POWDER, new Tuple<String,String> ("Show Mithril Powder","Show mithril powder."));
        put(SHOW_GEMSTONE_POWDER, new Tuple<String,String> ("Show Gemstone Powder","Show gemstone powder."));
        put(SHOW_SPEED, new Tuple<String,String> ("Show Speed","Show speed in the bottom bar."));
        put(SHOW_STRENGTH, new Tuple<String,String> ("Show Strength","Show strength in the bottom bar."));
        put(SHOW_CRIT_CHANCE, new Tuple<String,String> ("Show Crit Chance","Show crit chance in the bottom bar."));
        put(SHOW_CRIT_DAMAGE, new Tuple<String,String> ("Show Crit Damage","Show crit damage in the bottom bar."));
        put(SHOW_ATTACK_SPEED, new Tuple<String,String> ("Show Attack Speed","Show attack speed in the bottom bar."));
        put(DRILL_FUEL_BAR, new Tuple<String,String> ("Show Drill Fuel","Show a fuel bar for current drill."));
        put(SHOW_SOULFLOW, new Tuple<String,String> ("Show Soulflow","Show soulflow. Must have Soulflow Accessory in inventory."));
        put(SEPARATE_SOULFLOW_DISPLAY, new Tuple<String,String> ("Separate Soulflow Display","Separates soulfow from the bottom bar."));
        put(CENTER_ABILITY_TEXT, new Tuple<String,String> ("Center Abilities","Centers the Ability Text."));
        put(WITHER_SHIELD, new Tuple<String,String> ("Show Wither Shield","Shows if Wither Shield is ready."));
        put(CENTER_WITHER_SHIELD, new Tuple<String,String> ("Center Wither Shield","Centers the Wither Shield CD."));
        put(SHOW_OBJECTIVE, new Tuple<String, String>("Show objective","Show objective in the display panel"));
    }};

    /** Cache for setting states */
    private final HashMap<SettingID,Boolean> stateCache = new HashMap<SettingID,Boolean>();

    /**
     * @param settingID = The setting id of the referred setting
     * @return Returns the enable state of the setting
     */
    public boolean getSettingState(SettingID settingID){
        synchronized (stateCache) {
            Boolean state = stateCache.get(settingID);
            if (state != null) return state;
            for (Setting s : settings) {
                if (s.settingID == settingID) {
                    stateCache.put(s.settingID, s.enabled);
                    return s.enabled;
                }
            }
            return false;
        }
    }

    /**
     * @brief Sets the enable state of a setting
     * @param settingID = The setting id of the referred setting
     * @param state = The new state of the setting
     */
    public void setSettingState(SettingID settingID,boolean state) {
        stateCache.clear();
        for (Setting s : settings) {
            if (s.settingID == settingID) {
                s.enabled = state;
                break;
            }
        }
        MinecraftForge.EVENT_BUS.post(new ApecSettingChangedState(settingID,state));
        //this.SaveLegacySettings();
        this.SaveSettings();
    }

    /**
     * @brief Sets the enable state of a setting without overwriting the save data, used on initial load
     * @param settingID = The setting id of the referred setting
     * @param state = The new state of the setting
     */
    public void setSettingStateWithNoSaving(SettingID settingID, boolean state) {
        stateCache.clear();
        for (Setting s : settings) {
            if (s.settingID == settingID) {
                s.enabled = state;
                break;
            }
        }
        MinecraftForge.EVENT_BUS.post(new ApecSettingChangedState(settingID,state));
    }

    /**
     * @param settingID = The setting id of the referred setting
     * @return Returns a string,string tuple that contains the title and the description of the setting
     */
    public static Tuple<String,String> getNameAndDesc (SettingID settingID) {
        return settingData.get(settingID);
    }

    public void SaveSettings() {
        try {
            new File("config/Apec").mkdirs();
            new File("config/Apec/Settings.json").createNewFile();
            FileWriter fw = new FileWriter("config/Apec/Settings.json");

            JSONObject array = new JSONObject();

            JSONObject general = new JSONObject();
            general.put("version", ApecMain.version);

            JSONObject settings = new JSONObject();

            for (Setting setting : this.settings) {
                settings.put(setting.settingKey, setting.enabled);
            }

            array.put("general", general);
            array.put("settings", settings);

            fw.write(array.toString(1));
            fw.close();
        } catch (Exception e) {
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error saving settings!");
        }

    }

    public void LoadSettings() {
        try{
            File file = new File("config/Apec/Settings.json");
            String json = new Scanner(file).useDelimiter("\\Z").next();
            JSONObject loaded = new JSONObject(json.toString());
            String version = loaded.getJSONObject("general").getString("version");

            JSONObject settings = loaded.getJSONObject("settings");
            for (Setting setting : this.settings){
                if (settings.has(setting.settingKey))
                this.setSettingStateWithNoSaving(setting.settingID,settings.getBoolean(setting.settingKey));
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error reading settings!");
        }
    }

    /**
     * @brief Loads and sets the setting data from disk (legacy support)
     */
    public void LoadLegacySettings() {
        try {
            File legacy = new File("config/Apec/Settings.txt");
            Scanner scanner = new Scanner(legacy);
            while (scanner.hasNextLine()) {
                String s = scanner.nextLine();
                try {
                    String tempSplit[] = s.split("-");
                    if (tempSplit.length != 2)
                        continue;
                    int idx = Integer.parseInt(tempSplit[0]);
                    boolean status = false;
                    if (tempSplit[1].equals("t")) 
                        status = true;
                    this.setSettingStateWithNoSaving(SettingID.values()[idx],status);
                }
                catch (Exception e) {
                    ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error reading \"" + s + "\"!");
                }
            }
            scanner.close();

            boolean deleted = legacy.delete(); // delete legacy settings file.
            if(deleted){
                this.SaveSettings();
                System.out.println("[\u00A72Apec\u00A7f] Deleted legacy settings file.");
            }
        } catch (IOException e) {
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error reading legacy settings!");
        }
    }

    /**
     * @brief Saves the setting data to disk (legacy support)
     */
    public void SaveLegacySettings() {
        try {
            new File("config/Apec").mkdirs();
            new File("config/Apec/Settings.txt").createNewFile();
            FileWriter fw = new FileWriter("config/Apec/Settings.txt");
            String s = "";
            for (int i = 0;i < settings.size();i++) {
                s += settings.get(i).settingID.name() + "-" + (settings.get(i).enabled ? "t" : "f");
                if (i != settings.size() - 1) s += "\n";
            }
            fw.write(s);
            fw.close();
        } catch (IOException e) {
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error saving legacy settings!");
        }
    }

}