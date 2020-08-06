package Apec;

import Apec.Commands.ApecGuiOpenCommand;
import Apec.Commands.ApecMenuOpenCommand;
import Apec.Components.Gui.ContainerGuis.AuctionHouseComponent;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.Components.Gui.Menu.ApecMenu;
import Apec.Components.Gui.Menu.CustomizationMenu.CustomizationGui;
import Apec.Settings.SettingID;
import Apec.Settings.SettingsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ChatComponentText;
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
import java.util.Arrays;
import java.util.List;

@Mod(modid = ApecMain.modId, version = ApecMain.version, name = ApecMain.name)
public class ApecMain
{


    /**
     * Don't read that -> You lost
     */


    public static final String modId = "apec"; 
    public static final String name = "Apec";
    public static final String version = "1.5.1";

    public static ApecMain Instance;

    KeyBinding guiKey = new KeyBinding("Apec Gui", Keyboard.KEY_RCONTROL, "Apec");
    KeyBinding menuKey = new KeyBinding("Apec Settings Menu", Keyboard.KEY_M, "Apec");

    public DataExtractor dataExtractor = new DataExtractor();
    public InventorySubtractor inventorySubtractor = new InventorySubtractor();
    public SettingsManager settingsManager = new SettingsManager();

    public String newestVersion = "";

    public List<Component> components = new ArrayList<Component>() {{
        add(new GUIModifier());
        add(new ApecMenu());
        add(new AuctionHouseComponent());
    }};

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Instance = this;
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(inventorySubtractor);
        MinecraftForge.EVENT_BUS.register(dataExtractor);
        ClientRegistry.registerKeyBinding(guiKey);
        ClientRegistry.registerKeyBinding(menuKey);

        for (Component component : components) {
            MinecraftForge.EVENT_BUS.register(component);
        }

        ClientCommandHandler.instance.registerCommand(new ApecMenuOpenCommand() { });
        ClientCommandHandler.instance.registerCommand(new ApecGuiOpenCommand() { });

        newestVersion = VersionChecker.getVersion();

        this.settingsManager.LoadSettings();

    }

     @SubscribeEvent
     public void onGui (GuiOpenEvent event) {
        if (this.settingsManager.getSettingState(SettingID.NPC_GUI)) {
            if (getComponent(ComponentId.GUI_MODIFIER).getEnableState() && event.gui instanceof GuiChest) {
                try {
                    /** This is to ensure that there is not an Inner class of the GuiChes class forced by a mod , ughh ughh looking at you Skypixel */
                    String upperFieldName = ApecUtils.unObfedFieldNames.get("upperChestInventory");
                    String lowerFieldName = ApecUtils.unObfedFieldNames.get("lowerChestInventory");
                    if (ApecUtils.isNameInFieldList(event.gui.getClass().getDeclaredFields(),upperFieldName) &&
                        ApecUtils.isNameInFieldList(event.gui.getClass().getDeclaredFields(),lowerFieldName))
                    {
                        IInventory upper = (IInventory) FieldUtils.readDeclaredField(event.gui, upperFieldName, true);
                        IInventory lower = (IInventory) FieldUtils.readDeclaredField(event.gui, lowerFieldName, true);
                        ((AuctionHouseComponent) getComponent(ComponentId.AUCTION_HOUSE_MENU)).OpenTheGui(upper, lower, event);
                    } else {
                        IInventory upper = (IInventory) FieldUtils.readField(event.gui, upperFieldName, true);
                        IInventory lower = (IInventory) FieldUtils.readField(event.gui, lowerFieldName, true);
                        ((AuctionHouseComponent) getComponent(ComponentId.AUCTION_HOUSE_MENU)).OpenTheGui(upper, lower, event);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
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
