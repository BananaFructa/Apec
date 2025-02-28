package uk.co.hexeption.neoforge.apec;

import net.neoforged.fml.common.Mod;

import uk.co.hexeption.apec.Apec;

@Mod(Apec.MOD_ID)
public final class ExampleModNeoForge {
    public ExampleModNeoForge() {
        // Run our common setup.
        Apec.init();
    }
}
