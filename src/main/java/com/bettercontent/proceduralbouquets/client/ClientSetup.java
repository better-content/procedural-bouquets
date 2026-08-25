package com.bettercontent.proceduralbouquets.client;

import com.bettercontent.proceduralbouquets.ProceduralBouquets;
import com.bettercontent.proceduralbouquets.registry.ModBlockEntities;
import com.bettercontent.proceduralbouquets.registry.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ProceduralBouquets.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientSetup {
    public static final ResourceLocation BOUQUET_WRAP_MODEL =
        ResourceLocation.fromNamespaceAndPath(ProceduralBouquets.MOD_ID, "special/bouquet_wrap");

    private ClientSetup() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(ModMenus.BOUQUET_GRID.get(), BouquetGridScreen::new));
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.BOUQUET_GRID.get(), BouquetGridRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.POTTED_BOUQUET.get(), PottedBouquetRenderer::new);

        if (Boolean.getBoolean("procedural_bouquets.visualHarness")) {
            try {
                Class.forName("com.bettercontent.proceduralbouquets.harness.ProceduralBouquetsVisualHarness")
                    .getMethod("install")
                    .invoke(null);
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("Unable to install Procedural Bouquets visual harness", exception);
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(BOUQUET_WRAP_MODEL);
    }
}
