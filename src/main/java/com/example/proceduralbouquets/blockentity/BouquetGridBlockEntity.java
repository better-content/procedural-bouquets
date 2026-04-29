package com.example.proceduralbouquets.blockentity;

import com.example.proceduralbouquets.config.ModCommonConfig;
import com.example.proceduralbouquets.data.BouquetData;
import com.example.proceduralbouquets.data.BouquetEntry;
import com.example.proceduralbouquets.registry.ModBlockEntities;
import com.example.proceduralbouquets.registry.ModItems;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BouquetGridBlockEntity extends BlockEntity {
    private final List<BouquetEntry> entries = new ArrayList<>();

    public BouquetGridBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BOUQUET_GRID.get(), pos, state);
    }

    public List<BouquetEntry> getEntriesView() {
        return Collections.unmodifiableList(entries);
    }

    public boolean addEntry(BouquetEntry entry) {
        if (isFull() || getAt(entry.x(), entry.z()).isPresent()) {
            return false;
        }
        entries.add(entry);
        return true;
    }

    public Optional<BouquetEntry> removeAt(int x, int z) {
        for (int i = 0; i < entries.size(); i++) {
            BouquetEntry entry = entries.get(i);
            if (entry.x() == x && entry.z() == z) {
                entries.remove(i);
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    public Optional<BouquetEntry> getAt(int x, int z) {
        return entries.stream().filter(entry -> entry.x() == x && entry.z() == z).findFirst();
    }

    public boolean isFull() {
        return entries.size() >= Math.min(BouquetData.MAX_ENTRIES, ModCommonConfig.MAX_FLOWERS_PER_BOUQUET.get());
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
