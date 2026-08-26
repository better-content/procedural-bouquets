package com.bettercontent.proceduralbouquets.client;

import com.bettercontent.proceduralbouquets.config.ModClientConfig;
import com.bettercontent.proceduralbouquets.data.BouquetData;
import com.bettercontent.proceduralbouquets.data.BouquetEntry;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BouquetItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static BouquetItemRenderer INSTANCE;

    private BouquetItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    public static BouquetItemRenderer get() {
        if (INSTANCE == null) {
            Minecraft mc = Minecraft.getInstance();
            INSTANCE = new BouquetItemRenderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
        }
        return INSTANCE;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        List<BouquetEntry> entries = BouquetData.read(stack);
        long hash = BouquetData.stableHash(entries);

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.08F, 0.5F);

        renderWrap(entries.size(), poseStack, buffer, packedLight, packedOverlay);

        if (!entries.isEmpty()) {
            BouquetRenderUtil.renderGatheredBouquet(entries, poseStack, buffer, packedLight, packedOverlay, null, hash, ModClientConfig.MAX_RENDERED_FLOWERS.get());
        }

        poseStack.popPose();
    }

    private static void renderWrap(int flowerCount, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Minecraft minecraft = Minecraft.getInstance();
        BakedModel model = minecraft.getModelManager().getModel(ClientSetup.BOUQUET_WRAP_MODEL);
        RenderType renderType = RenderType.cutout();
        float scale = BouquetRenderUtil.gatheredWrapperScale(flowerCount);

        poseStack.pushPose();
        poseStack.translate(-(scale * 0.5F), -(scale * (5.0F / 12.0F)), -(scale * 0.5F));
        poseStack.scale(scale, scale, scale);
        minecraft.getBlockRenderer().getModelRenderer().renderModel(
            poseStack.last(),
            buffer.getBuffer(renderType),
            Blocks.AIR.defaultBlockState(),
            model,
            1.0F,
            1.0F,
            1.0F,
            packedLight,
            packedOverlay,
            ModelData.EMPTY,
            renderType
        );
        poseStack.popPose();
    }
}
