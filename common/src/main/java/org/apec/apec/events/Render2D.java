package org.apec.apec.events;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public record Render2D(int width, int height, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
}
