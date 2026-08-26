package com.bettercontent.proceduralbouquets.client;

import com.bettercontent.proceduralbouquets.ProceduralBouquets;
import com.bettercontent.proceduralbouquets.block.BouquetGridBlock;
import com.bettercontent.proceduralbouquets.block.BouquetPlacement;
import com.bettercontent.proceduralbouquets.blockentity.BouquetGridBlockEntity;
import com.bettercontent.proceduralbouquets.config.ModCommonConfig;
import com.bettercontent.proceduralbouquets.config.ModClientConfig;
import com.bettercontent.proceduralbouquets.data.BouquetEntry;
import com.bettercontent.proceduralbouquets.registry.ModBlocks;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
        if (mc.level == null
            || !mc.level.getBlockState(pos).is(ModBlocks.BOUQUET_GRID.get())
            || !(mc.level.getBlockEntity(pos) instanceof BouquetGridBlockEntity bouquetGrid)) {
            return;
        }

        ItemStack heldFlower = heldFlower(player);
        if (heldFlower.isEmpty()) {
            return;
        }

        Vec3 local = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        int x = BouquetPlacement.gridCoordinate(local.x);
        int z = BouquetPlacement.gridCoordinate(local.z);
        BouquetPlacement.State placementState = BouquetPlacement.state(
            bouquetGrid.getAt(x, z).isPresent(),
            bouquetGrid.isFull(),
            player.isShiftKeyDown(),
            ModCommonConfig.ALLOW_REPLACEMENT_WHEN_SNEAKING.get()
        );
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(heldFlower.getItem());
        BouquetEntry ghost = BouquetPlacement.entry(pos, player.getUUID(), itemId, x, z);

        Vec3 cam = event.getCamera().getPosition();
        event.getPoseStack().pushPose();
        event.getPoseStack().translate(pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z);
        BouquetPlacementGhostRenderer.render(
            ghost,
            placementState,
            event.getPoseStack(),
            event.getMultiBufferSource(),
            LevelRenderer.getLightColor(mc.level, pos.above()),
            0,
            mc.level,
            (int) pos.asLong()
        );
        event.getPoseStack().popPose();

        double minX = pos.getX() + (x / 16.0D);
        double minZ = pos.getZ() + (z / 16.0D);
        double maxX = minX + (1.0D / 16.0D);
        double maxZ = minZ + (1.0D / 16.0D);
        double y = pos.getY() + BouquetGridBlock.TRAY_HEIGHT + 0.001D;

        VertexConsumer consumer = event.getMultiBufferSource().getBuffer(RenderType.lines());
        float r = BouquetPlacementGhostRenderer.red(placementState);
        float g = BouquetPlacementGhostRenderer.green(placementState);
        float b = BouquetPlacementGhostRenderer.blue(placementState);

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

    private static ItemStack heldFlower(Player player) {
        if (BouquetGridBlock.isValidFlower(player.getMainHandItem())) {
            return player.getMainHandItem();
        }
        if (BouquetGridBlock.isValidFlower(player.getOffhandItem())) {
            return player.getOffhandItem();
        }
        return ItemStack.EMPTY;
    }
}
