package Apec.Components.Gui.ContainerGuis.AuctionHouse;

import Apec.ApecMain;
import Apec.ComponentId;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Modified copy of the original GuiContainer.class
 */

public abstract class AuctionHouseContainer extends GuiScreen {

    /** The location of the inventory background texture */
    protected static final ResourceLocation inventoryBackground = new ResourceLocation("textures/gui/container/inventory.png");
    /** The X size of the inventory window in pixels. */
    protected int xSize = 176;
    /** The Y size of the inventory window in pixels. */
    protected int ySize = 166;
    /** A list of the players inventory slots */
    public Container inventorySlots;
    /** Starting X position for the Gui. Inconsistent use for Gui backgrounds. */
    protected int guiLeft;
    /** Starting Y position for the Gui. Inconsistent use for Gui backgrounds. */
    protected int guiTop;
    /** holds the slot currently hovered */
    private Slot theSlot;
    /** Used when touchscreen is enabled. */
    private Slot clickedSlot;
    /** Used when touchscreen is enabled. */
    private boolean isRightMouseClick;
    /** Used when touchscreen is enabled */
    private ItemStack draggedStack;
    private int touchUpX;
    private int touchUpY;
    private Slot returningStackDestSlot;
    private long returningStackTime;
    /** Used when touchscreen is enabled */
    private ItemStack returningStack;
    private Slot currentDragTargetSlot;
    private long dragItemDropDelay;
    protected final Set<Slot> dragSplittingSlots = Sets.<Slot>newHashSet();
    protected boolean dragSplitting;
    private int dragSplittingLimit;
    private int dragSplittingButton;
    private boolean ignoreMouseUp;
    private int dragSplittingRemnant;
    private long lastClickTime;
    private Slot lastClickSlot;
    private int lastClickButton;
    private boolean doubleClick;
    private ItemStack shiftClickedSlot;

    //--
    private List<String> SortList = new ArrayList<String>();
    private List<String> ModeList = new ArrayList<String>();
    private List<String> RarityList = new ArrayList<String>();
    private String Page = "";

    private boolean acceptNextClick = false;
    private boolean setInitialCategory = false;
    //--

