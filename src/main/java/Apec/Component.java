package Apec;

import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;

public class Component {


    private boolean Enabled = false;

    public ComponentId componentId;
    public HashMap<Integer,String> DataToSave = new HashMap<Integer, String>();

    /**
     * @param componentId = The id by which the component identifies
     */
    public Component(ComponentId componentId) {
        this.componentId = componentId;
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * @brief Initialization function
     */
    public void init() {

    }

    /**
     * @brief Function called when the component is toggled
     */
    public void Toggle() {
        this.Enabled = !this.Enabled;
        if (this.Enabled) this.onEnable();
        else this.onDisable();
    }

    /**
     * @brief Function called when the component is enabled
     */
    protected void onEnable() {

    }

    /**
     * @brief Function called when the component is disabled
     */
    protected void onDisable() {

    }

    /**
     * @return Returns true if the component is enabled, false otherwise
     */
    public boolean getEnableState() {
        return this.Enabled;
    }

    /**
     * @brief Used to set data that the component has saved on disk
     * @param Data = An integer string hash map where the key specifies the data id and the string representing the data
     */
    public void loadSavedData(HashMap<Integer,String> Data) {
        for (Integer key : Data.keySet()) {
            DataToSave.put(key,Data.get(key));
        }
    }

    /**
     * @return Returns true if the component has any data to save on disk
     */
    public boolean hasDataToSave() {
        return DataToSave.size() != 0;
    }

}
