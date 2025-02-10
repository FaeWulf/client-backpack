package xyz.faewulf.backpack.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.registry.ItemTagRegistry;

import java.util.List;
import java.util.Map;

public class Compare {
    public static boolean isHasTag(Block block, String tagName) {
        // Create a TagKey for the block using the tagName.


        ResourceLocation path = ResourceLocation.tryParse(tagName);

        if (path == null)
            return false;

        TagKey<Block> blockTag = TagKey.create(BuiltInRegistries.BLOCK.key(), path);

        try {
            // Check if the block is in the specified tag.
            return BuiltInRegistries.BLOCK
                    .get(BuiltInRegistries.BLOCK
                            .getResourceKey(block)
                            .orElseThrow()
                    )
                    .orElseThrow()
                    .is(blockTag);

        } catch (IllegalStateException e) {
            return false;
        }

    }

    public static boolean isHasTag(Item item, String tagName) {
        // Create a TagKey for the block using the tagName.


        ResourceLocation path = ResourceLocation.tryParse(tagName);

        if (path == null)
            return false;

        TagKey<Item> itemTag = TagKey.create(BuiltInRegistries.ITEM.key(), path);

        try {
            // Check if the block is in the specified tag.
            return BuiltInRegistries.ITEM
                    .get(BuiltInRegistries.ITEM
                            .getResourceKey(item)
                            .orElseThrow()
                    )
                    .orElseThrow()
                    .is(itemTag);
        } catch (IllegalStateException e) {
            return false;
        }

    }

    public static boolean isHasTagClient(Item item, String tagName) {
        Map<String, List<Item>> stringListMap = ItemTagRegistry.getTypeToItemsMap();

        if (!stringListMap.containsKey(tagName))
            return false;

        return stringListMap.get(tagName).contains(item);
    }

    public static boolean isBlock(String name, Block block) {
        // Get the ResourceLocation of the block from the registry
        ResourceLocation resourceLocation = BuiltInRegistries.BLOCK.getKey(block);

        // Convert to a string (e.g., "minecraft:dirt")
        String id = resourceLocation != null ? resourceLocation.toString() : "unknown:block";

        return id.equalsIgnoreCase(name);
    }

    public static boolean isItem(String name, Item item) {
        // Get the ResourceLocation of the block from the registry
        ResourceLocation resourceLocation = BuiltInRegistries.ITEM.getKey(item);

        // Convert to a string (e.g., "minecraft:dirt")
        String id = resourceLocation != null ? resourceLocation.toString() : "unknown:item";

        return id.equalsIgnoreCase(name);
    }


    public static boolean hasInventoryChanged(Player player) {
        List<ItemStack> previousSnapshot = Constants.PLAYER_INV.get(player.getName().getString());

        if (previousSnapshot == null) {
            return true;
        }

        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack currentItem = player.getInventory().getItem(slot);
            ItemStack previousItem = previousSnapshot.get(slot);

            if (!ItemStack.matches(currentItem, previousItem)) {
                return true; // Inventory has changed
            }
        }

        return false; // No changes
    }
}
