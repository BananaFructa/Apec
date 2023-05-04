package org.apecce.apecce;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Data;
import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import me.zero.alpine.listener.Listener;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apecce.apecce.events.Render2D;
import org.apecce.apecce.managers.ModuleManager;
import org.apecce.apecce.utils.LogHelper;

@Data
public class ApecCE {
    private static ApecCE instance;
    private Logger logger = LogManager.getLogger(MOD_ID);

    public static final String MOD_ID = "ApecCE";

    public EventBus eventBus = EventManager.builder().setName("ApecCE Event Bus").setSuperListeners().build();
    public ModuleManager moduleManager = new ModuleManager();
    public ModLoader modLoader;

    public static ApecCE getInstance() {
        return instance == null ? instance = new ApecCE() : instance;
    }

    public void init(ModLoader modLoader) {
        this.modLoader = modLoader;

        LogHelper.section("Initializing ApecCE (" + modLoader.name() + ")");
        StopWatch stopWatch = StopWatch.createStarted();
        moduleManager.initialize();

        eventBus.subscribe(new Listener<Render2D>(render2D -> MC.mc.gui.getFont().drawShadow(render2D.poseStack(), "ApecCE", 2, 2, -1)));

        LogHelper.section("Initialized ApecCE (" + stopWatch.getTime() + "ms)");
    }

    public void postInit() {
        moduleManager.postInitialize();
    }


    public enum ModLoader {
        FABRIC,
        FORGE
    }
}