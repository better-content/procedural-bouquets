package com.bettercontent.proceduralbouquets.item;

import com.bettercontent.proceduralbouquets.blockentity.PottedBouquetBlockEntity;
import com.bettercontent.proceduralbouquets.client.PottedBouquetItemRenderer;
import com.bettercontent.proceduralbouquets.data.BouquetData;
import com.bettercontent.proceduralbouquets.data.BouquetEntry;
import com.bettercontent.proceduralbouquets.registry.ModBlockEntities;
import com.bettercontent.proceduralbouquets.registry.ModItems;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public final class PottedBouquetItem extends BlockItem {
    public PottedBouquetItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static ItemStack createStack(List<BouquetEntry> entries) {
        ItemStack stack = new ItemStack(ModItems.POTTED_BOUQUET_ITEM.get());
        if (!entries.isEmpty()) {
            CompoundTag blockEntityData = new CompoundTag();
            blockEntityData.put(BouquetData.TAG_ROOT, BouquetData.toTag(entries));
            BlockItem.setBlockEntityData(stack, ModBlockEntities.POTTED_BOUQUET.get(), blockEntityData);
        }
        return stack;
    }

    public static List<BouquetEntry> readEntries(ItemStack stack) {
        CompoundTag blockEntityData = BlockItem.getBlockEntityData(stack);
        if (blockEntityData == null || !blockEntityData.contains(BouquetData.TAG_ROOT, Tag.TAG_COMPOUND)) {
            return List.of();
        }
        return BouquetData.fromTag(blockEntityData.getCompound(BouquetData.TAG_ROOT));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        List<BouquetEntry> entries = readEntries(stack);
        tooltip.add(Component.translatable("tooltip.procedural_bouquets.flower_count", entries.size()).withStyle(ChatFormatting.GRAY));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return PottedBouquetItemRenderer.get();
            }
        });
    }
}
