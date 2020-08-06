package Apec.Settings;

import Apec.ApecUtils;
import net.minecraft.util.Tuple;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Scanner;

public class SettingsManager {

    public List<Setting> settings = new ArrayList<Setting>() {{
        add(new Setting(SettingID.AUTO_ENABLE,true));
        add(new Setting(SettingID.HP_BAR,true));
        add(new Setting(SettingID.SHOW_ABSORPTION_BAR,true));
        add(new Setting(SettingID.MP_BAR,true));
        add(new Setting(SettingID.XP_BAR,true));
        add(new Setting(SettingID.SHOW_AIR_BAR,true));
        add(new Setting(SettingID.SHOW_SKILL_XP,true));
        add(new Setting(SettingID.ALWAYS_SHOW_SKILL,false));
        add(new Setting(SettingID.SHOW_WARNING,true));
        add(new Setting(SettingID.INVENTORY_TRAFFIC,true));
        add(new Setting(SettingID.SHOW_POTIONS_EFFECTS,true));
        add(new Setting(SettingID.COMPACT_POTION,false));
        add(new Setting(SettingID.HIDE_NIGHT_VISION,false));
        add(new Setting(SettingID.ITEM_HIGHLIGHT_TEXT,false));
        add(new Setting(SettingID.NPC_GUI,true));
        add(new Setting(SettingID.SHOW_ABILITY_TEXT,true));
        add(new Setting(SettingID.HIDE_IN_F3,false));
        add(new Setting(SettingID.SNAP_IN_EDITING,true));
        add(new Setting(SettingID.SHOW_CACHED_PURSE_IN_DUNGEONS,true));
        add(new Setting(SettingID.INFO_BOX_ANIMATION,true));
        add(new Setting(SettingID.BORDER_TYPE,true));
        add(new Setting(SettingID.OVERWRITE_GUI,false));
        add(new Setting(SettingID.SHOW_DEBUG_MESSAGES,false));
    }};

    public boolean getSettingState(SettingID settingID){
        for (Setting s : settings) {
            if (s.settingID == settingID) return s.enabled;
        }
        return false;
    }

    public void setSettingState(SettingID settingID,boolean state) {
        for (Setting s : settings) {
            if (s.settingID == settingID) {
                s.enabled = state;
                break;
            }
        }
        this.SaveSettings();
    }

    public static Tuple<String,String> getNameAndDesc (SettingID settingID) {
        switch (settingID) {
            case AUTO_ENABLE:
                return new Tuple<String, String>("Auto Toggle","The GUI will automatically enable/disable when you join/leave skyblock");
            case SHOW_WARNING:
                return new Tuple<String, String>("Show Warning Icons","Show the warning icons under the status bars");
            case SHOW_POTIONS_EFFECTS:
                return new Tuple<String, String>("Show potion effects","Show potion effects on the left side of the screen");
            case SHOW_DEBUG_MESSAGES:
                return new Tuple<String, String>("Show Debug Messages","Shows messages in the chat about different events in the code");
            case OVERWRITE_GUI:
                return new Tuple<String, String>("Overwrite GUIs","Overwrites any other mod's ingame gui (close and reopen when enabled)");
            case NPC_GUI:
                return new Tuple<String, String>("Npc GUIs", "Custom GUIs for Npcs");
            case INVENTORY_TRAFFIC:
                return new Tuple<String, String>("Show Inventory Traffic", "Show what items are going in and out of the inventory");
            case HIDE_IN_F3:
                return new Tuple<String, String>("Hide in F3", "Hides certain elements while in f3");
            case SHOW_SKILL_XP:
                return new Tuple<String, String>("Show skill xp", "When you get skill xp it will appear on the screen");
            case BORDER_TYPE:
                return new Tuple<String, String>("Black border text", "When disabled the text will show without the black border");
            case COMPACT_POTION:
                return new Tuple<String, String>("Compact potion display","Shows 2 potions per line. As they are shown in the tab menu");
            case ITEM_HIGHLIGHT_TEXT:
                return new Tuple<String, String>("Normal tooltip text","Shows the tooltip text of an item centered with the hotbar");
            case HP_BAR:
                return new Tuple<String, String>("Show HP Bar","Toggles on or off the hp bar");
            case MP_BAR:
                return new Tuple<String, String>("Show MP Bar","Toggles on or off the mp bar");
            case XP_BAR:
                return new Tuple<String, String>("Show XP Bar","Toggles on or off the xp bar");
            case HIDE_NIGHT_VISION:
                return new Tuple<String, String>("Don't show night vision","Don't show the night vision effect in the effect list");
            case ALWAYS_SHOW_SKILL:
                return new Tuple<String, String>("Always show skill xp","Constantly shows skill xp, using cached values when not normally shown");
            case SHOW_CACHED_PURSE_IN_DUNGEONS:
                return new Tuple<String, String>("Show cached purse","Shows a cached value of the purse while in dungeons");
            case SHOW_AIR_BAR:
                return new Tuple<String, String>("Show air bar","Toggles on or off the air bar");
            case SNAP_IN_EDITING:
                return new Tuple<String, String>("Snap in gui editing","Activates snapping while in the gui customization menu");
            case INFO_BOX_ANIMATION:
                return new Tuple<String, String>("Bottom Bar Animation","A slide down animation for the bottom box when i chat");
            case SHOW_ABILITY_TEXT:
                return new Tuple<String, String>("Show ability text","Shows the ability text at the mana bar");
            case SHOW_ABSORPTION_BAR:
                return new Tuple<String, String>("Show absorption bar","Shows the absorption bar");
            default:
                return new Tuple<String, String>("N/A","N/A");
        }
    }

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

    public void LoadSettings() {
        try {
            Scanner scanner = new Scanner(new File("config/Apec/Settings.txt"));
            while (scanner.hasNextLine()) {
                String s = scanner.nextLine();
                String tempSplit[] = s.split("-");
                int idx = Integer.parseInt(tempSplit[0]);
                boolean status = false;
                if (tempSplit[1].equals("t")) status = true;
                else if (tempSplit[1].equals("f")) status = false;
                this.setSettingState(SettingID.values()[idx],status);
            }
            scanner.close();
        } catch (IOException e) {
            ApecUtils.showMessage("[\u00A72Apec\u00A7f] There was an error reading settings!");
        }
    }

}
