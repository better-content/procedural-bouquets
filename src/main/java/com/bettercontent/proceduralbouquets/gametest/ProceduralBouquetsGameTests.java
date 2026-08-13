package com.bettercontent.proceduralbouquets.gametest;

import com.bettercontent.proceduralbouquets.ProceduralBouquets;
import com.bettercontent.proceduralbouquets.blockentity.BouquetGridBlockEntity;
import com.bettercontent.proceduralbouquets.blockentity.PottedBouquetBlockEntity;
import com.bettercontent.proceduralbouquets.data.BouquetData;
import com.bettercontent.proceduralbouquets.data.BouquetEntry;
import com.bettercontent.proceduralbouquets.registry.ModBlocks;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ProceduralBouquets.MOD_ID)
@PrefixGameTestTemplate(false)
@Mod.EventBusSubscriber(modid = ProceduralBouquets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ProceduralBouquetsGameTests {
    public ProceduralBouquetsGameTests() {
    }

    @SubscribeEvent
    public static void register(RegisterGameTestsEvent event) {
        event.register(ProceduralBouquetsGameTests.class);
    }

    @GameTest(templateNamespace = "minecraft", template = "empty", batch = "procedural_bouquets_blocks", timeoutTicks = 40)
    public static void bouquetGridRejectsDuplicateCellsAndExportsBouquetStack(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.BOUQUET_GRID.get());
        BouquetGridBlockEntity grid = gridAt(helper, pos);

        BouquetEntry poppy = entry("minecraft:poppy", 2, 3);
        BouquetEntry dandelion = entry("minecraft:dandelion", 2, 3);

        helper.assertTrue(grid.addEntry(poppy), "first flower should be accepted");
        helper.assertTrue(!grid.addEntry(dandelion), "duplicate grid cell should be rejected");
        helper.assertTrue(grid.getEntriesView().size() == 1, "grid should keep only one entry for the occupied cell");

        ItemStack bouquet = grid.createBouquetStack();
        List<BouquetEntry> exported = BouquetData.read(bouquet);
        helper.assertTrue(exported.equals(List.of(poppy)), "exported bouquet stack should preserve grid entries");
        helper.succeed();
    }

    @GameTest(templateNamespace = "minecraft", template = "empty", batch = "procedural_bouquets_blocks", timeoutTicks = 40)
    public static void pottedBouquetPersistsEntriesThroughBlockEntityTag(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.POTTED_BOUQUET.get());
        PottedBouquetBlockEntity original = pottedAt(helper, pos);
        List<BouquetEntry> entries = List.of(
            entry("minecraft:poppy", 1, 1),
            entry("minecraft:cornflower", 4, 7)
        );

        original.setEntries(entries);
        PottedBouquetBlockEntity loaded = new PottedBouquetBlockEntity(pos, original.getBlockState());
        loaded.load(original.saveWithoutMetadata());

        helper.assertTrue(loaded.getEntriesView().equals(entries), "potted bouquet should round-trip saved entries");
        helper.succeed();
    }

    private static BouquetGridBlockEntity gridAt(GameTestHelper helper, BlockPos pos) {
        BlockEntity blockEntity = helper.getBlockEntity(pos);
        if (blockEntity instanceof BouquetGridBlockEntity grid) {
            return grid;
        }
        throw new IllegalStateException("Expected bouquet grid block entity at " + pos);
    }

    private static PottedBouquetBlockEntity pottedAt(GameTestHelper helper, BlockPos pos) {
        BlockEntity blockEntity = helper.getBlockEntity(pos);
        if (blockEntity instanceof PottedBouquetBlockEntity potted) {
            return potted;
        }
        throw new IllegalStateException("Expected potted bouquet block entity at " + pos);
    }

    private static BouquetEntry entry(String itemId, int x, int z) {
        return new BouquetEntry(new ResourceLocation(itemId), x, z, 0, 1.0F, 0);
    }
}
