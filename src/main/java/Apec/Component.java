package Apec;

import net.minecraftforge.common.MinecraftForge;

import java.io.Console;
import java.util.HashMap;

public class Component {

    private boolean Enabled = false;

    public ComponentId componentId;
    public HashMap<Integer,String> DataToSave = new HashMap<Integer, String>();

    public Component(ComponentId componentId) {
        this.componentId = componentId;
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void init() {

    }

    public void Toggle() {
        this.Enabled = !this.Enabled;
        if (this.Enabled) this.onEnable();
        else this.onDisable();
    }

    protected void onEnable() {

    }

    protected void onDisable() {

    }

    public boolean getEnableState() {
        return this.Enabled;
    }

    public void loadSavedData(HashMap<Integer,String> Data) {
        for (Integer key : Data.keySet()) {
            DataToSave.put(key,Data.get(key));
        }
    }

    public boolean hasDataToSave() {
        return DataToSave.size() != 0;
    }

}
