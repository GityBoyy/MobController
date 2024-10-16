package org.chubby.github.mobcontroller.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import org.chubby.github.mobcontroller.common.items.ControllerType;
import org.chubby.github.mobcontroller.common.items.ItemController;

public class ControllerLayer<T extends Monster, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public ControllerLayer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ItemController controller) {
            ControllerType type = controller.getControllerType();

            Block block = type.getAssociatedBlock();
            BlockItem blockItem = (BlockItem) block.asItem();

            poseStack.pushPose();

            poseStack.translate(0.0, 1.5, 0.0);
            poseStack.scale(0.5f, 0.5f, 0.5f);

            this.getParentModel().renderToBuffer(poseStack, multiBufferSource.getBuffer(this.getParentModel().renderType(ResourceLocation.parse(blockItem.getDefaultInstance().toString()))), packedLight, OverlayTexture.NO_OVERLAY);

            poseStack.popPose();
        }
    }
}
