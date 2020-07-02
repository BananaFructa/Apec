package Apec;

import java.io.Console;

public class Component {

    private boolean Enabled = false;

    public ComponentId componentId;

    public Component(ComponentId componentId) {
        this.componentId = componentId;
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

}
