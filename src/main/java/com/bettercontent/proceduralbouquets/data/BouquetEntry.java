package com.bettercontent.proceduralbouquets.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public record BouquetEntry(
    ResourceLocation itemId,
    int x,
    int z,
    int rotation,
    float scale,
    int yOffset
) {
    public BouquetEntry {
        if (itemId == null) {
            throw new IllegalArgumentException("itemId cannot be null");
        }
        x = Mth.clamp(x, 0, 15);
        z = Mth.clamp(z, 0, 15);
        rotation = rotation & 3;
        scale = Mth.clamp(scale, 0.65F, 1.15F);
    }
}
