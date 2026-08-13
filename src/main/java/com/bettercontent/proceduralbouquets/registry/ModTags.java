package com.bettercontent.proceduralbouquets.registry;

import com.bettercontent.proceduralbouquets.ProceduralBouquets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModTags {
    private ModTags() {
    }

    public static final class Items {
        public static final TagKey<Item> BOUQUET_FLOWERS = tag("bouquet_flowers");

        private Items() {
        }

        private static TagKey<Item> tag(String path) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(ProceduralBouquets.MOD_ID, path));
        }
    }
}
