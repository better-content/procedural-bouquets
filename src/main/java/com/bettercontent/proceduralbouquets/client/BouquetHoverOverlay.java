package com.bettercontent.proceduralbouquets.client;

import com.bettercontent.proceduralbouquets.ProceduralBouquets;
import com.bettercontent.proceduralbouquets.block.BouquetGridBlock;
import com.bettercontent.proceduralbouquets.config.ModClientConfig;
import com.bettercontent.proceduralbouquets.registry.ModBlocks;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ProceduralBouquets.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BouquetHoverOverlay {
    private BouquetHoverOverlay() {
    }

    @SubscribeEvent
    public static void onRenderBlockHighlight(RenderHighlightEvent.Block event) {
        if (!ModClientConfig.RENDER_HOVER_INDICATOR.get()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) {
            return;
        }

        BlockHitResult hit = event.getTarget();
        if (hit == null || hit.getDirection() != net.minecraft.core.Direction.UP) {
            return;
        }

        BlockPos pos = hit.getBlockPos();
        if (!mc.level.getBlockState(pos).is(ModBlocks.BOUQUET_GRID.get())) {
            return;
        }

        boolean placement = BouquetGridBlock.isValidFlower(player.getMainHandItem()) || BouquetGridBlock.isValidFlower(player.getOffhandItem());
        boolean removal = player.isShiftKeyDown() && player.getMainHandItem().isEmpty();
        if (!placement && !removal) {
            return;
        }

        Vec3 local = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        int x = Mth.clamp((int) Math.floor(local.x * 16.0D), 0, 15);
        int z = Mth.clamp((int) Math.floor(local.z * 16.0D), 0, 15);

        double minX = pos.getX() + (x / 16.0D);
        double minZ = pos.getZ() + (z / 16.0D);
        double maxX = minX + (1.0D / 16.0D);
        double maxZ = minZ + (1.0D / 16.0D);
        double y = pos.getY() + 1.001D;

        Vec3 cam = event.getCamera().getPosition();
        VertexConsumer consumer = event.getMultiBufferSource().getBuffer(RenderType.lines());
        float r = placement ? 0.2F : 1.0F;
        float g = placement ? 1.0F : 0.2F;
        float b = 0.2F;

        LevelRenderer.renderLineBox(
            event.getPoseStack(),
            consumer,
            minX - cam.x,
            y - cam.y,
            minZ - cam.z,
            maxX - cam.x,
            y + 0.002D - cam.y,
            maxZ - cam.z,
            r,
            g,
            b,
            1.0F
        );
    }
}
