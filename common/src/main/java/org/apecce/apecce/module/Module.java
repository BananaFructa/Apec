package org.apecce.apecce.module;

import lombok.Data;
import me.zero.alpine.listener.EventSubscriber;
import org.apecce.apecce.ApecCE;
import org.apecce.apecce.MC;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Data
public class Module implements MC, EventSubscriber {

    private String name = getClass().getAnnotation(ModuleInfo.class).name();
    private String description = getClass().getAnnotation(ModuleInfo.class).description();

    private boolean state;

    public void setState(boolean state) {
        onToggle();
        this.state = state;
        if (state) {
            onEnable();
            ApecCE.getInstance().eventBus.subscribe(this);
        } else {
            onDisable();
            ApecCE.getInstance().eventBus.unsubscribe(this);
        }

    }

    // Used to toggle if using @ModuleEnabled
    public void toggle() {
        setState(!state);
    }

    protected void onDisable() {
    }

    protected void onEnable() {
    }

    protected void onToggle() {

    }

    public void postInit() {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface ModuleInfo {
        String name();

        String description();
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ModuleEnabled {

    }

}
