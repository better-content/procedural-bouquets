package com.bettercontent.proceduralbouquets.block;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.bettercontent.proceduralbouquets.data.BouquetEntry;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

class BouquetPlacementTest {
    @Test
    void stateDistinguishesPlacementReplacementAndBothBlockedReasons() {
        assertEquals(BouquetPlacement.State.VALID, BouquetPlacement.state(false, false, false, true));
        assertEquals(BouquetPlacement.State.BLOCKED_FULL, BouquetPlacement.state(false, true, true, true));
        assertEquals(BouquetPlacement.State.BLOCKED_OCCUPIED, BouquetPlacement.state(true, false, false, true));
        assertEquals(BouquetPlacement.State.BLOCKED_OCCUPIED, BouquetPlacement.state(true, true, true, false));
        assertEquals(BouquetPlacement.State.REPLACEMENT, BouquetPlacement.state(true, true, true, true));
    }

    @Test
    void entryIsStableForTheSamePlayerFlowerCellAndBlock() {
        BlockPos pos = new BlockPos(12, 70, -4);
        UUID player = UUID.fromString("817723b2-2f88-4bf5-9355-d27f6078df4b");
        ResourceLocation poppy = ResourceLocation.withDefaultNamespace("poppy");

        BouquetEntry first = BouquetPlacement.entry(pos, player, poppy, 5, 11);
        BouquetEntry later = BouquetPlacement.entry(pos, player, poppy, 5, 11);

        assertEquals(first, later);
        assertEquals(5, first.x());
        assertEquals(11, first.z());
    }

    @Test
    void cellParticipatesInTheStableTransformSeed() {
        BlockPos pos = new BlockPos(12, 70, -4);
        UUID player = UUID.fromString("817723b2-2f88-4bf5-9355-d27f6078df4b");
        ResourceLocation poppy = ResourceLocation.withDefaultNamespace("poppy");

        assertNotEquals(
            BouquetPlacement.entry(pos, player, poppy, 5, 11),
            BouquetPlacement.entry(pos, player, poppy, 6, 11)
        );
    }

    @Test
    void gridCoordinateClampsEveryEdge() {
        assertEquals(0, BouquetPlacement.gridCoordinate(-0.1D));
        assertEquals(0, BouquetPlacement.gridCoordinate(0.0D));
        assertEquals(8, BouquetPlacement.gridCoordinate(0.5D));
        assertEquals(15, BouquetPlacement.gridCoordinate(0.999D));
        assertEquals(15, BouquetPlacement.gridCoordinate(1.1D));
    }
}
