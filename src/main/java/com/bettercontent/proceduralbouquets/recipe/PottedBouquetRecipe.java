package com.bettercontent.proceduralbouquets.recipe;

import com.bettercontent.proceduralbouquets.data.BouquetData;
import com.bettercontent.proceduralbouquets.data.BouquetEntry;
import com.bettercontent.proceduralbouquets.item.PottedBouquetItem;
import com.bettercontent.proceduralbouquets.registry.ModItems;
import com.bettercontent.proceduralbouquets.registry.ModRecipeSerializers;
import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public final class PottedBouquetRecipe extends CustomRecipe {
    public PottedBouquetRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        return findBouquetEntries(container) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registries) {
        List<BouquetEntry> entries = findBouquetEntries(container);
        return entries == null ? ItemStack.EMPTY : PottedBouquetItem.createStack(entries);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.POTTED_BOUQUET.get();
    }

    private static List<BouquetEntry> findBouquetEntries(CraftingContainer container) {
        List<BouquetEntry> bouquetEntries = null;
        boolean foundPot = false;

        for (int index = 0; index < container.getContainerSize(); index++) {
            ItemStack stack = container.getItem(index);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.is(ModItems.BOUQUET.get()) && bouquetEntries == null) {
                bouquetEntries = BouquetData.read(stack);
                if (bouquetEntries.isEmpty()) {
                    return null;
                }
            } else if (stack.is(Items.FLOWER_POT) && !foundPot) {
                foundPot = true;
            } else {
                return null;
            }
        }

        return bouquetEntries != null && foundPot ? bouquetEntries : null;
    }
}
