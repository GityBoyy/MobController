package org.chubby.github.mobcontroller.debug.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.component.DataComponentType;
import org.chubby.github.mobcontroller.debug.data.DataComponentData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DebugScreen {
    private final Minecraft minecraft;
    private Entity hoveredEntity = null;
    private DataComponentData componentData = null;
    private boolean visible = false;

    public DebugScreen() {
        this.minecraft = Minecraft.getInstance();
    }

    public void toggle() {
        visible = !visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void render(GuiGraphics guiGraphics, DeltaTracker partialTick) {
        if (!visible) return;

        // Update the hovered entity
        updateHoveredEntity();

        // Render entity information if an entity is hovered
        if (hoveredEntity != null) {
            int mouseX = (int) minecraft.mouseHandler.xpos();
            int mouseY = (int) minecraft.mouseHandler.ypos();
            renderEntityInfo(guiGraphics, mouseX, mouseY);
        }
    }

    private void updateHoveredEntity() {
        // Get what the player is looking at
        HitResult hitResult = minecraft.hitResult;

        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            hoveredEntity = entityHit.getEntity();

            // If it's a living entity, create the component data handler
            if (hoveredEntity instanceof LivingEntity livingEntity) {
                componentData = new DataComponentData(minecraft, livingEntity);
            } else {
                componentData = null;
            }
        } else {
            hoveredEntity = null;
            componentData = null;
        }
    }

    private void renderEntityInfo(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        List<String> lines = new ArrayList<>();

        // Basic entity information
        lines.add("§6Entity: §f" + hoveredEntity.getName().getString());
        lines.add("§6Type: §f" + hoveredEntity.getType().toString());
        lines.add("§6Health: §f" + (hoveredEntity instanceof LivingEntity livingEntity ?
                livingEntity.getHealth() + "/" + livingEntity.getMaxHealth() : "N/A"));

        // Component data if available
        if (componentData != null) {
            lines.add("");
            lines.add("§e=== Equipment Components ===");

            // Display components for each equipment slot
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.isArmor() || slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
                    Set<DataComponentType<?>> components = componentData.getActiveComponentsForSlot(slot);
                    if (!components.isEmpty()) {
                        lines.add("§6" + formatSlotName(slot) + ": §f" + components.size() + " components");
                        components.forEach(comp -> lines.add("  - " + comp.toString()));
                    }
                }
            }
        }

        // Render the info box
        int lineHeight = minecraft.font.lineHeight + 2;
        int boxWidth = 0;
        for (String line : lines) {
            int width = minecraft.font.width(line);
            if (width > boxWidth) boxWidth = width;
        }

        int boxHeight = lines.size() * lineHeight;
        int x = (int)mouseX + 12;
        int y = (int)mouseY - boxHeight / 2;

        // Ensure the box stays within screen bounds
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        if (x + boxWidth + 10 > screenWidth) x = screenWidth - boxWidth - 10;
        if (y < 5) y = 5;
        if (y + boxHeight + 5 > screenHeight) y = screenHeight - boxHeight - 5;

        // Draw background
        guiGraphics.fill(x - 5, y - 5, x + boxWidth + 5, y + boxHeight + 5, 0xC0000000);
        guiGraphics.fill(x - 5, y - 5, boxWidth + 10, boxHeight + 10, 0xFF999999);

        // Draw text
        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawString(minecraft.font, lines.get(i), x, y + i * lineHeight, 0xFFFFFFFF);
        }
    }

    private String formatSlotName(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> "Helmet";
            case CHEST -> "Chestplate";
            case LEGS -> "Leggings";
            case FEET -> "Boots";
            case MAINHAND -> "Main Hand";
            case OFFHAND -> "Off Hand";
            case BODY -> "Body";
        };
    }
}