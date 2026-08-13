package com.bettercontent.proceduralbouquets.client;

import com.bettercontent.proceduralbouquets.config.ModClientConfig;
import com.bettercontent.proceduralbouquets.data.BouquetData;
import com.bettercontent.proceduralbouquets.data.BouquetEntry;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BouquetItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static BouquetItemRenderer INSTANCE;

    private final Map<Long, List<BouquetEntry>> cache = new HashMap<>();

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

    public static void clearCache() {
        if (INSTANCE != null) {
            INSTANCE.cache.clear();
        }
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        List<BouquetEntry> entries = BouquetData.read(stack);
        long hash = BouquetData.stableHash(entries);

        if (cache.size() > 256) {
            cache.clear();
        }

        List<BouquetEntry> prepared = cache.computeIfAbsent(hash, key -> List.copyOf(entries));

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.15F, 0.5F);

        // Simple fallback wrapping/stem visuals.
        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(Items.PAPER), ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, buffer, null, 0);
        poseStack.translate(0.0F, -0.15F, 0.0F);
        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(Items.STICK), ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, buffer, null, 0);
        poseStack.translate(0.0F, 0.15F, 0.0F);

        if (!prepared.isEmpty()) {
            BouquetRenderUtil.renderCompactBouquet(prepared, poseStack, buffer, packedLight, packedOverlay, null, hash, ModClientConfig.MAX_RENDERED_FLOWERS.get());
        }

        poseStack.popPose();
    }
}
