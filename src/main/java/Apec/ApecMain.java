package Apec;

import Apec.Components.Gui.GUIModifier;
import com.sun.jna.platform.unix.X11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.http.impl.conn.IdleConnectionHandler;
import org.lwjgl.input.Keyboard;
import scala.collection.parallel.ParIterableLike;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.xml.crypto.Data;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Mod(modid = ApecMain.modId, version = ApecMain.version, name = ApecMain.name)
public class ApecMain
{

    /**
     * Don't read that -> You lost
     */


    public static final String modId = "apec";
    public static final String name = "Apec";
    public static final String version = "1.2";

    public static ApecMain Instance;

    KeyBinding guiKey = new KeyBinding("Apec Gui", Keyboard.KEY_RCONTROL, "key.categories.misc");

    public DataExtractor dataExtractor = new DataExtractor();
    public InventorySubtractor inventorySubtractor = new InventorySubtractor();

    String newestVersion = "";

    public List<Component> components = new ArrayList() {{
        add(new GUIModifier());
    }};

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Instance = this;
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(inventorySubtractor);
        MinecraftForge.EVENT_BUS.register(dataExtractor);
        ClientRegistry.registerKeyBinding(guiKey);

        newestVersion = VersionChecker.getVersion();

    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (guiKey.isPressed()) {
            components.get(0).Toggle(); // Dont worry propper keybinding will be real in 54 minutes
        }
    }

    private boolean notificationShown = false;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        // TODO: make some better way of doing this
        if (!notificationShown && Minecraft.getMinecraft().thePlayer != null && !ApecMain.version.equals(newestVersion)) {
            ChatComponentText msg = new ChatComponentText("[\u00A72Apec\u00A7f] There is a new version of Apec available! Click on this message to go to the CurseForge page.");
            msg.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://www.curseforge.com/minecraft/mc-mods/apec"));
            Minecraft.getMinecraft().thePlayer.addChatMessage(msg);
            notificationShown = true;
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        notificationShown = false;
    }

    public Object getComponent(ComponentId componentId) {
        for (Component component : components) {
            if (component.componentId == componentId) return component;
        }
        return null;
    }
}
