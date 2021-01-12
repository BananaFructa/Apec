/**
 * Fetches the potions from the /effects menu
 */
package Apec.DataInterpretation;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.Components.Gui.ContainerGuis.TrasparentEffects.ActiveEffectsTransparentGui;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PotionFetcher {

    Minecraft mc = Minecraft.getMinecraft();

    public boolean IsInitilized = false;
    public boolean NeedsInitialFetch = true;
    public boolean ShouldRun = false;
    public boolean InLoadingProcess = false;
    private GuiOpenEvent CurrentEvent;

    DataExtractor dataExtractor;

    private Method HandleMouseClickMethod;

    class PotionEffect {

        public String effectName;
        public int secondsRemaining;
        public int minutesRemaining;
        public int hoursRemaining;

    }

    List<PotionEffect> PotionEffects = new ArrayList<PotionEffect>();

    public PotionFetcher(DataExtractor dataExtractor) {
        this.dataExtractor = dataExtractor;
        MinecraftForge.EVENT_BUS.register(this);
        Method[] methods = GuiContainer.class.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals(ApecUtils.getUnObfedMethodNames.get("handleMouseClick"))) {
                HandleMouseClickMethod = m;
                HandleMouseClickMethod.setAccessible(true);
                break;
            }
        }
    }

    /**
     * @brief Initialization for new fetch cycle
     */
    public void init() {
        mc.thePlayer.sendChatMessage("/effects");
        IsInitilized = true;
    }

    /**
     * @brief Reloads the data
     */
    public void DumpAndLoad() {
        ClearAll();
        init();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMsg(ClientChatReceivedEvent event) {
        if (ShouldRun) {
            if ((event.message.getUnformattedText().contains("SIP") || event.message.getUnformattedText().contains("SLURP")) && !event.message.getUnformattedText().contains(":")) {
                DumpAndLoad();
            }
            if ((event.message.getUnformattedText().contains("BUFF")) && !event.message.getUnformattedText().contains(":")) {
                String name = event.message.getSiblings().get(0).getFormattedText();
                String time = event.message.getSiblings().get(0).getChatStyle().getChatHoverEvent().getValue().getSiblings().get(0).getFormattedText();
                String[] timeSplit = ApecUtils.removeAllCodes(time).replace("(", "").replace(")", "").split(":");
                CreateNewPotionEffect(name, timeSplit);

            }
            if (event.message.getUnformattedText().contains(":")) {
                if(event.message.getFormattedText().split(":")[0].contains("Your profile was changed to")) {
                    DumpAndLoad();
                }
            }
        }
    }

    /**
     * @brief Adds a new potion effect
     * @param name = The name of the effect
     * @param UnregularSplitedTime = Time text split by the character ':'
     */
    private void CreateNewPotionEffect(String name,String[] UnregularSplitedTime) {
        int h = 0, m = 0, s = 0;
        int I =  1;
        if (UnregularSplitedTime.length == 3) {
            h = Integer.parseInt(ApecUtils.removeNonNumericalChars(UnregularSplitedTime[0]));
            I = 0;
        }
        m = Integer.parseInt(ApecUtils.removeNonNumericalChars(UnregularSplitedTime[1 - I]));
        s = Integer.parseInt(ApecUtils.removeNonNumericalChars(UnregularSplitedTime[2 - I]));

        PotionEffect potionEffect = new PotionEffect();
        potionEffect.effectName = name;
        potionEffect.hoursRemaining = h;
        potionEffect.minutesRemaining = m;
        potionEffect.secondsRemaining = s;

        String[] WordSplit = name.split(" ");
        String GenericName = ApecUtils.removeAllCodes(name.replace(" " + WordSplit[WordSplit.length-1],""));
        int EffectTier = ApecUtils.RomanStringToValue(ApecUtils.removeAllCodes(WordSplit[WordSplit.length-1]));
        int Duration = s + m * 60 + h * 60 * 60;
        System.out.println(GenericName + " " + EffectTier + " " + Duration);

        for (int i = 0;i < PotionEffects.size();i++) {
            String[] WordSplit_ = PotionEffects.get(i).effectName.split(" ");
            String GenericName_ = ApecUtils.removeAllCodes(PotionEffects.get(i).effectName.replace(" " + WordSplit_[WordSplit_.length-1],""));
            int EffectTier_ = ApecUtils.RomanStringToValue(ApecUtils.removeAllCodes(WordSplit_[WordSplit_.length-1]));
            int Duration_ = PotionEffects.get(i).secondsRemaining + PotionEffects.get(i).minutesRemaining * 60 + PotionEffects.get(i).hoursRemaining * 60 * 60;
            System.out.println(GenericName_ + " " + EffectTier_ + " " + Duration_);
            if (GenericName.equals(GenericName_)) {
                if (EffectTier > EffectTier_ || Duration > Duration_) {
                    PotionEffects.set(i,potionEffect);
                    return;
                }
            }
        }

        PotionEffects.add(potionEffect);
    }

    /**
     * @returnReturns a list of all the potion effects in text form
     */
    public List<String> GetPotionEffects (){
        List<String> Effects = new ArrayList<String>();
        for (int i = 0;i < PotionEffects.size();i++) {
            if (ApecMain.Instance.settingsManager.getSettingState(SettingID.COMPACT_POTION)) {
                String total = PotionToString(PotionEffects.get(i));
                i++;
                if (i >= PotionEffects.size()) {
                    Effects.add(total);
                    break;
                } else {
                    total += "   " + PotionToString(PotionEffects.get(i));
                }
                Effects.add(total);
            } else {
                Effects.add(PotionToString(PotionEffects.get(i)));
            }
        }
        return Effects;
    }

    /**
     * @param Effect = The potion effect which needs to be converted
     * @return Returns the potion effect in text form
     */
    public String PotionToString(PotionEffect Effect) {
        return Effect.effectName
                + " \u00a7f"
                + (Effect.hoursRemaining < 10 && Effect.hoursRemaining != 0 ? "0" : "") + (Effect.hoursRemaining != 0 ? Effect.hoursRemaining + ":" : "")
                + (Effect.minutesRemaining < 10 ? "0" : "") + Effect.minutesRemaining + ":"
                + (Effect.secondsRemaining < 10 ? "0" : "") + Effect.secondsRemaining;
    }

    /**
     * @brief Cleared the potion effects list
     */
    public void ClearAll() {
        if (IsInitilized) {
            IsInitilized = false;
            PotionEffects.clear();
            NeedsInitialFetch = true;
        }
    }

    long LastSystemTime = 0;

    @SubscribeEvent
    public void OnTick(TickEvent.ClientTickEvent tickEvent) {
        ShouldRun = ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_POTIONS_EFFECTS) &&
                    !ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_EFFECTS_AS_IN_TAB) &&
                     ApecMain.Instance.dataExtractor.isInSkyblock;

        if (ShouldRun) {

            if (dataExtractor.isInSkyblock && !IsInitilized) init();
            if (!dataExtractor.isInSkyblock && IsInitilized) ClearAll();
            if (System.currentTimeMillis() - LastSystemTime >= 995 && !dataExtractor.IsDeadInTheCatacombs) {
                List<PotionEffect> EffectsToRemove = new ArrayList<PotionEffect>();
                for (PotionEffect Effect : PotionEffects) {
                    if (Effect.secondsRemaining == 0 && Effect.minutesRemaining == 0 && Effect.hoursRemaining == 0) {
                        EffectsToRemove.add(Effect);
                    } else {
                        Effect.secondsRemaining--;
                        if (Effect.secondsRemaining == -1) {
                            Effect.secondsRemaining = 59;
                            Effect.minutesRemaining--;
                            if (Effect.minutesRemaining == -1) {
                                Effect.minutesRemaining = 59;
                                Effect.hoursRemaining--;
                            }
                        }
                    }
                }
                for (PotionEffect Effect : EffectsToRemove) {
                    PotionEffects.remove(Effect);
                }
                EffectsToRemove.clear();
                LastSystemTime = System.currentTimeMillis();
            }

            // The script for fetching the /effects menu data

            try {

                if (mc.currentScreen instanceof ActiveEffectsTransparentGui && InLoadingProcess) {

                    GuiContainer Container = (GuiContainer) CurrentEvent.gui;

                    if (
                            Container.inventorySlots.inventorySlots.get(0).inventory.getStackInSlot(0) != null ||
                                    Container.inventorySlots.inventorySlots.get(53).inventory.getStackInSlot(53) != null
                    ) {
                        boolean FullBreak = false;
                        for (int i = 0; i < 3 && !FullBreak; i++) {
                            for (int j = 10 + i * 9; j < i * 9 + 17; j++) {
                                if (Container.inventorySlots.inventorySlots.get(j).inventory.getStackInSlot(j) == null) {
                                    FullBreak = true;
                                    break;
                                }
                                String name = Container.inventorySlots.inventorySlots.get(j).inventory.getStackInSlot(j).getDisplayName();
                                String time = "";
                                for (String string : Container.inventorySlots.inventorySlots.get(j).inventory.getStackInSlot(j).getTooltip(mc.thePlayer, false)) {
                                    if (string.contains("Remaining:")) {
                                        time = ApecUtils.removeAllCodes(string.replace("Remaining:", ""));
                                        break;
                                    }
                                }
                                CreateNewPotionEffect(name, time.split(":"));
                            }
                        }

                        InLoadingProcess = false;

                        if (Container.inventorySlots.inventorySlots.get(53).inventory.getStackInSlot(53).getItem() == Items.arrow) {
                            HandleMouseClickMethod.invoke(Container, Container.inventorySlots.inventorySlots.get(53), 53, 0, 0);
                        } else {
                            mc.displayGuiScreen(null);
                            NeedsInitialFetch = false;
                        }

                    }
                }
            } catch (Exception err) {
                err.printStackTrace();
                InLoadingProcess = false;
            }

            //----------------------------------------------
        }

    }

    @SubscribeEvent
    public void OnDrink(PlayerUseItemEvent.Finish event) {
        if (ShouldRun) {
            if (event.item.getItem() == Items.milk_bucket) {
                PotionEffects.clear();
            }
        }
    }


    // For initial Fetch
    // I know i can make some public functions in the custom gui class instead of doing reflection but i guess it's better when it comes to future modifications
    @SubscribeEvent
    public void OnGuiOpen (final GuiOpenEvent event) {
        if (ShouldRun) {
            if (event.gui instanceof ActiveEffectsTransparentGui && NeedsInitialFetch) {
                CurrentEvent = event;
                InLoadingProcess = true;
            }
        }
    }

}
