package com.bettercontent.proceduralbouquets.gametest;

import com.bettercontent.proceduralbouquets.ProceduralBouquets;
import com.bettercontent.proceduralbouquets.blockentity.BouquetGridBlockEntity;
import com.bettercontent.proceduralbouquets.blockentity.PottedBouquetBlockEntity;
import com.bettercontent.proceduralbouquets.data.BouquetData;
import com.bettercontent.proceduralbouquets.data.BouquetEntry;
import com.bettercontent.proceduralbouquets.item.PottedBouquetItem;
import com.bettercontent.proceduralbouquets.menu.BouquetGridMenu;
import com.bettercontent.proceduralbouquets.registry.ModBlocks;
import com.bettercontent.proceduralbouquets.registry.ModItems;
import com.bettercontent.proceduralbouquets.recipe.PottedBouquetRecipe;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
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

    @GameTest(templateNamespace = "minecraft", template = "empty", batch = "procedural_bouquets_lifecycle", timeoutTicks = 40)
    public static void bouquetOnlyPotsAnExistingFlowerPot(GameTestHelper helper) {
        BlockPos potPos = new BlockPos(1, 1, 1);
        BlockPos stonePos = new BlockPos(2, 1, 1);
        helper.setBlock(potPos, Blocks.FLOWER_POT);
        helper.setBlock(stonePos, Blocks.STONE);

        FakePlayer player = FakePlayerFactory.getMinecraft(helper.getLevel());
        List<BouquetEntry> entries = sampleEntries();
        ItemStack bouquet = bouquet(entries);
        player.setItemInHand(InteractionHand.MAIN_HAND, bouquet);

        InteractionResult potted = ModItems.BOUQUET.get().useOn(context(helper, player, potPos, bouquet));
        helper.assertTrue(potted.consumesAction(), "using a bouquet on an empty flower pot should succeed");
        helper.assertBlockPresent(ModBlocks.POTTED_BOUQUET.get(), potPos);
        helper.assertTrue(pottedAt(helper, potPos).getEntriesView().equals(entries), "direct potting should preserve bouquet entries");
        helper.assertTrue(bouquet.isEmpty(), "direct potting should consume the survival bouquet");

        ItemStack rejected = bouquet(entries);
        player.setItemInHand(InteractionHand.MAIN_HAND, rejected);
        InteractionResult unrelated = ModItems.BOUQUET.get().useOn(context(helper, player, stonePos, rejected));
        helper.assertTrue(unrelated == InteractionResult.PASS, "using a bouquet on an unrelated block should pass through");
        helper.assertBlockPresent(Blocks.STONE, stonePos);
        helper.assertTrue(rejected.getCount() == 1, "rejected placement must not consume the bouquet");
        helper.succeed();
    }

    @GameTest(templateNamespace = "minecraft", template = "empty", batch = "procedural_bouquets_lifecycle", timeoutTicks = 40)
    public static void pottedRecipePreservesBouquetDataOnItemAndPlacement(GameTestHelper helper) {
        FakePlayer player = FakePlayerFactory.getMinecraft(helper.getLevel());
        List<BouquetEntry> entries = sampleEntries();
        TransientCraftingContainer crafting = new TransientCraftingContainer(player.inventoryMenu, 2, 1);
        crafting.setItem(0, bouquet(entries));
        crafting.setItem(1, new ItemStack(Items.FLOWER_POT));

        Recipe<?> loaded = helper.getLevel().getRecipeManager()
            .byKey(ResourceLocation.fromNamespaceAndPath(ProceduralBouquets.MOD_ID, "potted_bouquet"))
            .orElseThrow(() -> new IllegalStateException("Potted bouquet recipe did not load"));
        helper.assertTrue(loaded instanceof PottedBouquetRecipe, "recipe JSON should resolve to the custom NBT-preserving serializer");
        PottedBouquetRecipe recipe = (PottedBouquetRecipe) loaded;
        helper.assertTrue(recipe.matches(crafting, helper.getLevel()), "one populated bouquet plus one flower pot should match");

        ItemStack result = recipe.assemble(crafting, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(ModItems.POTTED_BOUQUET_ITEM.get()), "recipe should output the placeable potted bouquet item");
        helper.assertTrue(PottedBouquetItem.readEntries(result).equals(entries), "recipe output should preserve every bouquet entry");

        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.POTTED_BOUQUET.get());
        boolean applied = BlockItem.updateCustomBlockEntityTag(helper.getLevel(), player, helper.absolutePos(pos), result);
        helper.assertTrue(applied, "potted item should apply its block entity data when placed");
        helper.assertTrue(pottedAt(helper, pos).getEntriesView().equals(entries), "placed potted item should retain the crafted arrangement");
        helper.succeed();
    }

    @GameTest(templateNamespace = "minecraft", template = "empty", batch = "procedural_bouquets_lifecycle", timeoutTicks = 40)
    public static void filledPotDropsExactlyItsPotAndBouquet(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.POTTED_BOUQUET.get());
        PottedBouquetBlockEntity potted = pottedAt(helper, pos);
        List<BouquetEntry> entries = sampleEntries();
        potted.setEntries(entries);

        LootParams.Builder loot = new LootParams.Builder(helper.getLevel())
            .withOptionalParameter(LootContextParams.BLOCK_ENTITY, potted);
        List<ItemStack> drops = ModBlocks.POTTED_BOUQUET.get().getDrops(potted.getBlockState(), loot);

        helper.assertTrue(drops.size() == 2, "filled pot should have exactly two drops");
        helper.assertTrue(drops.stream().filter(stack -> stack.is(Items.FLOWER_POT)).mapToInt(ItemStack::getCount).sum() == 1,
            "filled pot should return exactly one flower pot");
        ItemStack bouquetDrop = drops.stream().filter(stack -> stack.is(ModItems.BOUQUET.get())).findFirst()
            .orElseThrow(() -> new IllegalStateException("Filled pot did not return its bouquet"));
        helper.assertTrue(BouquetData.read(bouquetDrop).equals(entries), "broken pot should preserve the bouquet arrangement");
        helper.succeed();
    }

    @GameTest(templateNamespace = "minecraft", template = "empty", batch = "procedural_bouquets_editor", timeoutTicks = 40)
    public static void gridInventoryMapsEveryCellAndEnforcesFlowerCapacity(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.BOUQUET_GRID.get());
        BouquetGridBlockEntity grid = gridAt(helper, pos);

        int southeast = BouquetGridMenu.slotIndex(15, 15);
        grid.setItem(southeast, new ItemStack(Items.POPPY));
        helper.assertTrue(grid.getAt(15, 15).isPresent(), "slot 255 should map to the southeast grid cell");
        helper.assertTrue(grid.getItem(southeast).is(Items.POPPY), "mapped inventory slot should expose its flower");

        grid.setItem(BouquetGridMenu.slotIndex(0, 0), new ItemStack(Items.STONE));
        helper.assertTrue(grid.getAt(0, 0).isEmpty(), "non-flower items must be rejected");

        for (int slot = 0; slot < 63; slot++) {
            grid.setItem(slot, new ItemStack(Items.DANDELION));
        }
        helper.assertTrue(grid.getEntriesView().size() == BouquetData.MAX_ENTRIES, "editor should accept exactly 64 flowers");
        grid.setItem(63, new ItemStack(Items.CORNFLOWER));
        helper.assertTrue(grid.getAt(15, 3).isEmpty(), "the 65th occupied cell must be rejected");

        grid.setItem(southeast, new ItemStack(Items.CORNFLOWER));
        helper.assertTrue(grid.getItem(southeast).is(Items.CORNFLOWER), "a full grid should still allow replacing an occupied cell");
        ItemStack removed = grid.removeItem(southeast, 1);
        helper.assertTrue(removed.is(Items.CORNFLOWER) && grid.getAt(15, 15).isEmpty(), "inventory removal should return exactly its flower");
        helper.succeed();
    }

    @GameTest(templateNamespace = "minecraft", template = "empty", batch = "procedural_bouquets_editor", timeoutTicks = 40)
    public static void gridMenuSupportsStandardShiftClickAndCursorMovement(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.BOUQUET_GRID.get());
        BouquetGridBlockEntity grid = gridAt(helper, pos);
        FakePlayer player = FakePlayerFactory.getMinecraft(helper.getLevel());
        BlockPos absolute = helper.absolutePos(pos);
        player.setPos(absolute.getX() + 0.5D, absolute.getY() + 0.5D, absolute.getZ() + 0.5D);
        player.getInventory().clearContent();
        player.getInventory().setItem(0, new ItemStack(Items.POPPY, 3));

        BouquetGridMenu menu = new BouquetGridMenu(1, player.getInventory(), grid);
        int playerHotbarSlot = menu.findSlot(player.getInventory(), 0)
            .orElseThrow(() -> new IllegalStateException("Player hotbar slot was not added to bouquet menu"));
        ItemStack moved = menu.quickMoveStack(player, playerHotbarSlot);
        helper.assertTrue(moved.getCount() == 3, "shift-click should report the original moved stack");
        helper.assertTrue(grid.getEntriesView().size() == 3, "shift-click should distribute a flower stack into one-item cells");
        helper.assertTrue(player.getInventory().getItem(0).isEmpty(), "shift-click should consume only the inserted player stack");

        int source = BouquetGridMenu.slotIndex(0, 0);
        int destination = BouquetGridMenu.slotIndex(5, 7);
        menu.clicked(source, 0, ClickType.PICKUP, player);
        menu.clicked(destination, 0, ClickType.PICKUP, player);
        helper.assertTrue(grid.getAt(0, 0).isEmpty(), "cursor pickup should clear the source cell");
        helper.assertTrue(grid.getAt(5, 7).isPresent(), "cursor placement should populate the exact destination cell");
        helper.assertTrue(menu.getCarried().isEmpty(), "completed cursor movement should leave no carried flower");

        BouquetEntry arranged = grid.getAt(5, 7).orElseThrow();
        menu.clicked(destination, BouquetGridMenu.ROTATE_CLOCKWISE_BUTTON, ClickType.CLONE, player);
        helper.assertTrue(
            grid.getAt(5, 7).orElseThrow().equals(arranged.rotatedByQuarterTurns(1)),
            "clockwise rotation should change only the selected flower's quarter-turn"
        );
        menu.clicked(destination, BouquetGridMenu.ROTATE_COUNTERCLOCKWISE_BUTTON, ClickType.CLONE, player);
        helper.assertTrue(
            grid.getAt(5, 7).orElseThrow().equals(arranged),
            "counterclockwise rotation should restore the selected flower without changing its arrangement data"
        );

        helper.assertTrue(menu.stillValid(player), "menu should remain valid beside its grid");
        helper.setBlock(pos, Blocks.AIR);
        helper.assertTrue(!menu.stillValid(player), "menu should become invalid when its grid is removed");
        menu.removed(player);
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
        return new BouquetEntry(ResourceLocation.parse(itemId), x, z, 0, 1.0F, 0);
    }

    private static List<BouquetEntry> sampleEntries() {
        return List.of(
            entry("minecraft:poppy", 3, 4),
            entry("minecraft:cornflower", 10, 11)
        );
    }

    private static ItemStack bouquet(List<BouquetEntry> entries) {
        ItemStack stack = new ItemStack(ModItems.BOUQUET.get());
        BouquetData.write(stack, entries);
        return stack;
    }

    private static UseOnContext context(GameTestHelper helper, FakePlayer player, BlockPos relativePos, ItemStack stack) {
        BlockPos absolutePos = helper.absolutePos(relativePos);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        return new UseOnContext(helper.getLevel(), player, InteractionHand.MAIN_HAND, stack, hit);
    }
}
