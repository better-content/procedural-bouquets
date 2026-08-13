package com.bettercontent.proceduralbouquets;

import com.bettercontent.proceduralbouquets.config.ModClientConfig;
import com.bettercontent.proceduralbouquets.config.ModCommonConfig;
import com.bettercontent.proceduralbouquets.registry.ModBlockEntities;
import com.bettercontent.proceduralbouquets.registry.ModBlocks;
import com.bettercontent.proceduralbouquets.registry.ModCreativeTabs;
import com.bettercontent.proceduralbouquets.registry.ModItems;
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
