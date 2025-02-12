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

    public ConfigScreen(Screen parent) {
        super(Component.literal("Mob Controller Config"));
    }

    @Override
    protected void init() {
        // Title
        this.addRenderableWidget(new StringWidget(width / 2 - 75, 30, Component.literal("Mob Controller Config")
                .setStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.GOLD)), this.font));

        // Subtitle
        this.addRenderableWidget(new StringWidget(width / 2 - 75, 50, Component.literal("Configure your settings here")
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)), this.font));

        // Separator
        this.addRenderableWidget(createSeparator(70));

        // Sliders
        addIntConfigSlider(MCConfig.controlTick, 90);
    }

    private Component createTitle(String title) {
        return Component.literal(title).setStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.YELLOW));
    }

    private AbstractSliderButton createSeparator(int yOffset) {
        return new AbstractSliderButton(width / 2 - 90, yOffset, 180, 2, Component.empty(), 0.0) {
            @Override
            protected void updateMessage() {}

            @Override
            protected void applyValue() {}

            @Override
            public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
                guiGraphics.fillGradient(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height,
                        0xFF555555, 0xFFAAAAAA);
            }
        };
    }

    private void addIntConfigSlider(IntProperty property, int yOffset) {
        // Label
        this.addRenderableWidget(new StringWidget(width / 2 - 150, yOffset + 5, 150, 20,
                Component.literal(property.getName()).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)), this.font));

        // Slider
        this.addRenderableWidget(new AbstractSliderButton(width / 2, yOffset, 150, 20,
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
            public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
                super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);

                // Tooltip
                if (this.isHovered()) {
                    guiGraphics.renderTooltip(font, Component.literal(property.getDescription()), mouseX, mouseY);
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
