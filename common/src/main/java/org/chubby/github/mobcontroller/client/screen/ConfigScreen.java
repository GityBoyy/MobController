package org.chubby.github.mobcontroller.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.chubby.github.mobcontroller.core.config.Config;
import org.chubby.github.mobcontroller.core.config.MCConfig;
import org.chubby.github.mobcontroller.core.config.property.IntProperty;

public class ConfigScreen extends Screen {

    public ConfigScreen() {
        super(Component.literal("Mob Controller Config"));
    }

    public ConfigScreen(Screen screen)
    {
        super(Component.literal("Mob Controller Config"));
    }

    @Override
    protected void init() {
        this.addRenderableWidget(new StringWidget(120,60,Component.literal("Mob Controller Config"), this.font));

        this.addRenderableWidget(new StringWidget(120,55,Component.literal("                 "), this.font));

        addIntConfigSlider(MCConfig.controlTick, 50);
    }

    private Component createTitle(String title) {
        return Component.literal(title).setStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.YELLOW));
    }

    private AbstractSliderButton createSeparator(int offset) {
        return new AbstractSliderButton(10, offset, 180, 2, Component.empty(), 0.0) {
            @Override
            protected void updateMessage() {

            }

            @Override
            protected void applyValue() {

            }
        };
    }

    private void addIntConfigSlider(IntProperty property, int yOffset) {
        this.addRenderableWidget(new StringWidget(-5,yOffset,150,20,Component.literal(property.getName()),this.font));
        this.addRenderableWidget(new AbstractSliderButton(100, yOffset, 150, 20,
                Component.literal(property.getValue().toString()), property.getValue() / 500.0) {

            @Override
            protected void updateMessage() {
                this.setMessage(Component.literal(property.getValue().toString()));
            }

            @Override
            protected void applyValue() {
                property.setValue((int) (this.value * 500));
            }

            @Override
            public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
                super.renderWidget(guiGraphics, i, j, f);

                if(this.isHovered())
                {
                    guiGraphics.renderTooltip(font,Component.literal(property.getDescription()),i,j);
                }
            }
        });
    }

    @Override
    public void onClose() {
        Config.saveConfig();
        super.onClose();
    }
}
