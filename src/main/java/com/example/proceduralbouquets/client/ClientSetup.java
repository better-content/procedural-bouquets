package com.example.proceduralbouquets.client;

import com.example.proceduralbouquets.ProceduralBouquets;
import com.example.proceduralbouquets.registry.ModBlockEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ProceduralBouquets.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientSetup {
    private ClientSetup() {
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.BOUQUET_GRID.get(), BouquetGridRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.POTTED_BOUQUET.get(), PottedBouquetRenderer::new);
    }
}
