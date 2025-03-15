package uk.co.hexeption.apec.settings;

import net.minecraft.util.Tuple;
import uk.co.hexeption.apec.Apec;

public class Setting {

    public SettingID settingID;

    /** String main name, appears as the title in the settings menu */
    public String name;

    /** The description of the settings appears under the title in the settings menu */
    public String description;

    /** Holds the enable state */
    public boolean enabled = false;

    /**
     * @param settingID = The setting id
     * @param def = Default enable value
     */
    public Setting (SettingID settingID,boolean def) {
        this.settingID = settingID;
        Tuple<String,String> t = SettingsManager.getNameAndDesc(this.settingID);
        this.name = t.getA();
        this.description = t.getB();
        this.enabled = def;
    }

    /**
     * @brief Toggles the state of the setting
     */
    public void Toggle() {
        Apec.INSTANCE.settingsManager.setSettingState(this.settingID,!this.enabled);
    }


}
