package com.example.proceduralbouquets.blockentity;

import com.example.proceduralbouquets.data.BouquetData;
import com.example.proceduralbouquets.data.BouquetEntry;
import com.example.proceduralbouquets.registry.ModBlockEntities;
import com.example.proceduralbouquets.registry.ModItems;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PottedBouquetBlockEntity extends BlockEntity {
    private final List<BouquetEntry> entries = new ArrayList<>();

    public PottedBouquetBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POTTED_BOUQUET.get(), pos, state);
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public void setEntries(List<BouquetEntry> newEntries) {
        entries.clear();
        int max = Math.min(BouquetData.MAX_ENTRIES, newEntries.size());
        for (int i = 0; i < max; i++) {
            entries.add(newEntries.get(i));
        }
    }

    public List<BouquetEntry> getEntriesView() {
        return Collections.unmodifiableList(entries);
    }

    public ItemStack createBouquetStack() {
        ItemStack stack = new ItemStack(ModItems.BOUQUET.get());
        BouquetData.write(stack, entries);
        return stack;
    }

    public void markUpdated() {
        setChanged();
        if (level != null) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, net.minecraft.world.level.block.Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(BouquetData.TAG_ROOT, BouquetData.toTag(entries));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        entries.clear();
        if (tag.contains(BouquetData.TAG_ROOT, CompoundTag.TAG_COMPOUND)) {
            entries.addAll(BouquetData.fromTag(tag.getCompound(BouquetData.TAG_ROOT)));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
