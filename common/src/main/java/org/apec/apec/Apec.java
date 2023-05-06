package org.apec.apec;

import lombok.Data;
import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import me.zero.alpine.listener.Listener;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apec.apec.events.Render2D;
import org.apec.apec.managers.ModuleManager;
import org.apec.apec.skyblock.SkyBlockInfo;
import org.apec.apec.utils.LogHelper;

@Data
public class Apec {
    private static Apec instance;
    private Logger logger = LogManager.getLogger(MOD_ID);

    public static final String MOD_ID = "Apec";

    public EventBus eventBus = EventManager.builder().setName("Apec Event Bus").setSuperListeners().build();
    public ModuleManager moduleManager = new ModuleManager();
    public ModLoader modLoader;

    public static Apec getInstance() {
        return instance == null ? instance = new Apec() : instance;
    }

    public void init(ModLoader modLoader) {
        this.modLoader = modLoader;

        LogHelper.section("Initializing Apec (" + modLoader.name() + ")");
        StopWatch stopWatch = StopWatch.createStarted();
        moduleManager.initialize();
        eventBus.subscribe(SkyBlockInfo.getInstance());
        eventBus.subscribe(new Listener<Render2D>(render2D -> MC.mc.gui.getFont().drawShadow(render2D.poseStack(), "Apec", 2, 2, -1)));

        LogHelper.section("Initialized Apec (" + stopWatch.getTime() + "ms)");
    }

    public void postInit() {
        moduleManager.postInitialize();
    }


    public enum ModLoader {
        FABRIC,
        FORGE
    }
}