package org.apec.apec.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import org.apec.apec.gui.Element;
import org.apec.apec.gui.ElementType;
import org.apec.apec.skyblock.SkyBlockInfo;
import org.apec.apec.utils.ApecUtils;
import org.joml.Vector2f;

public class HPText extends Element {

    public HPText() {
        super(ElementType.HP_BAR);
    }

    @Override
    public void drawText(PoseStack poseStack, Vector2f scaledResolution) {

        boolean showAPBar = false;

        int hp = SkyBlockInfo.getInstance().getPlayerStats().hp();
        int base_hp = SkyBlockInfo.getInstance().getPlayerStats().base_hp();
        int ap = SkyBlockInfo.getInstance().getPlayerStats().absorption();
        int base_ap = SkyBlockInfo.getInstance().getPlayerStats().base_absorption();
        int heal_duration = SkyBlockInfo.getInstance().getPlayerStats().heal_duration();
        char heal_duration_tick = SkyBlockInfo.getInstance().getPlayerStats().heal_duration_tick();

        int addedHp = hp + ap;
        String hpText = (!showAPBar && ap != 0 ? ChatFormatting.YELLOW + String.valueOf(addedHp) + ChatFormatting.RESET : hp) + "/" + base_hp + " HP" + (heal_duration != 0 ? " +" + heal_duration + "/s " + heal_duration_tick : "");

        int width = (int) (scaledResolution.x - mc.font.width(hpText) - 5);
        ApecUtils.drawOutlineText(mc, poseStack, hpText, width, 5, 0xd10808);

        if (ap != 0 && showAPBar) {
            String apText = ap + "/" + base_ap + " AP";
            ApecUtils.drawOutlineText(mc, poseStack, apText, (int) (scaledResolution.x - 10 - mc.font.width(apText) - mc.font.width(hpText)), 5, 0x1966AD);
        }
    }
}
