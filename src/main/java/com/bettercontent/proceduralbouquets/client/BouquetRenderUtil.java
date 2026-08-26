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
            ItemStack stack = stackForEntry(entry);
            if (stack.isEmpty()) {
                continue;
            }

            float xCenter = (entry.x() + 0.5F) / 16.0F;
            float zCenter = (entry.z() + 0.5F) / 16.0F;
            float scale = densityScale(max, 0.30F, 0.20F) * entry.scale();

            poseStack.pushPose();
            poseStack.translate(
                xCenter,
                GRID_FLOWER_HEIGHT + (scale * 0.5F) + (entry.yOffset() * 0.006F),
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
                (int) (seedBase + (i * 31L))
            );
            poseStack.popPose();
        }
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
            poseStack.translate(nx * 0.25F, 0.28F + (nz * 0.17F) + (entry.yOffset() * 0.006F), nz * 0.10F);
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

            poseStack.pushPose();
            poseStack.translate(nx * 0.16F, 0.54F + (nz * 0.11F) + (entry.yOffset() * 0.005F), nz * 0.14F);
            poseStack.mulPose(Axis.YP.rotationDegrees(entry.rotationDegrees()));

            float scale = densityScale(max, 0.40F, 0.28F) * entry.scale();
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
