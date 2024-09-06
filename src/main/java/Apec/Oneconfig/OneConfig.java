package Apec.Oneconfig;

import Apec.ApecMain;
import Apec.ComponentId;
import Apec.Components.Gui.Menu.CustomizationMenu.CustomizationGui;
import Apec.Settings.SettingID;
import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Button;
import cc.polyfrost.oneconfig.config.annotations.KeyBind;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.libs.universal.UKeyboard;
import cc.polyfrost.oneconfig.utils.gui.GuiUtils;

public class OneConfig extends Config {

    @Switch(name = "Auto Toggle", description = "The GUI will automatically enable/disable when you join/leave skyblock", category = "General", subcategory = "General Settings")
    public static boolean AUTO_ENABLE = true;

    @Switch(name = "Show HP Bar", description = "Show the HP bar on the screen", category = "GUI", subcategory = "Health")
    public static boolean HP_BAR = true;

    @Switch(name = "Show HP Text", description = "Shows the HP text", category = "GUI", subcategory = "Health")
    public static boolean HP_TEXT = true;

    @Switch(name = "Show MP Bar", description = "Show the MP bar on the screen", category = "GUI", subcategory = "Mana")
    public static boolean MP_BAR = true;

    @Switch(name = "Show MP Text", description = "Shows the MP text", category = "GUI", subcategory = "Mana")
    public static boolean MP_TEXT = true;

    @Switch(name = "Show XP Bar", description = "Show the XP bar on the screen", category = "GUI", subcategory = "Experience")
    public static boolean XP_BAR = true;

    @Switch(name = "Show XP Text", description = "Shows the XP text", category = "GUI", subcategory = "Experience")
    public static boolean XP_TEXT = true;

    @Switch(name = "Show air bar", description = "Toggles on or off the air bar", category = "GUI", subcategory = "Air")
    public static boolean SHOW_AIR_BAR = true;

    @Switch(name = "Show Air text", description = "Shows the air text", category = "GUI", subcategory = "Air")
    public static boolean AIR_TEXT = true;

    @Switch(name = "Show skill xp", description = "When you get skill xp it will appear on the screen", category = "GUI", subcategory = "Skills")
    public static boolean SHOW_SKILL_XP = true;

    @Switch(name = "Show Skill text", description = "Shows the skill text", category = "GUI", subcategory = "Skills")
    public static boolean SKILL_TEXT = true;

    @Switch(name = "Colored skill xp", description = "Shows a different colored bar for each skill xp", category = "GUI", subcategory = "Skills")
    public static boolean COLORED_SKILL_XP = true;

    @Switch(name = "Always show skill xp", description = "Constantly shows skill xp, using cached values when not normally shown", category = "GUI", subcategory = "Skills")
    public static boolean ALWAYS_SHOW_SKILL = false;

    @Switch(name = "Show Warning Icons", description = "Show the warning icons under the status bars", category = "GUI", subcategory = "Warnings")
    public static boolean SHOW_WARNING = true;

    @Switch(name = "Show potion effects", description = "Show potion effects on the left side of the screen", category = "GUI", subcategory = "Potions")
    public static boolean SHOW_POTIONS_EFFECTS = true;

    @Switch(name = "Compact potion display", description = "Shows 2 potions per line", category = "GUI", subcategory = "Potions")
    public static boolean COMPACT_POTION = false;

    @Switch(name = "Show Debug Messages", description = "Shows messages in the chat about different events in the code", category = "Debug", subcategory = "Debugging")
    public static boolean SHOW_DEBUG_MESSAGES = false;

    @Switch(name = "Npc GUIs", description = "Custom GUIs for Npcs", category = "GUI", subcategory = "NPCs")
    public static boolean NPC_GUI = true;

    @Switch(name = "Menu GUIs", description = "Custom GUIs for menus", category = "GUI", subcategory = "Menus")
    public static boolean MENU_GUI = true;

    @Switch(name = "Show Inventory Traffic", description = "Show what items are going in and out of the inventory", category = "Inventory", subcategory = "Inventory Management")
    public static boolean INVENTORY_TRAFFIC = true;

    @Switch(name = "Hide in F3", description = "Hides certain elements while in f3", category = "GUI", subcategory = "General Settings")
    public static boolean HIDE_IN_F3 = false;

    @Switch(name = "Black border text", description = "When disabled the text will show without the black border", category = "GUI", subcategory = "Text")
    public static boolean BORDER_TYPE = true;

    @Switch(name = "Normal tooltip text", description = "Shows the tooltip text of an item centered with the hotbar", category = "GUI", subcategory = "Tooltips")
    public static boolean ITEM_HIGHLIGHT_TEXT = false;

