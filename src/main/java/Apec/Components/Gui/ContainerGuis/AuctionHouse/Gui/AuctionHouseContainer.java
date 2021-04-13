package Apec.Components.Gui.ContainerGuis.AuctionHouse.Gui;

import Apec.ApecMain;
import Apec.ComponentId;
import Apec.Components.Gui.ContainerGuis.ApecContainerGui;
import Apec.Components.Gui.ContainerGuis.AuctionHouse.AuctionHouseComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Modified copy of the original GuiContainer.class
 */

public abstract class AuctionHouseContainer extends ApecContainerGui {

    private List<String> SortList = new ArrayList<String>();
    private List<String> ModeList = new ArrayList<String>();
    private List<String> RarityList = new ArrayList<String>();
    private String Page = "";

    private boolean acceptNextClick = false;
    private boolean setInitialCategory = false;

    public AuctionHouseContainer(Container inventorySlotsIn) {
        super(inventorySlotsIn);
        this.rePositionSlots();
    }

    private void rePositionSlots() {
        for (Slot s : this.inventorySlots.inventorySlots) {
            s.xDisplayPosition -= 10;
            s.yDisplayPosition += 23;

        }
        for (int i = 11;i <=43;i+=9) {
            expandSlotPosition(i);
        }
    }

    private void expandSlotPosition(int _i) {
        for (int i = _i;i <= _i + 5;i++) {
            this.inventorySlots.inventorySlots.get(i).xDisplayPosition += (i-_i-2.5f)*4f;
        }
    }

    private void executeCategorySelect(AuctionHouseComponent.CategoryID categoryID) {
        acceptNextClick = true;
        handleMouseClick(this.inventorySlots.inventorySlots.get(categoryID.ordinal() * 9),categoryID.ordinal()*9,0,0);
    }

    private void executeAction (AuctionHouseComponent.Actions actions,int mouseButton) {
        acceptNextClick = true;
        if (mouseButton == 0 || mouseButton == 1) {
            switch (actions) {
                case SEARCH:
                    if (mouseButton == 0) handleMouseClick(this.inventorySlots.inventorySlots.get(48), 48, 0, 0);
                    else handleMouseClick(this.inventorySlots.inventorySlots.get(47), 47, 0, 0);
                    break;
                case NEXT:
                    handleMouseClick(this.inventorySlots.inventorySlots.get(53), 53, mouseButton, 0);
                    break;
                case BACK:
                    handleMouseClick(this.inventorySlots.inventorySlots.get(46), 46, mouseButton, 0);
                    break;
                case SORT_CHANGE:
                    handleMouseClick(this.inventorySlots.inventorySlots.get(50), 50, mouseButton, 0);
                    break;
                case MOD_CHANGE:
                    handleMouseClick(this.inventorySlots.inventorySlots.get(52), 52, mouseButton, 0);
                    break;
                case RARITY_CHANGE:
                    handleMouseClick(this.inventorySlots.inventorySlots.get(51), 51, mouseButton, 0);
                    break;
                case CLOSE:
                    handleMouseClick(this.inventorySlots.inventorySlots.get(49), 49, 0, 0);
                    break;
            }
        }
    }

