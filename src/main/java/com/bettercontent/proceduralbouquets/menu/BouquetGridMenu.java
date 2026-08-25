package com.bettercontent.proceduralbouquets.menu;

import com.bettercontent.proceduralbouquets.block.BouquetGridBlock;
import com.bettercontent.proceduralbouquets.blockentity.BouquetGridBlockEntity;
import com.bettercontent.proceduralbouquets.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class BouquetGridMenu extends AbstractContainerMenu {
    public static final int GRID_COLUMNS = 16;
    public static final int GRID_ROWS = 16;
    public static final int GRID_SLOT_COUNT = GRID_COLUMNS * GRID_ROWS;
    public static final int IMAGE_WIDTH = 304;
    public static final int IMAGE_HEIGHT = 406;
    public static final int GRID_LEFT = 8;
    public static final int GRID_TOP = 18;
    public static final int PLAYER_INVENTORY_TOP = 324;
    public static final int PLAYER_HOTBAR_TOP = 382;

    private final Container grid;

    public BouquetGridMenu(int id, Inventory inventory, FriendlyByteBuf data) {
        this(id, inventory, findGrid(inventory, data == null ? BlockPos.ZERO : data.readBlockPos()));
    }

    public BouquetGridMenu(int id, Inventory inventory, Container grid) {
        super(ModMenus.BOUQUET_GRID.get(), id);
        this.grid = grid;
        checkContainerSize(grid, GRID_SLOT_COUNT);
        grid.startOpen(inventory.player);

        for (int z = 0; z < GRID_ROWS; z++) {
            for (int x = 0; x < GRID_COLUMNS; x++) {
                int index = slotIndex(x, z);
                addSlot(new FlowerSlot(grid, index, GRID_LEFT + (x * 18), GRID_TOP + (z * 18)));
            }
        }

        int playerLeft = (IMAGE_WIDTH - 162) / 2;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + (row * 9) + 9, playerLeft + (column * 18), PLAYER_INVENTORY_TOP + (row * 18)));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, playerLeft + (column * 18), PLAYER_HOTBAR_TOP));
        }
    }

    private static Container findGrid(Inventory inventory, BlockPos pos) {
        if (inventory.player.level().getBlockEntity(pos) instanceof BouquetGridBlockEntity grid) {
            return grid;
        }
        return new SimpleContainer(GRID_SLOT_COUNT);
    }

    public static int slotIndex(int x, int z) {
        if (x < 0 || x >= GRID_COLUMNS || z < 0 || z >= GRID_ROWS) {
            throw new IndexOutOfBoundsException("Bouquet grid coordinate outside 16x16: " + x + "," + z);
        }
        return (z * GRID_COLUMNS) + x;
    }

    public static int xForSlot(int slot) {
        checkGridSlot(slot);
        return slot % GRID_COLUMNS;
    }

    public static int zForSlot(int slot) {
        checkGridSlot(slot);
        return slot / GRID_COLUMNS;
    }

    private static void checkGridSlot(int slot) {
        if (slot < 0 || slot >= GRID_SLOT_COUNT) {
            throw new IndexOutOfBoundsException("Bouquet grid slot outside 0..255: " + slot);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return grid.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        grid.stopOpen(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) {
            return ItemStack.EMPTY;
        }
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack source = slot.getItem();
        ItemStack original = source.copy();
        if (index < GRID_SLOT_COUNT) {
            if (!moveItemStackTo(source, GRID_SLOT_COUNT, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!BouquetGridBlock.isValidFlower(source)) {
                return ItemStack.EMPTY;
            }
            boolean movedAny = false;
            while (!source.isEmpty()) {
                int before = source.getCount();
                if (!moveItemStackTo(source, 0, GRID_SLOT_COUNT, false) || source.getCount() == before) {
                    break;
                }
                movedAny = true;
            }
            if (!movedAny) {
                return ItemStack.EMPTY;
            }
        }

        if (source.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        slot.onTake(player, source);
        return original;
    }

    private static final class FlowerSlot extends Slot {
        private FlowerSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return BouquetGridBlock.isValidFlower(stack) && container.canPlaceItem(index, stack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return 1;
        }
    }
}
