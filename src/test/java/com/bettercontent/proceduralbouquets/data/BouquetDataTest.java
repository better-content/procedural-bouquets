package com.bettercontent.proceduralbouquets.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

class BouquetDataTest {

    @Test
    void bouquetEntryClampsValues() {
        BouquetEntry entry = new BouquetEntry(id("minecraft:poppy"), -3, 99, 21, 5.0F, 0);

        assertEquals(0, entry.x());
        assertEquals(15, entry.z());
        assertEquals(1, entry.rotation());
        assertEquals(1.15F, entry.scale());
    }

    @Test
    void toTagAppliesClampRules() {
        List<BouquetEntry> entries = List.of(
            new BouquetEntry(id("minecraft:poppy"), -2, 40, 19, 5.0F, 0)
        );

        CompoundTag tag = BouquetData.toTag(entries);
        ListTag out = tag.getList(BouquetData.TAG_ENTRIES, Tag.TAG_COMPOUND);
        CompoundTag first = out.getCompound(0);

        assertEquals(0, first.getInt("x"));
        assertEquals(15, first.getInt("z"));
        assertEquals(3, first.getInt("rot"));
        assertEquals(1.15F, first.getFloat("scale"));
    }

    @Test
    void toTagClampsToMaxEntries() {
        List<BouquetEntry> entries = new ArrayList<>();
        for (int i = 0; i < 120; i++) {
            entries.add(new BouquetEntry(id("minecraft:poppy"), i % 16, (i / 16) % 16, i, 1.0F, 0));
        }

        CompoundTag tag = BouquetData.toTag(entries);
        ListTag out = tag.getList(BouquetData.TAG_ENTRIES, Tag.TAG_COMPOUND);

        assertEquals(BouquetData.MAX_ENTRIES, out.size());
    }

    @Test
    void stableHashIsDeterministicForSameEntries() {
        List<BouquetEntry> entries = List.of(
            new BouquetEntry(id("minecraft:poppy"), 2, 2, 1, 0.95F, 0),
            new BouquetEntry(id("minecraft:cornflower"), 3, 3, 2, 1.0F, 0)
        );

        long a = BouquetData.stableHash(entries);
        long b = BouquetData.stableHash(List.copyOf(entries));
        assertEquals(a, b);
    }

    @Test
    void bouquetEntryRejectsNullId() {
        assertThrows(IllegalArgumentException.class, () -> new BouquetEntry(null, 0, 0, 0, 1.0F, 0));
    }

    @Test
    void fromTagHandlesMissingAndInvalidListPayload() {
        assertTrue(BouquetData.fromTag(null).isEmpty());
        assertTrue(BouquetData.fromTag(new CompoundTag()).isEmpty());

        CompoundTag root = new CompoundTag();
        ListTag entries = new ListTag();
        entries.add(StringTag.valueOf("wrong-type"));
        root.put(BouquetData.TAG_ENTRIES, entries);

        assertTrue(BouquetData.fromTag(root).isEmpty());
    }

    @Test
    void fromTagSkipsMalformedResourceIdsBeforeRegistryLookup() {
        CompoundTag root = new CompoundTag();
        ListTag entries = new ListTag();
        entries.add(entryTag("not a valid id", 4, 5, 0.9F));
        root.put(BouquetData.TAG_ENTRIES, entries);

        assertTrue(BouquetData.fromTag(root).isEmpty());
    }

    @Test
    void toTagPreservesYOffsetForRendererVariation() {
        CompoundTag tag = BouquetData.toTag(List.of(
            new BouquetEntry(id("minecraft:poppy"), 2, 3, 0, 1.0F, -2)
        ));

        assertEquals(-2, tag.getList(BouquetData.TAG_ENTRIES, Tag.TAG_COMPOUND).getCompound(0).getInt("y"));
    }

    @Test
    void stableHashChangesWhenEntriesChange() {
        List<BouquetEntry> first = List.of(
            new BouquetEntry(id("minecraft:poppy"), 2, 2, 1, 0.95F, 0)
        );
        List<BouquetEntry> second = List.of(
            new BouquetEntry(id("minecraft:poppy"), 2, 2, 1, 1.0F, 0)
        );

        assertFalse(BouquetData.stableHash(first) == BouquetData.stableHash(second));
    }

    private static ResourceLocation id(String value) {
        return ResourceLocation.parse(value);
    }

    private static CompoundTag entryTag(String itemId, int x, int z, float scale) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", itemId);
        tag.putInt("x", x);
        tag.putInt("z", z);
        tag.putInt("rot", 1);
        tag.putFloat("scale", scale);
        tag.putInt("y", 0);
        return tag;
    }
}