    protected List<String> getSortText() {
        try {
            if (this.inventorySlots.inventorySlots.get(50).inventory.getStackInSlot(50) != null) {
                SortList = this.inventorySlots.inventorySlots.get(50).inventory.getStackInSlot(50).getTooltip(mc.thePlayer, false).subList(0, 6);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SortList;
    }

    protected List<String> getModeText() {
        try {
            if (this.inventorySlots.inventorySlots.get(52).inventory.getStackInSlot(52) != null) {
                ModeList = this.inventorySlots.inventorySlots.get(52).inventory.getStackInSlot(52).getTooltip(mc.thePlayer, false).subList(0,5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ModeList;
    }

    protected String getPageText() {
        try {
            if (this.inventorySlots.inventorySlots.get(53).inventory.getStackInSlot(53) != null) {
                List<String> s = this.inventorySlots.inventorySlots.get(53).inventory.getStackInSlot(53).getTooltip(mc.thePlayer, false);
                if (s.size() > 1)
                Page = this.inventorySlots.inventorySlots.get(53).inventory.getStackInSlot(53).getTooltip(mc.thePlayer, false).get(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Page;
    }

    protected List<String> getRarityText() {
        try {
            if (this.inventorySlots.inventorySlots.get(51).inventory.getStackInSlot(51) != null) {
                RarityList = this.inventorySlots.inventorySlots.get(51).inventory.getStackInSlot(51).getTooltip(mc.thePlayer, false).subList(0,11);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RarityList;
    }

    public void initGui()
    {
        super.initGui();

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int w = sr.getScaledWidth()/2;
        int h= sr.getScaledHeight()/2;
        this.buttonList.add(new AuctionHouseToggleButton(0,w-200,h-80+54,100,16,"Weapons", AuctionHouseComponent.CategoryID.WEAPONS));
        this.buttonList.add(new AuctionHouseToggleButton(0,w-200,h-64+54,100,16,"Armour", AuctionHouseComponent.CategoryID.ARMOUR));
        this.buttonList.add(new AuctionHouseToggleButton(0,w-200,h-48+54,100,16,"Accessories", AuctionHouseComponent.CategoryID.ACCESSORIES));
        this.buttonList.add(new AuctionHouseToggleButton(0,w-200,h-32+54,100,16,"Consumables", AuctionHouseComponent.CategoryID.CONSUMABLES));
        this.buttonList.add(new AuctionHouseToggleButton(0,w-200,h-16+54,100,16,"Blocks", AuctionHouseComponent.CategoryID.BLOCKS));
        this.buttonList.add(new AuctionHouseToggleButton(0,w-200,h+54,100,16,"Tools & Misc", AuctionHouseComponent.CategoryID.OTHER));
        this.buttonList.add(new AuctionHouseButton(0,w-99,h+54,15,15, AuctionHouseComponent.Actions.SEARCH));
        this.buttonList.add(new AuctionHouseButton(0,w+100-15,h+54,15,15, AuctionHouseComponent.Actions.NEXT));
        this.buttonList.add(new AuctionHouseButton(0,w+100-30,h+54,15,15, AuctionHouseComponent.Actions.BACK));
        this.buttonList.add(new AuctionHouseButton(0,w+110,h-100,80,70, AuctionHouseComponent.Actions.SORT_CHANGE));
        this.buttonList.add(new AuctionHouseButton(0,w+110,h-30,80,70, AuctionHouseComponent.Actions.MOD_CHANGE));
        this.buttonList.add(new AuctionHouseButton(0,w+200,h-100,80,120, AuctionHouseComponent.Actions.RARITY_CHANGE));
        this.buttonList.add(new AuctionHouseButton(0,w+85,h-100,15,15, AuctionHouseComponent.Actions.CLOSE));

        disableItemRendering();

    }

    /** Makes each button check if their current id is the one that should be enabled */
    private void FetchButtonStatesFromComponent() {
        for (GuiButton button : this.buttonList) {
            if (button instanceof AuctionHouseToggleButton) ((AuctionHouseToggleButton)button).LoadState();
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        int i = this.guiLeft;
        int j = this.guiTop;
        this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)i, (float)j, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        this.theSlot = null;
        int k = 240;
        int l = 240;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)k / 1.0F, (float)l / 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (!setInitialCategory && inventorySlots.inventorySlots.get(1).getStack() != null) {
            // Sets the initial category
            String name = inventorySlots.inventorySlots.get(1).getStack().getUnlocalizedName();
            AuctionHouseComponent component = ((AuctionHouseComponent) ApecMain.Instance.getComponent(ComponentId.AUCTION_HOUSE_MENU));
            if (name.endsWith("orange")) {
                component.SetCategoryID(AuctionHouseComponent.CategoryID.WEAPONS);
            } else if (name.endsWith("blue")) {
                component.SetCategoryID(AuctionHouseComponent.CategoryID.ARMOUR);
            } else if (name.endsWith("green")) {
                component.SetCategoryID(AuctionHouseComponent.CategoryID.ACCESSORIES);
            } else if (name.endsWith("red")) {
                component.SetCategoryID(AuctionHouseComponent.CategoryID.CONSUMABLES);
            } else if (name.endsWith("brown")) {
                component.SetCategoryID(AuctionHouseComponent.CategoryID.BLOCKS);
            } else if (name.endsWith("purple")) {
                component.SetCategoryID(AuctionHouseComponent.CategoryID.OTHER);
            }
            setInitialCategory = true;
            FetchButtonStatesFromComponent();
        }

        for (int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); ++i1) {
            // Only renders the slots of interest
            if ((i1 >= 11 && i1 <= 16) || (i1 >= 20 && i1 <= 25) || (i1 >= 29 && i1 <= 34) || (i1 >= 38 && i1 <= 43)) {
                Slot slot = (Slot) this.inventorySlots.inventorySlots.get(i1);
                this.drawSlot(slot);

                if (this.isMouseOverSlot(slot, mouseX, mouseY) && slot.canBeHovered()) {
                    this.theSlot = slot;
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    int j1 = slot.xDisplayPosition;
                    int k1 = slot.yDisplayPosition;
                    GlStateManager.colorMask(true, true, true, false);
                    this.drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
                    GlStateManager.colorMask(true, true, true, true);
                    GlStateManager.enableLighting();
                    GlStateManager.enableDepth();
                }
            }
        }

        RenderHelper.disableStandardItemLighting();
        this.drawGuiContainerForegroundLayer(mouseX, mouseY);
        RenderHelper.enableGUIStandardItemLighting();
        InventoryPlayer inventoryplayer = this.mc.thePlayer.inventory;
        ItemStack itemstack = this.draggedStack == null ? inventoryplayer.getItemStack() : this.draggedStack;

        if (itemstack != null)
        {
            int j2 = 8;
            int k2 = this.draggedStack == null ? 8 : 16;
            String s = null;

            if (this.draggedStack != null && this.isRightMouseClick)
            {
                itemstack = itemstack.copy();
                itemstack.stackSize = MathHelper.ceiling_float_int((float)itemstack.stackSize / 2.0F);
            }
            else if (this.dragSplitting && this.dragSplittingSlots.size() > 1)
            {
                itemstack = itemstack.copy();
                itemstack.stackSize = this.dragSplittingRemnant;

                if (itemstack.stackSize == 0)
                {
                    s = "" + EnumChatFormatting.YELLOW + "0";
                }
            }
        }

        if (this.returningStack != null)
        {
            float f = (float)(Minecraft.getSystemTime() - this.returningStackTime) / 100.0F;

            if (f >= 1.0F)
            {
                f = 1.0F;
                this.returningStack = null;
            }

            int l2 = this.returningStackDestSlot.xDisplayPosition - this.touchUpX;
            int i3 = this.returningStackDestSlot.yDisplayPosition - this.touchUpY;
            int l1 = this.touchUpX + (int)((float)l2 * f);
            int i2 = this.touchUpY + (int)((float)i3 * f);
            this.drawItemStack(this.returningStack, l1, i2, (String)null);
        }

        GlStateManager.popMatrix();

        if (inventoryplayer.getItemStack() == null && this.theSlot != null && this.theSlot.getHasStack())
        {
            ItemStack itemstack1 = this.theSlot.getStack();
            this.renderToolTip(itemstack1, mouseX, mouseY);
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
    }


    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (GuiButton guiButton : this.buttonList) {
            if (guiButton.mousePressed(mc, mouseX, mouseY)) {
                if (guiButton instanceof AuctionHouseToggleButton) {
                    AuctionHouseToggleButton button = (AuctionHouseToggleButton) guiButton;
                    if (!button.state) {
                        button.Toggle();
                        for (GuiButton _guiButton : this.buttonList) {
                            if (_guiButton != guiButton && _guiButton instanceof AuctionHouseToggleButton) {
                                AuctionHouseToggleButton _button = (AuctionHouseToggleButton) _guiButton;
                                if (_button.state) _button.Toggle();
                            }
                        }
                        executeCategorySelect(button.categoryID);
                        ((AuctionHouseComponent) ApecMain.Instance.getComponent(ComponentId.AUCTION_HOUSE_MENU)).SetCategoryID(button.categoryID);
                    }
                } else if (guiButton instanceof AuctionHouseButton) {
                    executeAction(((AuctionHouseButton) guiButton).action, mouseButton);
                }
                break;
            }
        }
    }

    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType)
    {
        if ((slotId >= 11 && slotId <= 16) || (slotId >= 20 && slotId <= 25) || (slotId >= 29 && slotId <= 34) || (slotId >= 38 && slotId <= 43) || acceptNextClick) {
            acceptNextClick = false;
            super.handleMouseClick(slotIn,slotId,clickedButton,clickType);
        }
    }

    public void onGuiClosed()
    {
        if (this.mc.thePlayer != null)
        {
            ((AuctionHouseComponent)ApecMain.Instance.getComponent(ComponentId.AUCTION_HOUSE_MENU)).guiIsOpened = false;
            this.inventorySlots.onContainerClosed(this.mc.thePlayer);
        }
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }

}
