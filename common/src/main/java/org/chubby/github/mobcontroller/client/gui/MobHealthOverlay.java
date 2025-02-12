package org.chubby.github.mobcontroller.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.chubby.github.mobcontroller.Constants;
import org.chubby.github.mobcontroller.util.Utils;

public class MobHealthOverlay
{
    private static final ResourceLocation OVERLAY_ELEMENTS = Utils.resource("textures/gui/overlay_elements.png");
    private static final int BAR_WIDTH = 182;
    private static final int BAR_HEIGHT = 10;
    private static final int ICON_SIZE = 16;
    private static final int BACKGROUND_PADDING = 4;

    public static void render(GuiGraphics graphics, LivingEntity mob, Player player, int screenWidth, int screenHeight) {
        if (mob == null || !mob.isAlive()) return;

        int baseX = (screenWidth - BAR_WIDTH) / 2;
        int baseY = screenHeight - 50;

        renderBackground(graphics, baseX, baseY);
        renderHealthBar(graphics, mob, baseX, baseY);
    }

    private static void renderBackground(GuiGraphics graphics, int x, int y) {
        // Semi-transparent dark background
        int bgWidth = BAR_WIDTH + (BACKGROUND_PADDING * 2);
        int bgHeight = BAR_HEIGHT + ICON_SIZE + (BACKGROUND_PADDING * 2);
        graphics.fill(
                x - BACKGROUND_PADDING,
                y - BACKGROUND_PADDING,
                x + bgWidth,
                y + bgHeight,
                0x80000000
        );
    }

    private static void renderHealthBar(GuiGraphics graphics, LivingEntity mob, int x, int y) {
        float maxHealth = mob.getMaxHealth();
        float currentHealth = mob.getHealth();
        float healthPercentage = Mth.clamp(currentHealth / maxHealth, 0.0F, 1.0F);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        graphics.blit(OVERLAY_ELEMENTS, x, y + ICON_SIZE, 0, 0, BAR_WIDTH, BAR_HEIGHT);

        int filledWidth = (int)(BAR_WIDTH * healthPercentage);
        if (filledWidth > 0) {
            float hue = healthPercentage * 0.3F;
            int color = java.awt.Color.HSBtoRGB(hue, 1.0F, 1.0F);

            RenderSystem.setShaderColor(
                    ((color >> 16) & 0xFF) / 255F,
                    ((color >> 8) & 0xFF) / 255F,
                    (color & 0xFF) / 255F,
                    1.0F
            );

            graphics.blit(OVERLAY_ELEMENTS, x, y + ICON_SIZE, 0, BAR_HEIGHT, filledWidth, BAR_HEIGHT);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

//        float absorption = mob.getAbsorptionAmount();
//        if (absorption > 0) {
//            float absorptionPercentage = Mth.clamp(absorption / maxHealth, 0.0F, 1.0F);
//            int absorptionWidth = (int)(BAR_WIDTH * absorptionPercentage);
//
//            // Gold color for absorption
//            RenderSystem.setShaderColor(1.0F, 0.8F, 0.0F, 1.0F);
//            graphics.blit(OVERLAY_ELEMENTS, x, y + ICON_SIZE - 2, 0, BAR_HEIGHT * 2, absorptionWidth, 2);
//            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        }

        RenderSystem.disableBlend();
    }
}
