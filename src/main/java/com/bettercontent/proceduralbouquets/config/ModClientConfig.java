package com.bettercontent.proceduralbouquets.config;

import com.bettercontent.proceduralbouquets.data.BouquetData;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public final class ModClientConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue RENDER_HOVER_INDICATOR = BUILDER
        .comment("Render a hovered bouquet grid cell outline")
        .define("renderHoverIndicator", true);

    public static final ForgeConfigSpec.IntValue MAX_RENDERED_FLOWERS = BUILDER
        .comment("Client cap for rendered flowers")
        .defineInRange("maxRenderedFlowers", BouquetData.MAX_ENTRIES, 1, BouquetData.MAX_ENTRIES);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private ModClientConfig() {
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SPEC);
    }
}
