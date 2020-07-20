package Apec.Settings;

import Apec.ApecMain;
import net.minecraft.util.Tuple;

public class Setting {

    public SettingID settingID;
    public String name;
    public String description;
    public boolean enabled = false;

    public Setting (SettingID settingID,boolean def) {
        this.settingID = settingID;
        Tuple<String,String> t = SettingsManager.getNameAndDesc(this.settingID);
        this.name = t.getFirst();
        this.description = t.getSecond();
        this.enabled = def;
    }

    public void Toggle() {
        ApecMain.Instance.settingsManager.setSettingState(this.settingID,!this.enabled);
    }

}
