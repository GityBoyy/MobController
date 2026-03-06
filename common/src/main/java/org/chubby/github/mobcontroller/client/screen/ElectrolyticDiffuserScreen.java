package org.chubby.github.mobcontroller.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.chubby.github.mobcontroller.common.menu.ElectrolyticDiffuserMenu;
import org.chubby.github.mobcontroller.util.Utils;

public class ElectrolyticDiffuserScreen extends AbstractContainerScreen<ElectrolyticDiffuserMenu>
{
    public static final ResourceLocation TEXTURE = Utils.resource("textures/gui/menu/electrolytic_diffuser_gui.png");

    public ElectrolyticDiffuserScreen(ElectrolyticDiffuserMenu pMenu, Inventory pInventory, Component pTitle) {
        super(pMenu, pInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics,mouseX,mouseY,delta);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
