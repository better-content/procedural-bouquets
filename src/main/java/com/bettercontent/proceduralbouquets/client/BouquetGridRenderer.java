package com.bettercontent.proceduralbouquets.client;

import com.bettercontent.proceduralbouquets.blockentity.BouquetGridBlockEntity;
import com.bettercontent.proceduralbouquets.config.ModClientConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class BouquetGridRenderer implements BlockEntityRenderer<BouquetGridBlockEntity> {
    public BouquetGridRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BouquetGridBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (be.getLevel() == null || be.getEntriesView().isEmpty()) {
            return;
        }
        BouquetRenderUtil.renderGridBouquet(
            be.getEntriesView(),
            poseStack,
            buffer,
            packedLight,
            packedOverlay,
            be.getLevel(),
            be.getBlockPos().asLong(),
            ModClientConfig.MAX_RENDERED_FLOWERS.get()
        );
    }
}