    @Switch(name = "Show current server", description = "Show in which server you are in", category = "General", subcategory = "General Settings")
    public static boolean SHOW_CURRENT_SERVER = false;

    @Switch(name = "Snap in gui editing", description = "Activates snapping while in the gui customization menu", category = "GUI", subcategory = "Customization")
    public static boolean SNAP_IN_EDITING = true;

    @Switch(name = "Bottom Bar Animation", description = "A slide down animation for the bottom box when in chat", category = "GUI", subcategory = "Animations")
    public static boolean INFO_BOX_ANIMATION = true;

    @Switch(name = "Show ability text", description = "Shows the ability text at the mana bar", category = "GUI", subcategory = "Abilities")
    public static boolean SHOW_ABILITY_TEXT = true;

    @Switch(name = "Show absorption bar", description = "Shows the absorption bar", category = "GUI", subcategory = "Health")
    public static boolean SHOW_ABSORPTION_BAR = true;

    @Switch(name = "Show overflow mana bar", description = "Shows the overflow mana bar", category = "GUI", subcategory = "Mana")
    public static boolean SHOW_OP_BAR = true;

    @Switch(name = "Show tab effect", description = "Show the one rolling effect that appears in tab", category = "GUI", subcategory = "Effects")
    public static boolean SHOW_EFFECTS_AS_IN_TAB = true;

    @Switch(name = "Info bar icons", description = "Shows icons for each stat instead of text", category = "GUI", subcategory = "Info Bar")
    public static boolean INFO_BOX_ICONS = true;

    @Switch(name = "Info bar autoscaling", description = "The bottom bar auto-scales based on the gui scale", category = "GUI", subcategory = "Info Bar")
    public static boolean USE_AUTO_SCALING_BB = true;

    @Switch(name = "Allow GUIs when inactive", description = "Allow GUIs when the Ingame GUI is not enabled", category = "GUI", subcategory = "General Settings")
    public static boolean GUIS_WHEN_DISABLED = false;

    @Switch(name = "Defence outside bar", description = "Shows the defence outside the bottom bar", category = "GUI", subcategory = "Defence")
    public static boolean USE_DEFENCE_OUT_OF_BB = false;

    @Switch(name = "Compatibility Safety", description = "Ensures that certain features that might break some mods are disabled", category = "General", subcategory = "General Settings")
    public static boolean COMPATIBILITY_SAFETY = true;

    @Switch(name = "Info bar on top", description = "Puts the info bar on top instead of on the bottom", category = "GUI", subcategory = "Info Bar")
    public static boolean BB_ON_TOP = false;

    @Switch(name = "Custom tool tip", description = "Shows extra icons above the item tool tip", category = "GUI", subcategory = "Tooltips")
    public static boolean CUSTOM_TOOL_TIP = true;

    @Switch(name = "Show rift timer", description = "Shows the rift timer", category = "General", subcategory = "General Settings")
    public static boolean SHOW_RIFT_TIMER = true;

    @Switch(name = "Game Mode outside bar", description = "Shows the gamemode outside the bottom bar", category = "GUI", subcategory = "Game Mode")
    public static boolean USE_GAME_MODE_OUT_OF_BB = false;

    @Switch(name = "Kuudra set bonus outside bar", description = "Shows the kuudra set bonus outside the bottom bar", category = "GUI", subcategory = "Kuudra Set Bonus")
    public static boolean USE_KUUDRA_SET_BONUS_OUT_OF_BB = false;

    @Button(name = "Open Customization GUI", text = "Open", description = "Opens the GUI customization menu", category = "General", subcategory = "Gui Customization")
    Runnable runnable = () -> {
        GuiUtils.displayScreen(new CustomizationGui());
    };

    @KeyBind(name = "Toggle GUI", description = "Toggles the GUI", category = "General", subcategory = "General Settings")
    public static OneKeyBind TOGGLE_GUI = new OneKeyBind(UKeyboard.KEY_RCONTROL);

    public OneConfig() {
        super(new Mod("Apec", ModType.SKYBLOCK), "apec.json");
        initialize();
        registerKeyBind(TOGGLE_GUI, () -> ApecMain.Instance.getComponent(ComponentId.GUI_MODIFIER).Toggle());

    }

    public boolean getSettingState(SettingID settingID) {
        try {
            return (boolean) this.getClass().getDeclaredField(settingID.name()).get(this);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public void setSettingState(SettingID settingID, boolean state) {
        try {
            this.getClass().getDeclaredField(settingID.name()).set(this, state);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

}
