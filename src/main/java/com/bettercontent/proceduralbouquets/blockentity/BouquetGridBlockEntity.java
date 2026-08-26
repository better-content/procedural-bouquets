package com.bettercontent.proceduralbouquets.blockentity;

import com.bettercontent.proceduralbouquets.config.ModCommonConfig;
import com.bettercontent.proceduralbouquets.data.BouquetData;
import com.bettercontent.proceduralbouquets.data.BouquetEntry;
import com.bettercontent.proceduralbouquets.registry.ModBlockEntities;
import com.bettercontent.proceduralbouquets.registry.ModItems;
import com.bettercontent.proceduralbouquets.menu.BouquetGridMenu;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

public class BouquetGridBlockEntity extends BlockEntity implements Container, MenuProvider {
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

    public boolean rotateAt(int x, int z, int quarterTurns) {
        for (int i = 0; i < entries.size(); i++) {
            BouquetEntry entry = entries.get(i);
            if (entry.x() == x && entry.z() == z) {
                entries.set(i, entry.rotatedByQuarterTurns(quarterTurns));
                markUpdated();
                return true;
            }
        }
        return false;
    }

    public boolean isFull() {
        return entries.size() >= Math.min(BouquetData.MAX_ENTRIES, ModCommonConfig.MAX_FLOWERS_PER_BOUQUET.get());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.procedural_bouquets.bouquet_grid");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new BouquetGridMenu(id, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return BouquetGridMenu.GRID_SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        int x = BouquetGridMenu.xForSlot(slot);
        int z = BouquetGridMenu.zForSlot(slot);
        return getAt(x, z)
            .map(BouquetEntry::itemId)
            .flatMap(BuiltInRegistries.ITEM::getOptional)
            .map(ItemStack::new)
            .orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack removed = removeItemNoUpdate(slot);
        if (!removed.isEmpty()) {
            markUpdated();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        int x = BouquetGridMenu.xForSlot(slot);
        int z = BouquetGridMenu.zForSlot(slot);
        return removeAt(x, z)
            .map(BouquetEntry::itemId)
            .flatMap(BuiltInRegistries.ITEM::getOptional)
            .map(ItemStack::new)
            .orElse(ItemStack.EMPTY);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        int x = BouquetGridMenu.xForSlot(slot);
        int z = BouquetGridMenu.zForSlot(slot);
        Optional<BouquetEntry> current = getAt(x, z);

        if (stack.isEmpty()) {
            if (current.isPresent()) {
                removeAt(x, z);
                markUpdated();
            }
            return;
        }
        if (!canPlaceItem(slot, stack)) {
            return;
        }

        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (current.filter(entry -> entry.itemId().equals(id)).isPresent()) {
            return;
        }

        current.ifPresent(ignored -> removeAt(x, z));
        long seed = worldPosition.asLong() ^ ((long) id.hashCode() << 32) ^ slot;
        int rotation = (int) Math.floorMod(seed, 4L);
        float scale = 0.85F + (Math.floorMod(seed >>> 2, 21L) / 100.0F);
        entries.add(new BouquetEntry(id, x, z, rotation, scale, 0));
        markUpdated();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (!com.bettercontent.proceduralbouquets.block.BouquetGridBlock.isValidFlower(stack)) {
            return false;
        }
        int x = BouquetGridMenu.xForSlot(slot);
        int z = BouquetGridMenu.zForSlot(slot);
        return getAt(x, z).isPresent() || !isFull();
    }

    @Override
    public void clearContent() {
        if (!entries.isEmpty()) {
            entries.clear();
            markUpdated();
        }
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
