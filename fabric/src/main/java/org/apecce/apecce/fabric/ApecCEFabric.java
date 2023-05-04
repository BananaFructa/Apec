package org.apecce.apecce.fabric;

import net.fabricmc.api.ClientModInitializer;
import org.apecce.apecce.ApecCE;

public class ApecCEFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ApecCE.getInstance().init(ApecCE.ModLoader.FABRIC);
        ApecCE.getInstance().getLogger().info("ApecCE initialized");
    }


}