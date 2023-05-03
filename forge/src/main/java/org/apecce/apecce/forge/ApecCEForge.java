package org.apecce.apecce.forge;

import dev.architectury.platform.forge.EventBuses;
import org.apecce.apecce.ApecCE;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ApecCE.MOD_ID)
public class ApecCEForge {
    public ApecCEForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(ApecCE.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
            ApecCE.init();
    }
}