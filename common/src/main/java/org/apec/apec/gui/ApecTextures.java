package org.apec.apec.gui;

import net.minecraft.resources.ResourceLocation;

public enum ApecTextures {

    STATUS_BAR("status_bar", 256, 256),
    ICONS("icons", 256, 256);

    private final ResourceLocation resourceLocation;
    private final int width;
    private final int height;

    ApecTextures(String path, int width, int height) {
        this.resourceLocation = new ResourceLocation("apec", "textures/" + path + ".png");
        this.width = width;
        this.height = height;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
