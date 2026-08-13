package com.bettercontent.proceduralbouquets.registry;

import com.bettercontent.proceduralbouquets.ProceduralBouquets;
import com.bettercontent.proceduralbouquets.block.BouquetGridBlock;
import com.bettercontent.proceduralbouquets.block.PottedBouquetBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ProceduralBouquets.MOD_ID);

    public static final RegistryObject<Block> BOUQUET_GRID = BLOCKS.register("bouquet_grid", () ->
        new BouquetGridBlock(BlockBehaviour.Properties.of().strength(0.8F).sound(SoundType.WOOD).noOcclusion())
    );

    public static final RegistryObject<Block> POTTED_BOUQUET = BLOCKS.register("potted_bouquet", () ->
        new PottedBouquetBlock(BlockBehaviour.Properties.of().strength(0.6F).sound(SoundType.DECORATED_POT).noOcclusion())
    );

    private ModBlocks() {
    }
}
