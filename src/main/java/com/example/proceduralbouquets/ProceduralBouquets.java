package com.example.proceduralbouquets;

import com.example.proceduralbouquets.config.ModClientConfig;
import com.example.proceduralbouquets.config.ModCommonConfig;
import com.example.proceduralbouquets.registry.ModBlockEntities;
import com.example.proceduralbouquets.registry.ModBlocks;
import com.example.proceduralbouquets.registry.ModCreativeTabs;
import com.example.proceduralbouquets.registry.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ProceduralBouquets.MOD_ID)
public final class ProceduralBouquets {
    public static final String MOD_ID = "procedural_bouquets";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ProceduralBouquets() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modBus);
        modBus.addListener(ModCreativeTabs::onBuildCreativeTabContents);

        ModCommonConfig.register();
        ModClientConfig.register();
    }
}
