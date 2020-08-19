package Apec;

import Apec.Commands.ApecGuiOpenCommand;
import Apec.Commands.ApecMenuOpenCommand;
import Apec.Components.Gui.ContainerGuis.AuctionHouseComponent;
import Apec.Components.Gui.ContainerGuis.ChestGuiComponent;
import Apec.Components.Gui.ContainerGuis.SkillViewComponent;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.Components.Gui.GuiIngame.GuiElements.GUIComponent;
import Apec.Components.Gui.Menu.ApecMenu;
import Apec.Settings.SettingID;
import Apec.Settings.SettingsManager;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = ApecMain.modId, version = ApecMain.version, name = ApecMain.name)
public class ApecMain
{


    /**
     * Don't read that -> You lost
     */


    public static final String modId = "apec"; 
    public static final String name = "Apec";
    public static final String version = "1.6";

    public static ApecMain Instance;

    KeyBinding guiKey = new KeyBinding("Apec Gui", Keyboard.KEY_RCONTROL, "Apec");
    KeyBinding menuKey = new KeyBinding("Apec Settings Menu", Keyboard.KEY_M, "Apec");

    public DataExtractor dataExtractor = new DataExtractor();
    public InventorySubtractor inventorySubtractor = new InventorySubtractor();
    public SettingsManager settingsManager = new SettingsManager();
    public ContainerGuiManager containerGuiManager = new ContainerGuiManager();

    public String newestVersion = "";

    public List<Component> components = new ArrayList<Component>() {{
        add(new GUIModifier());
        add(new ApecMenu());
        add(new AuctionHouseComponent());
        add(new SkillViewComponent());
    }};

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Instance = this;
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(inventorySubtractor);
        MinecraftForge.EVENT_BUS.register(dataExtractor);
        MinecraftForge.EVENT_BUS.register(containerGuiManager);
        ClientRegistry.registerKeyBinding(guiKey);
        ClientRegistry.registerKeyBinding(menuKey);

        for (Component component : components) {
            MinecraftForge.EVENT_BUS.register(component);
        }

        ClientCommandHandler.instance.registerCommand(new ApecMenuOpenCommand() { });
        ClientCommandHandler.instance.registerCommand(new ApecGuiOpenCommand() { });

        newestVersion = VersionChecker.getVersion();

        this.settingsManager.LoadSettings();

        containerGuiManager.init();

    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (guiKey.isPressed()) {
            getComponent(ComponentId.GUI_MODIFIER).Toggle();
        } else if (menuKey.isPressed()) {
            getComponent(ComponentId.SETTINGS_MENU).Toggle();
        }
    }

    public Component getComponent(ComponentId componentId) {
        for (Component component : components) {
            if (component.componentId == componentId) return component;
        }
        return null;
    }
}
