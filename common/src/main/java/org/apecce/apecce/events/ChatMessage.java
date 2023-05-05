package org.apecce.apecce.events;

import net.minecraft.network.chat.Component;

public record ChatMessage(Component component, boolean isOverlay) {
}
