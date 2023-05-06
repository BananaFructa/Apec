package org.apec.apec.fabric;

import net.fabricmc.api.ClientModInitializer;
import org.apec.apec.Apec;

public class ApecFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Apec.getInstance().init(Apec.ModLoader.FABRIC);
        Apec.getInstance().getLogger().info("ApecCE initialized");
    }


}