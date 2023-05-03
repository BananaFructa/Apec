package org.apecce.apecce.forge;

import dev.architectury.platform.forge.EventBuses;
import org.apecce.apecce.ApecCE;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Locale;

@Mod("apecce")
public class ApecCEForge {
    public ApecCEForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus("apecce", FMLJavaModLoadingContext.get().getModEventBus());
        ApecCE.getInstance().init(ApecCE.ModLoader.FORGE);
    }
}