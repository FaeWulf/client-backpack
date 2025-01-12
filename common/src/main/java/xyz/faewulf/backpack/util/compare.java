package xyz.faewulf.backpack.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SuspiciousEffectHolder;
import net.minecraft.world.phys.Vec3;
import xyz.faewulf.backpack.Constants;

import java.util.List;

public class compare {
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


    public static boolean isEntity2BehindEntity1(LivingEntity entity1, LivingEntity entity2) {
        // Villager's facing direction vector
        Vec3 entity1ViewVector = entity1.getViewVector(1.0F);

        // Vector from villager to player
        Vec3 toEntity2 = entity2.position().subtract(entity1.position()).normalize();

        // Calculate the angle between the two vectors
        double dotProduct = entity1ViewVector.dot(toEntity2);
        double angle = Math.acos(dotProduct);

        // If angle is close to π (180 degrees), the player is behind the villager
        return angle >= Math.PI / 2 && angle <= Math.PI;
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
