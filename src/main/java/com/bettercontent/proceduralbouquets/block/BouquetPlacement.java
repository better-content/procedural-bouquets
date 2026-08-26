package com.bettercontent.proceduralbouquets.block;

import com.bettercontent.proceduralbouquets.data.BouquetEntry;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** Shared placement rules and flower transform data used by both logical and visual placement. */
public final class BouquetPlacement {
    private BouquetPlacement() {
    }

    public enum State {
        VALID(true),
        REPLACEMENT(true),
        BLOCKED_OCCUPIED(false),
        BLOCKED_FULL(false);

        private final boolean placeable;

        State(boolean placeable) {
            this.placeable = placeable;
        }

        public boolean placeable() {
            return placeable;
        }
    }

    public static State state(boolean occupied, boolean full, boolean sneaking, boolean replacementAllowed) {
        if (occupied) {
            return sneaking && replacementAllowed ? State.REPLACEMENT : State.BLOCKED_OCCUPIED;
        }
        return full ? State.BLOCKED_FULL : State.VALID;
    }

    public static BouquetEntry entry(BlockPos pos, UUID playerId, ResourceLocation itemId, int x, int z) {
        long seed = pos.asLong()
            ^ playerId.getLeastSignificantBits()
            ^ itemId.hashCode()
            ^ (x * 31L + z);
        int rotation = (int) Math.floorMod(seed, 4L);
        float scale = 0.85F + (Math.floorMod(seed >>> 2, 21L) / 100.0F);
        return new BouquetEntry(itemId, x, z, rotation, scale, 0);
    }

    public static int gridCoordinate(double localCoordinate) {
        return Mth.clamp((int) Math.floor(localCoordinate * 16.0D), 0, 15);
    }
}
