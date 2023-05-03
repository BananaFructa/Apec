package org.apecce.apecce.fabric;

import org.apecce.apecce.ApecCE;
import net.fabricmc.api.ModInitializer;

public class ApecCEFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ApecCE.init();
    }
}