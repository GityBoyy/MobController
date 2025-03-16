package org.chubby.github.mobcontroller.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.chubby.github.mobcontroller.common.menu.DataDisplayerMenu;
import org.chubby.github.mobcontroller.util.Utils;

public class DataDisplayerScreen extends AbstractContainerScreen<DataDisplayerMenu>
{
    private static final ResourceLocation GUI = Utils.resource("textures/gui/data_displayer_gui.png");
    private int x;
    private int y;
    private final Minecraft minecraft;
    public DataDisplayerScreen(DataDisplayerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        minecraft = Minecraft.getInstance();
    }

    @Override
    protected void init() {
        super.init();
        x = (width-imageWidth)/2;
        y = (height-imageHeight)/2;
        this.inventoryLabelY = Integer.MAX_VALUE; // We dont need it lol
        this.titleLabelY = Integer.MAX_VALUE;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F,1.0F,1.0F,1.0F);
        RenderSystem.setShaderTexture(0,GUI);
        guiGraphics.blit(GUI,x,y,0,0,imageWidth,imageHeight);
        for(int i=0;i<menu.getControlledIds().size();i++){
            guiGraphics.drawString(minecraft.font,menu.getControlledIds().get(i).getDisplayName(),x+10,y+10,0x0000);
        }
    }


}
