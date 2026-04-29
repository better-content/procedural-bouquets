package com.example.proceduralbouquets.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public final class ModClientConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue RENDER_HOVER_INDICATOR = BUILDER
        .comment("Render a hovered bouquet grid cell outline")
        .define("renderHoverIndicator", true);

    public static final ForgeConfigSpec.ConfigValue<String> RENDER_MODE = BUILDER
        .comment("Renderer mode")
        .define("renderMode", "ITEM_SPRITES");

    public static final ForgeConfigSpec.IntValue MAX_RENDERED_FLOWERS = BUILDER
        .comment("Client cap for rendered flowers")
        .defineInRange("maxRenderedFlowers", 64, 1, 256);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private ModClientConfig() {
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SPEC);
    }
}
