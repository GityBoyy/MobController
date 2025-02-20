package org.chubby.github.mobcontroller.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.monster.Monster;
import org.chubby.github.mobcontroller.common.items.ControllerType;
import org.chubby.github.mobcontroller.common.items.ItemController;
import org.chubby.github.mobcontroller.common.items.ItemGoggles;
import org.chubby.github.mobcontroller.common.items.util.ControllerChances;
import org.chubby.github.mobcontroller.util.UtilityMethods;

public class GogglesScreen {
    private static final Minecraft minecraft = Minecraft.getInstance();

    private static final int BACKGROUND_COLOR = 0x80000000;
    private static final int[] TIER_COLORS = {
            0xFF6B00,  // Copper (Orange)
            0xCCCCCC,  // Iron (Silver)
            0xFFD700,  // Gold
            0x00FFCC,  // Diamond (Turquoise)
            0xFF3366   // Netherite (Pink)
    };

    public GogglesScreen() {

    }

    public static void onRenderGui(GuiGraphics graphics) {
        if (!ItemGoggles.isGogglesEquipped()) return;

        var player = minecraft.player;
        if (player == null) return;

        var targetEntity = UtilityMethods.isLookingAtEntity(player, player.level(), 30.0D);
        if (targetEntity.isEmpty() || !(targetEntity.get() instanceof Monster monster)  ) return;
        if(ItemController.getPlayerMobControlMap().containsValue(monster)) return;
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        int centerX = screenWidth / 2;
        int startY = screenHeight / 2 - 50;

        String monsterName = monster.getName().getString();
        drawCenteredText(graphics, "§l" + monsterName, centerX, startY, 0xFFFFFF);

        String[] tierNames = {"Copper", "Iron", "Gold", "Diamond", "Netherite"};
        ControllerType[] tiers = {
                ControllerType.COPPER,
                ControllerType.IRON,
                ControllerType.GOLD,
                ControllerType.DIAMOND,
                ControllerType.NETHERITE
        };

        drawCenteredText(graphics, "§lControl Chances:", centerX, startY + 15, 0xFFFFFF);

        for (int i = 0; i < tierNames.length; i++) {
            int chance = ControllerChances.getControlChance(monster, tiers[i]);
            String text = String.format("%s: %d%%", tierNames[i], chance);
            drawCenteredText(graphics, text, centerX, startY + 30 + (i * 12), TIER_COLORS[i]);
        }
    }

    private static void drawCenteredText(GuiGraphics graphics, String text, int x, int y, int color) {
        int width = minecraft.font.width(text);
        int padding = 2;

        graphics.fill(
                x - width/2 - padding,
                y - padding,
                x + width/2 + padding,
                y + minecraft.font.lineHeight + padding,
                BACKGROUND_COLOR
        );

        graphics.drawString(
                minecraft.font,
                text,
                x - width/2,
                y,
                color,
                true
        );
    }
}