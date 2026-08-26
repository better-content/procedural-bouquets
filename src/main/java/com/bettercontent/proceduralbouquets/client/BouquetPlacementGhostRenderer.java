package com.bettercontent.proceduralbouquets.client;

import com.bettercontent.proceduralbouquets.block.BouquetPlacement;
import com.bettercontent.proceduralbouquets.data.BouquetEntry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;

public final class BouquetPlacementGhostRenderer {
    private static final float GHOST_ALPHA = 0.45F;
    private static final float Z_FIGHT_OFFSET = 0.002F;

    private BouquetPlacementGhostRenderer() {
    }

    public static void render(
        BouquetEntry entry,
        BouquetPlacement.State state,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        int packedOverlay,
        Level level,
        int seed
    ) {
        float red = red(state);
        float green = green(state);
        float blue = blue(state);
        MultiBufferSource translucentTint = ignored -> new TintedVertexConsumer(
            buffer.getBuffer(RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS)),
            red,
            green,
            blue,
            GHOST_ALPHA
        );
        BouquetRenderUtil.renderGridFlower(
            entry,
            poseStack,
            translucentTint,
            packedLight,
            packedOverlay,
            level,
            seed,
            Z_FIGHT_OFFSET
        );
    }

    public static float red(BouquetPlacement.State state) {
        return switch (state) {
            case VALID -> 0.45F;
            case REPLACEMENT -> 1.0F;
            case BLOCKED_OCCUPIED, BLOCKED_FULL -> 1.0F;
        };
    }

    public static float green(BouquetPlacement.State state) {
        return switch (state) {
            case VALID -> 1.0F;
            case REPLACEMENT -> 0.68F;
            case BLOCKED_OCCUPIED, BLOCKED_FULL -> 0.2F;
        };
    }

    public static float blue(BouquetPlacement.State state) {
        return switch (state) {
            case VALID -> 0.55F;
            case REPLACEMENT -> 0.12F;
            case BLOCKED_OCCUPIED, BLOCKED_FULL -> 0.2F;
        };
    }

    private static final class TintedVertexConsumer implements VertexConsumer {
        private final VertexConsumer delegate;
        private final float red;
        private final float green;
        private final float blue;
        private final float alpha;

        private TintedVertexConsumer(VertexConsumer delegate, float red, float green, float blue, float alpha) {
            this.delegate = delegate;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            delegate.vertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            delegate.color(
                Math.round(red * this.red),
                Math.round(green * this.green),
                Math.round(blue * this.blue),
                Math.round(alpha * this.alpha)
            );
            return this;
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            delegate.uv(u, v);
            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {
            delegate.overlayCoords(u, v);
            return this;
        }

        @Override
        public VertexConsumer uv2(int u, int v) {
            delegate.uv2(u, v);
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            delegate.normal(x, y, z);
            return this;
        }

        @Override
        public void endVertex() {
            delegate.endVertex();
        }

        @Override
        public void defaultColor(int red, int green, int blue, int alpha) {
            delegate.defaultColor(
                Math.round(red * this.red),
                Math.round(green * this.green),
                Math.round(blue * this.blue),
                Math.round(alpha * this.alpha)
            );
        }

        @Override
        public void unsetDefaultColor() {
            delegate.unsetDefaultColor();
        }
    }
}