    public AuctionHouseContainer(Container inventorySlotsIn)
    {
        this.inventorySlots = inventorySlotsIn;
        this.ignoreMouseUp = true;
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
        if (mouseButton == 0) {
            switch (actions) {
                case SEARCH:
                    handleMouseClick(this.inventorySlots.inventorySlots.get(48), 48, 0, 0);
                    break;
                case NEXT:
                    handleMouseClick(this.inventorySlots.inventorySlots.get(53), 53, 0, 0);
                    break;
                case BACK:
                    handleMouseClick(this.inventorySlots.inventorySlots.get(46), 46, 0, 0);
                    break;
                case SORT_CHANGE:
                    handleMouseClick(this.inventorySlots.inventorySlots.get(50), 50, 0, 0);
                    break;
                case MOD_CHANGE:
                    handleMouseClick(this.inventorySlots.inventorySlots.get(52), 52, 0, 0);
                    break;
                case RARITY_CHANGE:
                    handleMouseClick(this.inventorySlots.inventorySlots.get(51), 51, 0, 0);
                    break;
                case CLOSE:
                    handleMouseClick(this.inventorySlots.inventorySlots.get(49), 49, 0, 0);
                    break;
            }
        } else if (mouseButton == 1) {
            switch (actions) {
                case SEARCH:
                    handleMouseClick(this.inventorySlots.inventorySlots.get(47), 47, 0, 0);
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

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        super.initGui();

        this.mc.thePlayer.openContainer = this.inventorySlots;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
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

    }

    /** Makes each button check if their current id is the one that should be enabled */
    private void FetchButtonStatesFromComponent() {
        for (GuiButton button : this.buttonList) {
            if (button instanceof AuctionHouseToggleButton) ((AuctionHouseToggleButton)button).LoadState();
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
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

    /**
     * Render an ItemStack. Args : stack, x, y, format
     */
    private void drawItemStack(ItemStack stack, int x, int y, String altText)
    {
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        this.itemRender.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = null;
        if (stack != null) font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = fontRendererObj;
        this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y - (this.draggedStack == null ? 0 : 8), altText);
        this.zLevel = 0.0F;
        this.itemRender.zLevel = 0.0F;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    protected abstract void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY);

    private void drawSlot(Slot slotIn)
    {
        int i = slotIn.xDisplayPosition;
        int j = slotIn.yDisplayPosition;
        ItemStack itemstack = slotIn.getStack();
        boolean flag = false;
        boolean flag1 = slotIn == this.clickedSlot && this.draggedStack != null && !this.isRightMouseClick;
        ItemStack itemstack1 = this.mc.thePlayer.inventory.getItemStack();
        String s = null;

        if (slotIn == this.clickedSlot && this.draggedStack != null && this.isRightMouseClick && itemstack != null)
        {
            itemstack = itemstack.copy();
            itemstack.stackSize /= 2;
        }
        else if (this.dragSplitting && this.dragSplittingSlots.contains(slotIn) && itemstack1 != null)
        {
            if (this.dragSplittingSlots.size() == 1)
            {
                return;
            }

            if (Container.canAddItemToSlot(slotIn, itemstack1, true) && this.inventorySlots.canDragIntoSlot(slotIn))
            {
                itemstack = itemstack1.copy();
                flag = true;
                Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack, slotIn.getStack() == null ? 0 : slotIn.getStack().stackSize);

                if (itemstack.stackSize > itemstack.getMaxStackSize())
                {
                    s = EnumChatFormatting.YELLOW + "" + itemstack.getMaxStackSize();
                    itemstack.stackSize = itemstack.getMaxStackSize();
                }

                if (itemstack.stackSize > slotIn.getItemStackLimit(itemstack))
                {
                    s = EnumChatFormatting.YELLOW + "" + slotIn.getItemStackLimit(itemstack);
                    itemstack.stackSize = slotIn.getItemStackLimit(itemstack);
                }
            }
            else
            {
                this.dragSplittingSlots.remove(slotIn);
                this.updateDragSplitting();
            }
        }

        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;

        if (itemstack == null)
        {
            TextureAtlasSprite textureatlassprite = slotIn.getBackgroundSprite();

            if (textureatlassprite != null)
            {
                GlStateManager.disableLighting();
                this.mc.getTextureManager().bindTexture(slotIn.getBackgroundLocation());
                this.drawTexturedModalRect(i, j, textureatlassprite, 16, 16);
                GlStateManager.enableLighting();
                flag1 = true;
            }
        }

        if (!flag1)
        {
            if (flag)
            {
                drawRect(i, j, i + 16, j + 16, -2130706433);
            }

            GlStateManager.enableDepth();
            this.itemRender.renderItemAndEffectIntoGUI(itemstack, i, j);
            this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, itemstack, i, j, s);
        }

        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    private void updateDragSplitting()
    {
       ItemStack itemstack = this.mc.thePlayer.inventory.getItemStack();

        if (itemstack != null && this.dragSplitting)
        {
            this.dragSplittingRemnant = itemstack.stackSize;

            for (Slot slot : this.dragSplittingSlots)
            {
                ItemStack itemstack1 = itemstack.copy();
                int i = slot.getStack() == null ? 0 : slot.getStack().stackSize;
                Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack1, i);

                if (itemstack1.stackSize > itemstack1.getMaxStackSize())
                {
                    itemstack1.stackSize = itemstack1.getMaxStackSize();
                }

                if (itemstack1.stackSize > slot.getItemStackLimit(itemstack1))
                {
                    itemstack1.stackSize = slot.getItemStackLimit(itemstack1);
                }

                this.dragSplittingRemnant -= itemstack1.stackSize - i;
            }
        }
    }

    /**
     * Returns the slot at the given coordinates or null if there is none.
     */
    private Slot getSlotAtPosition(int x, int y)
    {
        for (int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i)
        {
            Slot slot = (Slot)this.inventorySlots.inventorySlots.get(i);

            if (this.isMouseOverSlot(slot, x, y))
            {
                return slot;
            }
        }

        return null;
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        boolean flag = mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100;
        Slot slot = this.getSlotAtPosition(mouseX, mouseY);
        long i = Minecraft.getSystemTime();
        this.doubleClick = this.lastClickSlot == slot && i - this.lastClickTime < 250L && this.lastClickButton == mouseButton;
        this.ignoreMouseUp = false;

            for (GuiButton guiButton : this.buttonList) {
                if (guiButton.mousePressed(mc,mouseX,mouseY)) {
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
                        executeAction(((AuctionHouseButton)guiButton).action,mouseButton);
                    }
                    break;
                }
            }

        if (mouseButton == 0 || mouseButton == 1 || flag)
        {
            int j = this.guiLeft;
            int k = this.guiTop;
            boolean flag1 = mouseX < j || mouseY < k || mouseX >= j + this.xSize || mouseY >= k + this.ySize;
            if (slot != null) flag1 = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
            int l = -1;

            if (slot != null)
            {
                l = slot.slotNumber;
            }

            if (flag1)
            {
                l = -999;
            }

            if (this.mc.gameSettings.touchscreen && flag1 && this.mc.thePlayer.inventory.getItemStack() == null)
            {
                this.mc.displayGuiScreen((GuiScreen)null);
                return;
            }

            if (l != -1)
            {
                if (this.mc.gameSettings.touchscreen)
                {
                    if (slot != null && slot.getHasStack())
                    {
                        this.clickedSlot = slot;
                        this.draggedStack = null;
                        this.isRightMouseClick = mouseButton == 1;
                    }
                    else
                    {
                        this.clickedSlot = null;
                    }
                }
                else if (!this.dragSplitting)
                {
                    if (this.mc.thePlayer.inventory.getItemStack() == null)
                    {
                        if (mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100)
                        {
                            this.handleMouseClick(slot, l, mouseButton, 3);
                        }
                        else
                        {
                            boolean flag2 = l != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                            int i1 = 0;

                            if (flag2)
                            {
                                this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack() : null;
                                i1 = 1;
                            }
                            else if (l == -999)
                            {
                                i1 = 4;
                            }

                            this.handleMouseClick(slot, l, mouseButton, i1);
                        }

                        this.ignoreMouseUp = true;
                    }
                    else
                    {
                        this.dragSplitting = true;
                        this.dragSplittingButton = mouseButton;
                        this.dragSplittingSlots.clear();

                        if (mouseButton == 0)
                        {
                            this.dragSplittingLimit = 0;
                        }
                        else if (mouseButton == 1)
                        {
                            this.dragSplittingLimit = 1;
                        }
                        else if (mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100)
                        {
                            this.dragSplittingLimit = 2;
                        }
                    }
                }
            }
        }

        this.lastClickSlot = slot;
        this.lastClickTime = i;
        this.lastClickButton = mouseButton;
    }

