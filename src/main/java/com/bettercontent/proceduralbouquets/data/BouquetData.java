package com.bettercontent.proceduralbouquets.data;

import com.bettercontent.proceduralbouquets.config.ModCommonConfig;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class BouquetData {
    public static final String TAG_ROOT = "Bouquet";
    public static final String TAG_ENTRIES = "Entries";
    public static final int GRID_SIZE = 16;
    public static final int MAX_ENTRIES = 64;

    private BouquetData() {
    }

    public static List<BouquetEntry> read(ItemStack stack) {
        if (!stack.hasTag()) {
            return List.of();
        }
        CompoundTag root = stack.getTag();
        if (root == null || !root.contains(TAG_ROOT, Tag.TAG_COMPOUND)) {
            return List.of();
        }
        return fromTag(root.getCompound(TAG_ROOT));
    }

    public static void write(ItemStack stack, List<BouquetEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            CompoundTag root = stack.getTag();
            if (root != null) {
                root.remove(TAG_ROOT);
                if (root.isEmpty()) {
                    stack.setTag(null);
                }
            }
            return;
        }

        CompoundTag root = stack.getOrCreateTag();
        root.put(TAG_ROOT, toTag(entries));
    }

    public static CompoundTag toTag(List<BouquetEntry> entries) {
        CompoundTag root = new CompoundTag();
        ListTag listTag = new ListTag();

        int max = Math.min(configuredMaxEntries(), entries.size());
        for (int i = 0; i < max; i++) {
            BouquetEntry entry = entries.get(i);
            CompoundTag tag = new CompoundTag();
            tag.putString("id", entry.itemId().toString());
            tag.putInt("x", Mth.clamp(entry.x(), 0, 15));
            tag.putInt("z", Mth.clamp(entry.z(), 0, 15));
            tag.putInt("rot", entry.rotation() & 3);
            tag.putFloat("scale", Mth.clamp(entry.scale(), 0.65F, 1.15F));
            tag.putInt("y", entry.yOffset());
            listTag.add(tag);
        }

        root.put(TAG_ENTRIES, listTag);
        return root;
    }

    public static List<BouquetEntry> fromTag(CompoundTag tag) {
        if (tag == null || !tag.contains(TAG_ENTRIES, Tag.TAG_LIST)) {
            return List.of();
        }

        ListTag listTag = tag.getList(TAG_ENTRIES, Tag.TAG_COMPOUND);
        int max = Math.min(configuredMaxEntries(), listTag.size());

        List<BouquetEntry> entries = new ArrayList<>(max);
        Set<Integer> occupied = new LinkedHashSet<>();

        for (int i = 0; i < max; i++) {
            if (!(listTag.get(i) instanceof CompoundTag entryTag)) {
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(entryTag.getString("id"));
            if (id == null) {
                continue;
            }
            Item item = BuiltInRegistries.ITEM.getOptional(id).orElse(null);
            if (item == null || item == Items.AIR) {
                continue;
            }

            int x = Mth.clamp(entryTag.getInt("x"), 0, 15);
            int z = Mth.clamp(entryTag.getInt("z"), 0, 15);
            int key = (x << 4) | z;
            if (occupied.contains(key)) {
                continue;
            }

            int rot = entryTag.getInt("rot") & 3;
            float scale = Mth.clamp(entryTag.contains("scale", Tag.TAG_FLOAT) ? entryTag.getFloat("scale") : 1.0F, 0.65F, 1.15F);
            int y = entryTag.getInt("y");
            entries.add(new BouquetEntry(id, x, z, rot, scale, y));
            occupied.add(key);
        }

        return entries;
    }

    public static long stableHash(List<BouquetEntry> entries) {
        long hash = 1125899906842597L;
        int max = Math.min(entries.size(), configuredMaxEntries());

        for (int i = 0; i < max; i++) {
            BouquetEntry entry = entries.get(i);
            hash = (hash * 31L) + entry.itemId().hashCode();
            hash = (hash * 31L) + entry.x();
            hash = (hash * 31L) + entry.z();
            hash = (hash * 31L) + entry.rotation();
            hash = (hash * 31L) + Float.floatToIntBits(entry.scale());
            hash = (hash * 31L) + entry.yOffset();
        }

        return hash;
    }

    private static int configuredMaxEntries() {
        int configured = MAX_ENTRIES;
        try {
            configured = ModCommonConfig.MAX_FLOWERS_PER_BOUQUET.get();
        } catch (Throwable ignored) {
            // Unit tests may run outside a fully initialized Forge config context.
        }
        return Mth.clamp(configured, 1, MAX_ENTRIES);
    }
}
