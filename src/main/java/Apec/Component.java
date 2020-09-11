package Apec;

import net.minecraftforge.common.MinecraftForge;

import java.io.Console;

public class Component {

    private boolean Enabled = false;

    public ComponentId componentId;

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

}
