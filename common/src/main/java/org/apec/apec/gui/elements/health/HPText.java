package org.apec.apec.gui.elements.health;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import org.apec.apec.gui.Element;
import org.apec.apec.gui.ElementType;
import org.apec.apec.skyblock.SkyBlockInfo;
import org.apec.apec.utils.ApecUtils;
import org.joml.Vector2f;

public class HPText extends Element {

    private int textWidth;

    public HPText() {
        super(ElementType.HP_BAR);
    }

    @Override
    public void drawText(PoseStack poseStack, Vector2f scaledResolution) {
        this.scaledResolution = scaledResolution;

        boolean showAPBar = false;

        int hp = SkyBlockInfo.getInstance().getPlayerStats().hp();
        int base_hp = SkyBlockInfo.getInstance().getPlayerStats().base_hp();
        int ap = SkyBlockInfo.getInstance().getPlayerStats().absorption();
        int base_ap = SkyBlockInfo.getInstance().getPlayerStats().base_absorption();
        int heal_duration = SkyBlockInfo.getInstance().getPlayerStats().heal_duration();
        char heal_duration_tick = SkyBlockInfo.getInstance().getPlayerStats().heal_duration_tick();

        int addedHp = hp + ap;
        String hpText = (!showAPBar && ap != 0 ? ChatFormatting.YELLOW + String.valueOf(addedHp) + ChatFormatting.RESET : hp) + "/" + base_hp + " HP" + (heal_duration != 0 ? " +" + heal_duration + "/s " + heal_duration_tick : "");

        Vector2f statBar = ApecUtils.scalarMultiply(getCurrentAnchorPoint(), 1f / scale);

        ApecUtils.drawOutlineText(mc, poseStack, hpText, (int) (statBar.x - mc.font.width(hpText)), (int) (statBar.y - 10), 0xd10808);
        textWidth = mc.font.width(hpText);

        if (ap != 0 && showAPBar) {
            String apText = ap + "/" + base_ap + " AP";
            ApecUtils.drawOutlineText(mc, poseStack, apText, (int) (statBar.x - 5 - mc.font.width(apText) - mc.font.width(hpText)), (int) (statBar.y - 10), 0x1966AD);
        }
    }

    @Override
    public Vector2f getAnchorPointPosition() {
        return manager.applyGlobalChanges(this, new Vector2f(scaledResolution.x - 190 + 112 + 70, 15));
    }

    @Override
    public Vector2f getBoundingPoint() {
        return new Vector2f(-textWidth, -11);
    }
}
