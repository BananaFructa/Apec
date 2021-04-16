package Apec.Components.Gui.ContainerGuis.SkillView.Gui;

import Apec.Utils.ApecUtils;
import Apec.Components.Gui.ContainerGuis.ApecContainerGui;
import Apec.Components.Gui.ContainerGuis.SkillView.SkillViewComponent;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import org.lwjgl.input.Mouse;

import javax.vecmath.Vector2f;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class SkillViewContainer extends ApecContainerGui {

    int[] skillXpContainerSlot = new int[] {
            0,9,18,27,28,29,20,11,2,3,4,13,22,31,32,33,24,15,6,7,8,17,26,35,44,53
    };

    boolean handleNextClick = false;
    boolean userIsDragging = false;
    Vector2f initialMousePosition = new Vector2f(0,0);
    int xSliderValue = 0;
    int tempSlideValue = 0;
    boolean firstSetxSlider = true;

    public SkillViewContainer(Container inventorySlotsIn)
    {
        super(inventorySlotsIn);
    }

    public void initGui()
    {
        super.initGui();
        firstSetxSlider = true;
        this.mc.thePlayer.openContainer = this.inventorySlots;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        ScaledResolution sr= new ScaledResolution(mc);
        this.buttonList.add(new SkillViewButton(0,sr.getScaledWidth()-30,0,15,15, SkillViewComponent.Actions.BACK));
        this.buttonList.add(new SkillViewButton(0,sr.getScaledWidth()-15,0,15,15, SkillViewComponent.Actions.CLOSE));
        this.buttonList.add(new SkillViewButton(0,0,sr.getScaledHeight()-30,120,30, SkillViewComponent.Actions.PAGE_CHANGE));
        this.buttonList.add(new SkillViewButton(0,sr.getScaledWidth() - 120,sr.getScaledHeight()-30,120,30, SkillViewComponent.Actions.OPEN_INFO));

        disableItemRendering();
        disableSlotClickLogic();
    }

    public void executeAction(SkillViewComponent.Actions action) {
        handleNextClick = true;
        switch (action) {
            case CLOSE:
                handleMouseClick(this.inventorySlots.inventorySlots.get(49),49,0,0);
                break;
            case BACK:
                handleMouseClick(this.inventorySlots.inventorySlots.get(48),48,0,0);
                break;
            case PAGE_CHANGE:
                if (this.inventorySlots.inventorySlots.get(45).inventory.getStackInSlot(45).getItem() == Items.arrow) {
                    handleMouseClick(this.inventorySlots.inventorySlots.get(45),45,0,0);
                } else {
                    handleMouseClick(this.inventorySlots.inventorySlots.get(50),50,0,0);
                }
                break;
            case OPEN_INFO:
                handleMouseClick(this.inventorySlots.inventorySlots.get(40),48,0,0);
                break;
        }
    }

    public List<String> getPageText() {
        try {
            List<String> _s;
            if (this.inventorySlots.inventorySlots.get(45).inventory.getStackInSlot(45).getItem() == Items.arrow) {
                _s = this.inventorySlots.inventorySlots.get(45).inventory.getStackInSlot(45).getTooltip(mc.thePlayer, false);
            } else {
                _s = this.inventorySlots.inventorySlots.get(50).inventory.getStackInSlot(50).getTooltip(mc.thePlayer, false);
            }
            return _s;
        } catch (Exception e) {
            return new ArrayList<String>();
        }
    }

    public List<String> getOtherInfoText() {
        try {
            if (this.inventorySlots.inventorySlots.get(40).inventory.getStackInSlot(40).getItem() != Item.getItemFromBlock(Blocks.glass_pane)) {
                final List<String> allText = this.inventorySlots.inventorySlots.get(40).inventory.getStackInSlot(40).getTooltip(mc.thePlayer, false);
                return new ArrayList<String>() {{
                    add(allText.get(0));
                    add(allText.get(allText.size()-1));
                }};
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
        return new ArrayList<String>();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();
        if (i > 1)
            xSliderValue += 20;
        if (i < -1)
            xSliderValue -= 20;

    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX,mouseY,partialTicks);

        try {
            if (this.inventorySlots.inventorySlots.get(skillXpContainerSlot[0]).inventory.getStackInSlot(skillXpContainerSlot[0]).getItem() != null) {
                int totalWidth = 15;
                int startSliderValue = 0;
                ScaledResolution sr = new ScaledResolution(mc);
                for (int k = 0; k < 25; k++) {
                    List<String> list = this.inventorySlots.inventorySlots.get(skillXpContainerSlot[k + 1]).inventory.getStackInSlot(skillXpContainerSlot[k + 1]).getTooltip(mc.thePlayer, false);
                    int height = list.size() * 10;
                    int width = ApecUtils.getMaxStringWidth(list);
                    drawRect(totalWidth + xSliderValue + tempSlideValue, sr.getScaledHeight() / 2 - height / 2, totalWidth + width + xSliderValue + tempSlideValue, sr.getScaledHeight() / 2 + height / 2, 0xaa353535);
                    if (ApecUtils.doesListContainRegex(list, "Progress") && firstSetxSlider) {
                        startSliderValue = -totalWidth + sr.getScaledWidth()/2 - width/2;
                    }
                    for (int l = 0; l < list.size(); l++) {
                        fontRendererObj.drawString(list.get(l), totalWidth + xSliderValue + tempSlideValue, sr.getScaledHeight() / 2 - height / 2 + 1 + 10 * (l), 0xffffff);
                    }
                    totalWidth += width + 15;
                }

                if (firstSetxSlider) {
                    xSliderValue = startSliderValue;
                    firstSetxSlider = false;
                }

                if (userIsDragging) {
                    tempSlideValue = mouseX - (int) initialMousePosition.x;
                    if (tempSlideValue + xSliderValue > 0) {
                        tempSlideValue = 0;
                        xSliderValue = 0;
                    }
                    if (tempSlideValue + xSliderValue < -totalWidth + sr.getScaledWidth() - 15) {
                        tempSlideValue = 0;
                        xSliderValue = -totalWidth + sr.getScaledWidth() - 15;
                    }
                } else {
                    if (tempSlideValue + xSliderValue > 0) {
                        tempSlideValue = 0;
                        xSliderValue = 0;
                    }
                    if (tempSlideValue + xSliderValue < -totalWidth + sr.getScaledWidth() - 15) {
                        tempSlideValue = 0;
                        xSliderValue = -totalWidth + sr.getScaledWidth() - 15;
                    }
                    xSliderValue += tempSlideValue;
                    tempSlideValue = 0;
                }

                List<String> linesForIntroduction = this.inventorySlots.inventorySlots.get(skillXpContainerSlot[0]).inventory.getStackInSlot(skillXpContainerSlot[0]).getTooltip(mc.thePlayer, false);
                for (int k = 0; k < linesForIntroduction.size(); k++) {
                    fontRendererObj.drawString(linesForIntroduction.get(k), 1, 1 + 10 * k, 0xffffff);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) {
            for (GuiButton guiButton : this.buttonList) {
                if (guiButton.mousePressed(mc,mouseX,mouseY)) {
                    executeAction(((SkillViewButton)guiButton).action);
                    break;
                }
            }
        }

        userIsDragging = true;
        initialMousePosition = new Vector2f(mouseX,mouseY);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state); //Forge, Call parent to release buttons
        userIsDragging = false;
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
