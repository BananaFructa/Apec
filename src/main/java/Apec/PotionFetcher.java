package Apec;

import Apec.Components.Gui.ContainerGuis.ActiveEffectsTransparentGui;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Method;
import java.util.*;

public class PotionFetcher {

    Minecraft mc = Minecraft.getMinecraft();

    public boolean ShouldAwaitThreadsExist = false;
    public boolean IsInitilized = false;
    public boolean NeedsInitialFetch = true;
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

    public void init() {
        mc.thePlayer.sendChatMessage("/effects");
        IsInitilized = true;
    }

    public void DumpAndLoad() {
        ClearAll();
        init();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMsg(ClientChatReceivedEvent event) {
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_POTIONS_EFFECTS)) {
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
    private void CreateNewPotionEffect(String name,String[] UnregularSplitedTime) {
        PotionEffect potionEffect = new PotionEffect();
        potionEffect.effectName = name;
        // Removing the non numerical characters because there is this random crap in it with all these backslashes
        if (UnregularSplitedTime.length == 3) {
            potionEffect.hoursRemaining = Integer.parseInt(ApecUtils.removeNonNumericalChars(UnregularSplitedTime[0]));
            potionEffect.minutesRemaining = Integer.parseInt(ApecUtils.removeNonNumericalChars(UnregularSplitedTime[1]));
            potionEffect.secondsRemaining =  Integer.parseInt(ApecUtils.removeNonNumericalChars(UnregularSplitedTime[2]));
            PotionEffects.add(potionEffect);
        } else if (UnregularSplitedTime.length == 2){
            potionEffect.minutesRemaining = Integer.parseInt(ApecUtils.removeNonNumericalChars(UnregularSplitedTime[0]));
            potionEffect.secondsRemaining =  Integer.parseInt(ApecUtils.removeNonNumericalChars(UnregularSplitedTime[1]));
            PotionEffects.add(potionEffect);
        }
    }

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

    public String PotionToString(PotionEffect Effect) {
        return Effect.effectName
                + " \u00a7f"
                + (Effect.hoursRemaining < 10 && Effect.hoursRemaining != 0 ? "0" : "") + (Effect.hoursRemaining != 0 ? Effect.hoursRemaining + ":" : "")
                + (Effect.minutesRemaining < 10 ? "0" : "") + Effect.minutesRemaining + ":"
                + (Effect.secondsRemaining < 10 ? "0" : "") + Effect.secondsRemaining;
    }

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
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_POTIONS_EFFECTS)) {
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
        }
    }

    @SubscribeEvent
    public void OnDrink(PlayerUseItemEvent.Finish event) {
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_POTIONS_EFFECTS)) {
            if (event.item.getItem() == Items.milk_bucket) {
                PotionEffects.clear();
            }
        }
    }


    // For initial Fetch
    // I know i can make some public functions in the custom gui class instead of doing reflection but i guess it's better when it comes to future modifications
    @SubscribeEvent
    public void OnGuiOpen (final GuiOpenEvent event) {
        try {
            System.out.println(event.gui.getClass().getName());
        } catch (Exception e) {

        }
        if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SHOW_POTIONS_EFFECTS)) {
            if (event.gui instanceof ActiveEffectsTransparentGui && NeedsInitialFetch) {
                Tuple<IInventory, IInventory> UpperLower = ApecUtils.GetUpperLowerFromGuiEvent(event);
                if (UpperLower == null) return;
                CurrentEvent = event;
                if (UpperLower.getSecond().getDisplayName().getUnformattedText().contains("Active Effects")) {
                    Thread AwaitThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                GuiContainer Container;
                                do {
                                    Container = (GuiContainer) CurrentEvent.gui;
                                    assert !ShouldAwaitThreadsExist;
                                } while ((Container.inventorySlots.inventorySlots.get(0).inventory.getStackInSlot(0) == null
                                        || Container.inventorySlots.inventorySlots.get(53).inventory.getStackInSlot(53) == null)
                                        && ShouldAwaitThreadsExist);
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
                                if (Container.inventorySlots.inventorySlots.get(53).inventory.getStackInSlot(53).getItem() == Items.arrow) {
                                    HandleMouseClickMethod.invoke(Container, Container.inventorySlots.inventorySlots.get(53), 53, 0, 0);
                                } else {
                                    mc.displayGuiScreen(null);
                                    NeedsInitialFetch = false;
                                }
                                ShouldAwaitThreadsExist = false;
                                ApecMain.logger.info("Effect fetch thread exited without errors!");
                                Thread.currentThread().interrupt();
                            } catch (Exception e) {
                                ApecMain.logger.info("An error occured in the effect fetch thread, below it's the stack trace hopefully!");
                                e.printStackTrace();
                                ShouldAwaitThreadsExist = false;
                                Thread.currentThread().interrupt();
                            }
                        }
                    });

                    ShouldAwaitThreadsExist = true;
                    AwaitThread.start();
                }
            }
        }

    }

}
