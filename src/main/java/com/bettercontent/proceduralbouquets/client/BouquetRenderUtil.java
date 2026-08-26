package com.bettercontent.proceduralbouquets.client;

import com.bettercontent.proceduralbouquets.data.BouquetEntry;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

public final class BouquetRenderUtil {
    public static final float GRID_FLOWER_HEIGHT = 0.135F;

    private BouquetRenderUtil() {
    }

    public static void renderGridBouquet(
        List<BouquetEntry> entries,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        int packedOverlay,
        Level level,
        long seedBase,
        int maxRendered
    ) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        int max = Math.min(entries.size(), maxRendered);

        for (int i = 0; i < max; i++) {
            BouquetEntry entry = entries.get(i);
            renderGridFlower(
                entry,
                poseStack,
                buffer,
                packedLight,
                packedOverlay,
                level,
                (int) (seedBase + (i * 31L)),
                0.0F,
                itemRenderer
            );
        }
    }

    public static void renderGridFlower(
        BouquetEntry entry,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        int packedOverlay,
        Level level,
        int seed,
        float verticalOffset
    ) {
        renderGridFlower(
            entry,
            poseStack,
            buffer,
            packedLight,
            packedOverlay,
            level,
            seed,
            verticalOffset,
            Minecraft.getInstance().getItemRenderer()
        );
    }

    private static void renderGridFlower(
        BouquetEntry entry,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        int packedOverlay,
        Level level,
        int seed,
        float verticalOffset,
        ItemRenderer itemRenderer
    ) {
        ItemStack stack = stackForEntry(entry);
        if (stack.isEmpty()) {
            return;
        }

        float xCenter = (entry.x() + 0.5F) / 16.0F;
        float zCenter = (entry.z() + 0.5F) / 16.0F;
        float scale = gridFlowerScale(1) * entry.scale();

        poseStack.pushPose();
        poseStack.translate(
            xCenter,
            GRID_FLOWER_HEIGHT + (scale * 0.5F) + (entry.yOffset() * 0.006F) + verticalOffset,
            zCenter
        );
        poseStack.mulPose(Axis.YP.rotationDegrees(entry.rotationDegrees()));
        poseStack.scale(scale, scale, scale);
        itemRenderer.renderStatic(
            stack,
            ItemDisplayContext.FIXED,
            packedLight,
            packedOverlay,
            poseStack,
            buffer,
            level,
            seed
        );
        poseStack.popPose();
    }

    public static void renderGatheredBouquet(
        List<BouquetEntry> entries,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        int packedOverlay,
        Level level,
        long seedBase,
        int maxRendered
    ) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        int max = Math.min(entries.size(), maxRendered);

        for (int i = 0; i < max; i++) {
            BouquetEntry entry = entries.get(i);
            ItemStack stack = stackForEntry(entry);
            if (stack.isEmpty()) {
                continue;
            }

            float nx = (entry.x() - 7.5F) / 7.5F;
            float nz = (entry.z() - 7.5F) / 7.5F;

            poseStack.pushPose();
            poseStack.translate(gatheredStemHorizontalOffset(nx), 0.28F + (nz * 0.17F) + (entry.yOffset() * 0.006F), nz * 0.10F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(gatheredOutwardTiltDegrees(nx)));
            poseStack.mulPose(Axis.YP.rotationDegrees(entry.rotationDegrees()));

            float scale = densityScale(max, 0.48F, 0.30F) * entry.scale();
            poseStack.scale(scale, scale, scale);

            itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                packedLight,
                packedOverlay,
                poseStack,
                buffer,
                level,
                (int) (seedBase + (i * 17L))
            );
            poseStack.popPose();
        }
    }

    public static void renderPottedBouquet(
        List<BouquetEntry> entries,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        int packedOverlay,
        Level level,
        long seedBase,
        int maxRendered
    ) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        int max = Math.min(entries.size(), maxRendered);

        for (int i = 0; i < max; i++) {
            BouquetEntry entry = entries.get(i);
            ItemStack stack = stackForEntry(entry);
            if (stack.isEmpty()) {
                continue;
            }

            float nx = (entry.x() - 7.5F) / 7.5F;
            float nz = (entry.z() - 7.5F) / 7.5F;
            float scale = pottedFlowerScale(max) * entry.scale();

            poseStack.pushPose();
            poseStack.translate(
                pottedHorizontalOffset(nx),
                pottedVerticalCenter(nx, nz, entry.yOffset()),
                pottedHorizontalOffset(nz)
            );
            poseStack.mulPose(Axis.YP.rotationDegrees(entry.rotationDegrees()));
            poseStack.scale(scale, scale, scale);

            itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                packedLight,
                packedOverlay,
                poseStack,
                buffer,
                level,
                (int) (seedBase + (i * 23L))
            );
            poseStack.popPose();
        }
    }

    static float pottedHorizontalOffset(float normalizedCoordinate) {
        return normalizedCoordinate * 0.21F;
    }

    static float gridFlowerScale(int count) {
        return 0.40F;
    }

    static float pottedVerticalCenter(float normalizedX, float normalizedZ, int yOffset) {
        float radius = Math.min(1.0F, (float) Math.sqrt((normalizedX * normalizedX) + (normalizedZ * normalizedZ)));
        return 0.50F + ((1.0F - radius) * 0.12F) + (normalizedZ * 0.025F) + (yOffset * 0.006F);
    }

    static float pottedFlowerScale(int count) {
        return densityScale(count, 0.58F, 0.38F);
    }

    static float pottedItemFlowerLayerScale() {
        return 1.25F;
    }

    static float gatheredTieScale(int count) {
        return densityScale(count, 0.48F, 0.68F);
    }

    static float gatheredTieVerticalOffset() {
        return 0.18F;
    }

    static float gatheredStemHorizontalOffset(float normalizedX) {
        return normalizedX * 0.055F;
    }

    static float gatheredOutwardTiltDegrees(float normalizedX) {
        float edgeAmount = Math.max(0.0F, (Math.abs(normalizedX) - 0.55F) / 0.45F);
        if (edgeAmount == 0.0F) {
            return 0.0F;
        }
        return -Math.signum(normalizedX) * Math.min(edgeAmount, 1.0F) * 7.0F;
    }

    public static ItemStack stackForEntry(BouquetEntry entry) {
        return BuiltInRegistries.ITEM.getOptional(entry.itemId())
            .map(ItemStack::new)
            .orElse(ItemStack.EMPTY);
    }

    static float densityScale(int count, float sparseScale, float denseScale) {
        float crowding = Math.min(Math.max(count - 1, 0), 63) / 63.0F;
        return sparseScale + ((denseScale - sparseScale) * (float) Math.sqrt(crowding));
    }
}
