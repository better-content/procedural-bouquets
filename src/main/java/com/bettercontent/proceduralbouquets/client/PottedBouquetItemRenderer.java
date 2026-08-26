package com.bettercontent.proceduralbouquets.client;

import com.bettercontent.proceduralbouquets.config.ModClientConfig;
import com.bettercontent.proceduralbouquets.data.BouquetData;
import com.bettercontent.proceduralbouquets.data.BouquetEntry;
import com.bettercontent.proceduralbouquets.item.PottedBouquetItem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class PottedBouquetItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static PottedBouquetItemRenderer instance;

    private PottedBouquetItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    public static PottedBouquetItemRenderer get() {
        if (instance == null) {
            Minecraft minecraft = Minecraft.getInstance();
            instance = new PottedBouquetItemRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
        }
        return instance;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        List<BouquetEntry> entries = PottedBouquetItem.readEntries(stack);
        Minecraft minecraft = Minecraft.getInstance();

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        minecraft.getItemRenderer().renderStatic(
            new ItemStack(Items.FLOWER_POT),
            ItemDisplayContext.FIXED,
            packedLight,
            packedOverlay,
            poseStack,
            buffer,
            null,
            0
        );
        if (!entries.isEmpty()) {
            BouquetRenderUtil.renderPottedBouquet(
                entries,
                poseStack,
                buffer,
                packedLight,
                packedOverlay,
                null,
                BouquetData.stableHash(entries),
                ModClientConfig.MAX_RENDERED_FLOWERS.get()
            );
        }
        poseStack.popPose();
    }

}
