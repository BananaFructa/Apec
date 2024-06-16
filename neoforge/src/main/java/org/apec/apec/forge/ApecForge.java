package org.apec.apec.forge;

import dev.architectury.platform.forge.EventBuses;
import org.apec.apec.Apec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("apec")
public class ApecForge {
    public ApecForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus("apecce", FMLJavaModLoadingContext.get().getModEventBus());
        Apec.getInstance().init(Apec.ModLoader.FORGE);
    }
}