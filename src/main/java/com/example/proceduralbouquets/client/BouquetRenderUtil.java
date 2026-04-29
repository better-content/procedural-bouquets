package com.example.proceduralbouquets.client;

import com.example.proceduralbouquets.data.BouquetEntry;
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

            poseStack.pushPose();
            poseStack.translate(xCenter, 0.08F + (entry.yOffset() * 0.01F), zCenter);
            poseStack.mulPose(Axis.YP.rotationDegrees(entry.rotation() * 90.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));

            float scale = 0.35F * entry.scale();
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

    public static void renderCompactBouquet(
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
            float spread = 0.16F;

            poseStack.pushPose();
            poseStack.translate(nx * spread, 0.1F + (i % 5) * 0.012F + (entry.yOffset() * 0.01F), nz * spread);
            poseStack.mulPose(Axis.YP.rotationDegrees(entry.rotation() * 90.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));

            float scale = 0.26F * entry.scale();
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

    public static ItemStack stackForEntry(BouquetEntry entry) {
        return BuiltInRegistries.ITEM.getOptional(entry.itemId())
            .map(ItemStack::new)
            .orElse(ItemStack.EMPTY);
    }
}
