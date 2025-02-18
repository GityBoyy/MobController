package org.chubby.github.mobcontroller.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.chubby.github.mobcontroller.util.Utils;

public class MonsterInventoryScreen extends AbstractContainerScreen<MonsterInventoryMenu>
{
    private final Monster controlledMonster;
    public static final ResourceLocation GUI = Utils.resource("textures/gui/menu/entity_inventory.png");
    public MonsterInventoryScreen(MonsterInventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.controlledMonster = menu.monster;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(GUI,x,y,0,0,imageWidth,imageHeight);
        renderEntity(controlledMonster,x,y,mouseX,mouseY,guiGraphics);
    }

    private void renderEntity(Monster entity, int x, int y, int mouseX, int mouseY, GuiGraphics graphics){
        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, x + 26, y + 18, x + 78, y + 70, 24, 0.25F, mouseX, mouseY, entity);
    }
}
