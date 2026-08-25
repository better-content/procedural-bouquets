package com.bettercontent.proceduralbouquets.config;

import com.bettercontent.proceduralbouquets.data.BouquetData;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public final class ModCommonConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue MAX_FLOWERS_PER_BOUQUET = BUILDER
        .comment("Maximum flowers stored in a bouquet")
        .defineInRange("maxFlowersPerBouquet", BouquetData.MAX_ENTRIES, 1, BouquetData.MAX_ENTRIES);

    public static final ForgeConfigSpec.BooleanValue CONSUME_FLOWERS_IN_CREATIVE = BUILDER
        .comment("If true, flowers are consumed in creative mode")
        .define("consumeFlowersInCreative", false);

    public static final ForgeConfigSpec.BooleanValue ALLOW_REPLACEMENT_WHEN_SNEAKING = BUILDER
        .comment("If true, sneaking with flower can replace an occupied cell")
        .define("allowReplacementWhenSneaking", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private ModCommonConfig() {
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
    }
}
