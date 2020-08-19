package Apec.Components.Gui.ContainerGuis;

import Apec.ApecMain;
import Apec.Component;
import Apec.ComponentId;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SkillViewComponent extends ChestGuiComponent {

    public boolean guiIsOpened = false;

    public SkillViewComponent() {
        super(ComponentId.SKILL_VIEW_MENU);
    }

    public void OpenGui(IInventory upper, IInventory lower, GuiOpenEvent event) {
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.MENU_GUI)) {
            if (lower.getDisplayName().getUnformattedText().contains("Skill") && (lower.getDisplayName().getUnformattedText().contains("Farming") ||
                    lower.getDisplayName().getUnformattedText().contains("Mining") ||
                    lower.getDisplayName().getUnformattedText().contains("Combat") ||
                    lower.getDisplayName().getUnformattedText().contains("Foraging") ||
                    lower.getDisplayName().getUnformattedText().contains("Fishing") ||
                    lower.getDisplayName().getUnformattedText().contains("Enchanting") ||
                    lower.getDisplayName().getUnformattedText().contains("Alchemy") ||
                    lower.getDisplayName().getUnformattedText().contains("Carpentry") ||
                    lower.getDisplayName().getUnformattedText().contains("Runecrafting") ||
                    lower.getDisplayName().getUnformattedText().contains("Taming"))) {
                event.setCanceled(true);
                Minecraft.getMinecraft().displayGuiScreen(new SkillViewGui(upper, lower));
                guiIsOpened = true;
            }
        }
    }

}
