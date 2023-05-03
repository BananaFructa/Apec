package org.apecce.apecce.module;

import lombok.Data;
import me.zero.alpine.listener.EventSubscriber;
import org.apecce.apecce.ApecCE;
import org.apecce.apecce.MC;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Data
public class Module implements MC, EventSubscriber {

    private String name = getClass().getAnnotation(ModuleInfo.class).name();
    private String description = getClass().getAnnotation(ModuleInfo.class).description();

    private boolean state;

    public void setState(boolean state) {
        onToggle();
        if (mc.level != null) { // Don't enable if not in a world
            this.state = state;
            if (state) {
                onEnable();
                ApecCE.getInstance().eventBus.subscribe(this);
            } else {
                onDisable();
                ApecCE.getInstance().eventBus.unsubscribe(this);
            }
        }

    }

    protected void onDisable() {
    }

    protected void onEnable() {
    }

    protected void onToggle() {

    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface ModuleInfo {
        String name();

        String description();
    }

}