    /**
     * Called when a mouse button is pressed and the mouse is moved around. Parameters are : mouseX, mouseY,
     * lastButtonClicked & timeSinceMouseClick.
     */
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
        Slot slot = this.getSlotAtPosition(mouseX, mouseY);
        ItemStack itemstack = this.mc.thePlayer.inventory.getItemStack();

        if (this.clickedSlot != null && this.mc.gameSettings.touchscreen)
        {
            if (clickedMouseButton == 0 || clickedMouseButton == 1)
            {
                if (this.draggedStack == null)
                {
                    if (slot != this.clickedSlot && this.clickedSlot.getStack() != null)
                    {
                        this.draggedStack = this.clickedSlot.getStack().copy();
                    }
                }
                else if (this.draggedStack.stackSize > 1 && slot != null && Container.canAddItemToSlot(slot, this.draggedStack, false))
                {
                    long i = Minecraft.getSystemTime();

                    if (this.currentDragTargetSlot == slot)
                    {
                        if (i - this.dragItemDropDelay > 500L)
                        {
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                            this.handleMouseClick(slot, slot.slotNumber, 1, 0);
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                            this.dragItemDropDelay = i + 750L;
                            --this.draggedStack.stackSize;
                        }
                    }
                    else
                    {
                        this.currentDragTargetSlot = slot;
                        this.dragItemDropDelay = i;
                    }
                }
            }
        }
        else if (this.dragSplitting && slot != null && itemstack != null && itemstack.stackSize > this.dragSplittingSlots.size() && Container.canAddItemToSlot(slot, itemstack, true) && slot.isItemValid(itemstack) && this.inventorySlots.canDragIntoSlot(slot))
        {
            this.dragSplittingSlots.add(slot);
            this.updateDragSplitting();
        }
    }

    /**
     * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
     */
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state); //Forge, Call parent to release buttons
        Slot slot = this.getSlotAtPosition(mouseX, mouseY);
        int i = this.guiLeft;
        int j = this.guiTop;
        boolean flag = mouseX < i || mouseY < j || mouseX >= i + this.xSize || mouseY >= j + this.ySize;
        if (slot != null) flag = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
        int k = -1;

        if (slot != null)
        {
            k = slot.slotNumber;
        }

        if (flag)
        {
            k = -999;
        }

        if (this.doubleClick && slot != null && state == 0 && this.inventorySlots.canMergeSlot((ItemStack)null, slot))
        {
            if (isShiftKeyDown())
            {
                if (slot != null && slot.inventory != null && this.shiftClickedSlot != null)
                {
                    for (Slot slot2 : this.inventorySlots.inventorySlots)
                    {
                        if (slot2 != null && slot2.canTakeStack(this.mc.thePlayer) && slot2.getHasStack() && slot2.inventory == slot.inventory && Container.canAddItemToSlot(slot2, this.shiftClickedSlot, true))
                        {
                            this.handleMouseClick(slot2, slot2.slotNumber, state, 1);
                        }
                    }
                }
            }
            else
            {
                this.handleMouseClick(slot, k, state, 6);
            }

            this.doubleClick = false;
            this.lastClickTime = 0L;
        }
        else
        {
            if (this.dragSplitting && this.dragSplittingButton != state)
            {
                this.dragSplitting = false;
                this.dragSplittingSlots.clear();
                this.ignoreMouseUp = true;
                return;
            }

            if (this.ignoreMouseUp)
            {
                this.ignoreMouseUp = false;
                return;
            }

            if (this.clickedSlot != null && this.mc.gameSettings.touchscreen)
            {
                if (state == 0 || state == 1)
                {
                    if (this.draggedStack == null && slot != this.clickedSlot)
                    {
                        this.draggedStack = this.clickedSlot.getStack();
                    }

                    boolean flag2 = Container.canAddItemToSlot(slot, this.draggedStack, false);

                    if (k != -1 && this.draggedStack != null && flag2)
                    {
                        this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, 0);
                        this.handleMouseClick(slot, k, 0, 0);

                        if (this.mc.thePlayer.inventory.getItemStack() != null)
                        {
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, 0);
                            this.touchUpX = mouseX - i;
                            this.touchUpY = mouseY - j;
                            this.returningStackDestSlot = this.clickedSlot;
                            this.returningStack = this.draggedStack;
                            this.returningStackTime = Minecraft.getSystemTime();
                        }
                        else
                        {
                            this.returningStack = null;
                        }
                    }
                    else if (this.draggedStack != null)
                    {
                        this.touchUpX = mouseX - i;
                        this.touchUpY = mouseY - j;
                        this.returningStackDestSlot = this.clickedSlot;
                        this.returningStack = this.draggedStack;
                        this.returningStackTime = Minecraft.getSystemTime();
                    }

                    this.draggedStack = null;
                    this.clickedSlot = null;
                }
            }
            else if (this.dragSplitting && !this.dragSplittingSlots.isEmpty())
            {
                this.handleMouseClick((Slot)null, -999, Container.func_94534_d(0, this.dragSplittingLimit), 5);

                for (Slot slot1 : this.dragSplittingSlots)
                {
                    this.handleMouseClick(slot1, slot1.slotNumber, Container.func_94534_d(1, this.dragSplittingLimit), 5);
                }

                this.handleMouseClick((Slot)null, -999, Container.func_94534_d(2, this.dragSplittingLimit), 5);
            }
            else if (this.mc.thePlayer.inventory.getItemStack() != null)
            {
                if (state == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100)
                {
                    this.handleMouseClick(slot, k, state, 3);
                }
                else
                {
                    boolean flag1 = k != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));

                    if (flag1)
                    {
                        this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack() : null;
                    }

                    this.handleMouseClick(slot, k, state, flag1 ? 1 : 0);
                }
            }
        }

        if (this.mc.thePlayer.inventory.getItemStack() == null)
        {
            this.lastClickTime = 0L;
        }

        this.dragSplitting = false;
    }

    /**
     * Returns if the passed mouse position is over the specified slot. Args : slot, mouseX, mouseY
     */
    private boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY)
    {
        return this.isPointInRegion(slotIn.xDisplayPosition, slotIn.yDisplayPosition, 16, 16, mouseX, mouseY);
    }

    /**
     * Test if the 2D point is in a rectangle (relative to the GUI). Args : rectX, rectY, rectWidth, rectHeight, pointX,
     * pointY
     */
    protected boolean isPointInRegion(int left, int top, int right, int bottom, int pointX, int pointY)
    {
        int i = this.guiLeft;
        int j = this.guiTop;
        pointX = pointX - i;
        pointY = pointY - j;
        return pointX >= left - 1 && pointX < left + right + 1 && pointY >= top - 1 && pointY < top + bottom + 1;
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType)
    {
        if ((slotId >= 11 && slotId <= 16) || (slotId >= 20 && slotId <= 25) || (slotId >= 29 && slotId <= 34) || (slotId >= 38 && slotId <= 43) || acceptNextClick) {

            acceptNextClick = false;
            if (slotIn != null) {
                slotId = slotIn.slotNumber;
            }

            this.mc.playerController.windowClick(this.inventorySlots.windowId, slotId, clickedButton, clickType, this.mc.thePlayer);
        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode())
        {
            this.mc.thePlayer.closeScreen();
        }

        this.checkHotbarKeys(keyCode);

        if (this.theSlot != null && this.theSlot.getHasStack())
        {
            if (keyCode == this.mc.gameSettings.keyBindPickBlock.getKeyCode())
            {
                this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, 0, 3);
            }
            else if (keyCode == this.mc.gameSettings.keyBindDrop.getKeyCode())
            {
                this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, 4);
            }
        }
    }

    /**
     * This function is what controls the hotbar shortcut check when you press a number key when hovering a stack. Args
     * : keyCode, Returns true if a Hotbar key is pressed, else false
     */
    protected boolean checkHotbarKeys(int keyCode)
    {
        if (this.mc.thePlayer.inventory.getItemStack() == null && this.theSlot != null)
        {
            for (int i = 0; i < 9; ++i)
            {
                if (keyCode == this.mc.gameSettings.keyBindsHotbar[i].getKeyCode())
                {
                    this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, i, 2);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        if (this.mc.thePlayer != null)
        {
            ((AuctionHouseComponent)ApecMain.Instance.getComponent(ComponentId.AUCTION_HOUSE_MENU)).guiIsOpened = false;
            this.inventorySlots.onContainerClosed(this.mc.thePlayer);
        }
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();

        if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead)
        {
            this.mc.thePlayer.closeScreen();
        }
    }

}
