package Apec.Events;

import Apec.Settings.SettingID;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ApecSettingChangedState extends Event {
    public SettingID settingID;
    public boolean state;
    
    public ApecSettingChangedState(SettingID settingID,boolean state) {
        this.settingID = settingID;
        this.state = state;
    }
    
}
