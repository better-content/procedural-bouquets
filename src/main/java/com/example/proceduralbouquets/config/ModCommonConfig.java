package com.example.proceduralbouquets.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public final class ModCommonConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue MAX_FLOWERS_PER_BOUQUET = BUILDER
        .comment("Maximum flowers stored/rendered in a bouquet")
        .defineInRange("maxFlowersPerBouquet", 64, 1, 256);

    public static final ForgeConfigSpec.BooleanValue CONSUME_FLOWERS_IN_CREATIVE = BUILDER
        .comment("If true, flowers are consumed in creative mode")
        .define("consumeFlowersInCreative", false);

    public static final ForgeConfigSpec.BooleanValue ALLOW_REPLACEMENT_WHEN_SNEAKING = BUILDER
        .comment("If true, sneaking with flower can replace an occupied cell")
        .define("allowReplacementWhenSneaking", true);

    public static final ForgeConfigSpec.BooleanValue DROP_INDIVIDUAL_FLOWERS_IF_BOUQUET_INVALID = BUILDER
        .comment("If true, fallback to individual drops when bouquet creation fails")
        .define("dropIndividualFlowersIfBouquetInvalid", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private ModCommonConfig() {
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
    }
}
