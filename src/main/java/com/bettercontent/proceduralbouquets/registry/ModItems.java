package com.bettercontent.proceduralbouquets.registry;

import com.bettercontent.proceduralbouquets.ProceduralBouquets;
import com.bettercontent.proceduralbouquets.item.BouquetItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ProceduralBouquets.MOD_ID);

    public static final RegistryObject<Item> BOUQUET_GRID_ITEM = ITEMS.register("bouquet_grid", () ->
        new BlockItem(ModBlocks.BOUQUET_GRID.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> BOUQUET = ITEMS.register("bouquet", () ->
        new BouquetItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> POTTED_BOUQUET_ITEM = ITEMS.register("potted_bouquet", () ->
        new BlockItem(ModBlocks.POTTED_BOUQUET.get(), new Item.Properties())
    );

    private ModItems() {
    }
}
