package Apec.Settings;

import Apec.ApecMain;
import net.minecraft.util.Tuple;

public class Setting {

    public SettingID settingID;
    /** String main key, appears as the title in the config */
    public String settingKey;
    /** String main name, appears as the title in the settings menu */
    public String name;
    /** The description of the settings appears under the title in the settings menu */
    public String description;
    /** The category the settings appears in */
    public CategoryID category;
    /** Holds the enable state */
    public boolean enabled = false;

    /**
     * @param settingID = The setting id
     * @param def = Default enable value
     */
    public Setting (String settingKey, SettingID settingID, CategoryID category, boolean def) {
        this.settingKey = settingKey;
        this.settingID = settingID;
        Tuple<String,String> t = SettingsManager.getNameAndDesc(this.settingID);
        this.name = t.getFirst();
        this.description = t.getSecond();
        this.enabled = def;
    }

    /**
     * @brief Toggles the state of the setting
     */
    public void Toggle() {
        ApecMain.Instance.settingsManager.setSettingState(this.settingID,!this.enabled);
    }

}
