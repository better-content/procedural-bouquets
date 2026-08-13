package com.bettercontent.proceduralbouquets.item;

import com.bettercontent.proceduralbouquets.blockentity.PottedBouquetBlockEntity;
import com.bettercontent.proceduralbouquets.client.BouquetItemRenderer;
import com.bettercontent.proceduralbouquets.config.ModCommonConfig;
import com.bettercontent.proceduralbouquets.data.BouquetData;
import com.bettercontent.proceduralbouquets.data.BouquetEntry;
import com.bettercontent.proceduralbouquets.registry.ModBlocks;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class BouquetItem extends Item {
    public BouquetItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        List<BouquetEntry> entries = BouquetData.read(stack);
        if (entries.isEmpty()) {
            return InteractionResult.PASS;
        }

        BlockPos clicked = context.getClickedPos();
        Direction face = context.getClickedFace();
        BlockPlaceContext placeContext = new BlockPlaceContext(context);

        BlockPos placePos = clicked;
        BlockState clickedState = level.getBlockState(clicked);
        if (!clickedState.canBeReplaced(placeContext)) {
            placePos = clicked.relative(face);
        }

        if (!level.getBlockState(placePos).canBeReplaced(placeContext)) {
            return InteractionResult.FAIL;
        }

        if (!level.setBlock(placePos, ModBlocks.POTTED_BOUQUET.get().defaultBlockState(), 3)) {
            return InteractionResult.FAIL;
        }

        if (level.getBlockEntity(placePos) instanceof PottedBouquetBlockEntity be) {
            be.setEntries(entries);
            be.markUpdated();
        }

        if (!context.getPlayer().isCreative() || ModCommonConfig.CONSUME_FLOWERS_IN_CREATIVE.get()) {
            stack.shrink(1);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        List<BouquetEntry> entries = BouquetData.read(stack);
        tooltip.add(Component.translatable("tooltip.procedural_bouquets.flower_count", entries.size()).withStyle(ChatFormatting.GRAY));

        int shown = Math.min(5, entries.size());
        for (int i = 0; i < shown; i++) {
            BouquetEntry entry = entries.get(i);
            BuiltInRegistries.ITEM.getOptional(entry.itemId()).ifPresent(item ->
                tooltip.add(Component.literal("- ").append(item.getDescription()).withStyle(ChatFormatting.DARK_GRAY))
            );
        }

        if (entries.size() > shown) {
            tooltip.add(Component.translatable("tooltip.procedural_bouquets.more", entries.size() - shown).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return BouquetItemRenderer.get();
            }
        });
    }
}
