package com.bettercontent.proceduralbouquets.registry;

import com.bettercontent.proceduralbouquets.ProceduralBouquets;
import com.bettercontent.proceduralbouquets.blockentity.BouquetGridBlockEntity;
import com.bettercontent.proceduralbouquets.blockentity.PottedBouquetBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ProceduralBouquets.MOD_ID);

    public static final RegistryObject<BlockEntityType<BouquetGridBlockEntity>> BOUQUET_GRID = BLOCK_ENTITY_TYPES.register("bouquet_grid", () ->
        BlockEntityType.Builder.of(BouquetGridBlockEntity::new, ModBlocks.BOUQUET_GRID.get()).build(null)
    );

    public static final RegistryObject<BlockEntityType<PottedBouquetBlockEntity>> POTTED_BOUQUET = BLOCK_ENTITY_TYPES.register("potted_bouquet", () ->
        BlockEntityType.Builder.of(PottedBouquetBlockEntity::new, ModBlocks.POTTED_BOUQUET.get()).build(null)
    );

    private ModBlockEntities() {
    }
}
