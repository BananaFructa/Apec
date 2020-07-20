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
        add(new Setting(SettingID.AUTO_ENABLE,false));
        add(new Setting(SettingID.SHOW_WARNING,true));
        add(new Setting(SettingID.SHOW_POTIONS_EFFECTS,true));
        add(new Setting(SettingID.SHOW_DEBUG_MESSAGES,false));
        add(new Setting(SettingID.OVERWRITE_GUI,false));
        add(new Setting(SettingID.NPC_GUI,true));
        add(new Setting(SettingID.INVENTORY_TRAFFIC,true));
        add(new Setting(SettingID.HIDE_IN_F3,false));
        add(new Setting(SettingID.SHOW_SKILL_XP,true));
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
