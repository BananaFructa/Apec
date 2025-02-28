package uk.co.hexeption.fabric.apec;

import net.fabricmc.api.ModInitializer;

import uk.co.hexeption.apec.Apec;

public final class ExampleModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        Apec.init();
    }


}
