package org.chubby.github.mobcontroller.client.renderer.blockEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import org.chubby.github.mobcontroller.client.model.block.ScepticTankModel;
import org.chubby.github.mobcontroller.common.blocks.entity.ScepticTankBE;
import org.jetbrains.annotations.NotNull;

public class ScepticTankRenderer implements BlockEntityRenderer<ScepticTankBE> {
    public ScepticTankRenderer(BlockEntityRendererProvider.Context ctx)
    {
    }

    @Override
    public void render(@NotNull ScepticTankBE scepticTankBE, float partialTick,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        poseStack.pushPose();

        ScepticTankModel.render(poseStack,scepticTankBE.getBlockPos(),bufferSource,packedLight,packedOverlay);

        poseStack.popPose();
    }
}
