package Apec.Settings;

import Apec.Utils.ApecUtils;
import net.minecraft.util.Tuple;

import java.io.File;
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
        add(new Setting(AUTO_ENABLE,true));
        add(new Setting(SettingID.HP_BAR,true));
        add(new Setting(SettingID.HP_TEXT,true));
        add(new Setting(SettingID.SHOW_ABSORPTION_BAR,true));
        add(new Setting(MP_BAR,true));
        add(new Setting(MP_TEXT,true));
        add(new Setting(XP_BAR,true));
        add(new Setting(XP_TEXT,true));
        add(new Setting(SHOW_AIR_BAR,true));
        add(new Setting(AIR_TEXT,true));
        add(new Setting(SHOW_SKILL_XP,true));
        add(new Setting(SKILL_TEXT,true));
        add(new Setting(COLORED_SKILL_XP,true));
        add(new Setting(ALWAYS_SHOW_SKILL,false));
        add(new Setting(SHOW_WARNING,true));
        add(new Setting(INVENTORY_TRAFFIC,true));
        add(new Setting(SHOW_POTIONS_EFFECTS,true));
        add(new Setting(COMPACT_POTION,false));
        //add(new Setting(HIDE_NIGHT_VISION,false));
        add(new Setting(SHOW_EFFECTS_AS_IN_TAB,true));
        add(new Setting(SHOW_CURRENT_SERVER,false));
        add(new Setting(ITEM_HIGHLIGHT_TEXT,false));
        add(new Setting(NPC_GUI,true));
        add(new Setting(MENU_GUI,true));
        add(new Setting(GUIS_WHEN_DISABLED, false));
        add(new Setting(SHOW_ABILITY_TEXT,true));
        add(new Setting(USE_DEFENCE_OUT_OF_BB,false));
        add(new Setting(COMPATIBILITY_SAFETY,true));
        add(new Setting(HIDE_IN_F3,false));
        add(new Setting(SNAP_IN_EDITING,true));
        //add(new Setting(SHOW_CACHED_PURSE_IN_DUNGEONS,true));
        add(new Setting(INFO_BOX_ANIMATION,true));
        add(new Setting(INFO_BOX_ICONS,true));
        add(new Setting(USE_AUTO_SCALING_BB,true));
        add(new Setting(BORDER_TYPE,true));
        //add(new Setting(OVERWRITE_GUI,false));
        add(new Setting(SHOW_DEBUG_MESSAGES,false));
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
        put(HP_TEXT,new Tuple<String, String>("Show HP Text","Shows the HP text"));
        put(MP_TEXT,new Tuple<String, String>("Show MP Text","Shows the MP text"));
        put( XP_TEXT,new Tuple<String, String>("Show XP Text","Shows the XP text"));
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
    }};

    /**
     * @param settingID = The setting id of the referred setting
     * @return Returns the enable state of the setting
     */
    public boolean getSettingState(SettingID settingID){
        for (Setting s : settings) {
            if (s.settingID == settingID) return s.enabled;
        }
        return false;
    }

    /**
     * @brief Sets the enable state of a setting
     * @param settingID = The setting id of the referred setting
     * @param state = The new state of the setting
     */
    public void setSettingState(SettingID settingID,boolean state) {
        for (Setting s : settings) {
            if (s.settingID == settingID) {
                s.enabled = state;
                break;
            }
        }
        this.SaveSettings();
    }

    /**
     * @brief Sets the enable state of a setting without overwriting the save data, used on initial load
     * @param settingID = The setting id of the referred setting
     * @param state = The new state of the setting
     */
    public void setSettingStateWithNoSaveing(SettingID settingID,boolean state) {
        for (Setting s : settings) {
            if (s.settingID == settingID) {
                s.enabled = state;
                break;
            }
        }
    }

    /**
     * @param settingID = The setting id of the referred setting
     * @return Returns a string,string tuple that contains the title and the description of the setting
     */
    public static Tuple<String,String> getNameAndDesc (SettingID settingID) {
        return settingData.get(settingID);
    }

    /**
     * @brief Saves the setting data to disk
     */
    public void SaveSettings() {
        try {
            new File("config/Apec").mkdirs();
            new File("config/Apec/Settings.txt").createNewFile();
            FileWriter fw = new FileWriter("config/Apec/Settings.txt");
            String s = "";
            for (int i = 0;i < settings.size();i++) {
                s += settings.get(i).settingID.ordinal() + "-" + (settings.get(i).enabled ? "t" : "f");
                if (i != settings.size() - 1) s += "\n";
            }
            fw.write(s);
            fw.close();
        } catch (IOException e) {
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error saving settings!");
        }
    }

    /**
     * @brief Loads and sets the setting data from disk
     */
    public void LoadSettings() {
        try {
            Scanner scanner = new Scanner(new File("config/Apec/Settings.txt"));
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
                    this.setSettingStateWithNoSaveing(SettingID.values()[idx],status);
                }
                catch (Exception e) {
                    ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error reading \"" + s + "\"!");
                }
            }
            scanner.close();
        } catch (IOException e) {
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error reading settings!");
        }
    }

}
