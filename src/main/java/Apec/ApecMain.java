package Apec;

import Apec.Commands.ApecComponentTogglerCommand;
import Apec.Components.Gui.ContainerGuis.TrasparentEffects.ActiveEffectsTransparentComponent;
import Apec.Components.Gui.ContainerGuis.AuctionHouse.AuctionHouseComponent;
import Apec.Components.Gui.ContainerGuis.SkillView.SkillViewComponent;
import Apec.Components.Gui.CustomItemToolTip;
import Apec.Components.Gui.GuiIngame.GUIModifier;
import Apec.Components.Gui.Menu.SettingsMenu.ApecMenu;
import Apec.Components.Gui.Menu.TexturePackMenu.TexturePackRegistryViewer;
import Apec.DataInterpretation.ComponentSaveManager;
import Apec.DataInterpretation.ContainerGuiManager;
import Apec.DataInterpretation.DataExtractor;
import Apec.DataInterpretation.InventorySubtractor;
import Apec.Settings.SettingsManager;
import Apec.Utils.VersionChecker;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mod(modid = ApecMain.modId, version = ApecMain.version, name = ApecMain.name)
public class ApecMain
{

    /**
     * Don't read that -> You lost
     */

    public static Logger logger = FMLLog.getLogger();

    public static final String modId = "apec"; 
    public static final String name = "Apec";
    public static final String version = "1.11.3";

    public static ApecMain Instance;

    /** Key for toggling the gui */
    KeyBinding guiKey = new KeyBinding("Apec Gui", Keyboard.KEY_RCONTROL, "Apec");
    /** Key for toggling the menu*/
    KeyBinding menuKey = new KeyBinding("Apec Settings Menu", Keyboard.KEY_M, "Apec");

    /** Data parser */
    public DataExtractor dataExtractor = new DataExtractor();
    /** Keeps track of changes in the inventory */
    public InventorySubtractor inventorySubtractor = new InventorySubtractor();
    /** Keeps track of setting states */
    public SettingsManager settingsManager = new SettingsManager();
    /** Manages custom guis for container like interfaces */
    public ContainerGuiManager containerGuiManager = new ContainerGuiManager();

    /** The newest version number set */
    public String newestVersion = "";

    /** List of all the components present, removing any of these removes the feature from the mod */
    public List<Component> components = new ArrayList<Component>() {{
        add(new GUIModifier()); // The gui ingame interface
        add(new ApecMenu()); // The settings menu
        add(new TexturePackRegistryViewer());
        add(new CustomItemToolTip());
        add(new AuctionHouseComponent()); // The auction house ui
        add(new SkillViewComponent()); // Skill view ui
        add(new ActiveEffectsTransparentComponent()); // The transparent /effects screen
    }};

    /** Manages the saving and loading of component save data */
    public ComponentSaveManager componentSaveManager = new ComponentSaveManager(components);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new ApecComponentTogglerCommand(ComponentId.SETTINGS_MENU,"apec",true));
        ClientCommandHandler.instance.registerCommand(new ApecComponentTogglerCommand(ComponentId.GUI_MODIFIER,"apectoggle",false));
        ClientCommandHandler.instance.registerCommand(new ApecComponentTogglerCommand(ComponentId.TEXTURE_PACK_REGISTRY_VIEWER,"apectpr",true));
    }

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

        newestVersion = VersionChecker.getVersion();

        this.settingsManager.LoadSettings();

        containerGuiManager.init();

        List<HashMap<Integer,String>> DataOfComponents = componentSaveManager.LoadData();

        // Sets the saved data for each component
        for (Component component : components){
            // FIXME:
            if (!DataOfComponents.get(component.componentId.ordinal()).isEmpty()) component.loadSavedData(DataOfComponents.get(component.componentId.ordinal()));
            component.init();
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

    /**
     * @param componentId = The id of component which has to be found
     * @return Returns the component with the same id, null if none was found
     */
    public <U extends Component> U getComponent(ComponentId componentId) {
        for (Component component : components) {
            if (component.componentId == componentId) return (U)component;
        }
        return null;
    }

}
