package com.bettercontent.proceduralbouquets.client;

import com.bettercontent.proceduralbouquets.blockentity.PottedBouquetBlockEntity;
import com.bettercontent.proceduralbouquets.config.ModClientConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class PottedBouquetRenderer implements BlockEntityRenderer<PottedBouquetBlockEntity> {
    public PottedBouquetRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(PottedBouquetBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (be.getLevel() == null || be.getEntriesView().isEmpty()) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        BouquetRenderUtil.renderPottedBouquet(
            be.getEntriesView(),
            poseStack,
            buffer,
            packedLight,
            packedOverlay,
            be.getLevel(),
            be.getBlockPos().asLong(),
            ModClientConfig.MAX_RENDERED_FLOWERS.get()
        );
        poseStack.popPose();
    }
}
