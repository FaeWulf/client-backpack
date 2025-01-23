package xyz.faewulf.backpack.util;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.inter.BackpackStatus;
import xyz.faewulf.backpack.platform.Services;

import java.util.ArrayList;
import java.util.List;

public class converter {

    // Todo: smart system to update inventory of other player's inventory, since you couldn't see other player inv
    // By remember item they holding in their hands and push into the list.
    // Also ignore smart system for Local players, this should only calculate for outsider
    public static List<ItemStack> takeInventorySnapshot(Player player) {
        List<ItemStack> snapshot = new ArrayList<>();

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack originalStack = player.getInventory().getItem(i);
            if (!originalStack.isEmpty()) {
                snapshot.add(originalStack.copy()); // Deep copy of the ItemStack
            } else {
                snapshot.add(ItemStack.EMPTY); // Preserve empty slots
            }
        }
        return snapshot;
    }

    // Todo: Handle for server and client different
    public static BackpackStatus updateBackpackStatus(BackpackStatus backpackStatus, String name, Boolean serverSide) {

        List<ItemStack> playerInv;

        if (serverSide)
            playerInv = Constants.SERVER_PLAYER_INV.get(name);
        else
            playerInv = Constants.PLAYER_INV.get(name);

        List<ItemStack> tools = new ArrayList<>();
        List<ItemStack> pockets = new ArrayList<>();
        List<ItemStack> containers = new ArrayList<>();
        List<ItemStack> liquids = new ArrayList<>();
        ItemStack banner = null;
        backpackStatus.hasLightSource = false;

        if (playerInv != null)
            for (int index = 0; index < playerInv.size(); index++) {
                ItemStack stack = playerInv.get(index);

                if (stack.isEmpty())
                    continue;

                // if light source
                if (Services.PLATFORM.isModLoaded("lambdynlights"))
                    if (!backpackStatus.hasLightSource && Services.DYNAMIC_LIGHT_HELPER.getLuminance(stack) > 0) {
                        backpackStatus.hasLightSource = true;
                    }

                // if weapon or tool, and not holding it (main hand and offhand = 40)
                if (backpackStatus.holdingSlot != index && index != 40) {
                    if (serverSide) {
                        if (compare.isHasTag(stack.getItem(), Constants.MOD_ID + ":tool_and_weapon"))
                            tools.add(stack);
                    } else if (compare.isHasTagClient(stack.getItem(), "tool_and_weapon"))
                        tools.add(stack);
                }

                // if pocket item (arrow for example)
                if (serverSide) {
                    if (compare.isHasTag(stack.getItem(), Constants.MOD_ID + ":pocket_item")) {
                        pockets.add(stack);
                    }
                } else if (compare.isHasTagClient(stack.getItem(), "pocket_item")) {
                    pockets.add(stack);
                }


                // Banner
                if (serverSide) {
                    if (compare.isHasTag(stack.getItem(), Constants.MOD_ID + ":banner")) {
                        banner = stack;
                    }
                } else if (compare.isHasTagClient(stack.getItem(), "banner")) {
                    banner = stack;
                }

                // if containers (shulker, bundle for example)
                if (backpackStatus.holdingSlot != index && index != 40) {
                    if (serverSide) {
                        if (compare.isHasTag(stack.getItem(), Constants.MOD_ID + ":container"))
                            containers.add(stack);
                    } else if (compare.isHasTagClient(stack.getItem(), "container")) {
                        containers.add(stack);
                    }
                }

                // if liquid (lava, water for example)
                if (serverSide) {
                    if (compare.isHasTag(stack.getItem(), Constants.MOD_ID + ":liquid")) {
                        liquids.add(stack);
                    }
                } else if (compare.isHasTagClient(stack.getItem(), "liquid")) {
                    liquids.add(stack);
                }

            }

        backpackStatus.invChanged = false;
        backpackStatus.toolsList = tools;
        backpackStatus.pocketList = pockets;
        backpackStatus.containerList = containers;
        backpackStatus.liquidList = liquids;
        backpackStatus.banner = banner;

        return backpackStatus;
    }

    public static Holder<Enchantment> getEnchant(Level world, ResourceKey<Enchantment> enchant) {
        HolderGetter<Enchantment> registryEntryLookup = world.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT);

        return registryEntryLookup.getOrThrow(enchant);
    }

    public static Holder<Enchantment> getEnchant(Level world, String namespace, String path) {
        return world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .get(ResourceLocation.fromNamespaceAndPath(namespace, path))
                .orElse(null);

    }

    public static String getNoteCharacter(int note) {
        return switch (note) {
            case 0 -> "F#";
            case 1 -> "G";
            case 2 -> "G#";
            case 3 -> "A";
            case 4 -> "A#";
            case 5 -> "B";
            case 6 -> "C";
            case 7 -> "C#";
            case 8 -> "D";
            case 9 -> "D#";
            case 10 -> "E";
            case 11 -> "F";
            case 12 -> "F#";
            case 13 -> "G";
            case 14 -> "G#";
            case 15 -> "A";
            case 16 -> "A#";
            case 17 -> "B";
            case 18 -> "C";
            case 19 -> "C#";
            case 20 -> "D";
            case 21 -> "D#";
            case 22 -> "E";
            case 23 -> "F";
            case 24 -> "F#";
            default -> "Unknown";
        };
    }

    public static String tick2Time(long ticks) {
        long seconds = ticks / 20;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        //ensure in range 60
        seconds %= 60;
        minutes %= 60;

        if (hours > 0)
            return String.format("%02dh%02dm%02ds", hours, minutes, seconds);
        if (minutes > 0)
            return String.format("%02dm%02ds", minutes, seconds);

        return String.format("%02ds", seconds);
    }

    public static String tick2MinecraftTime(long ticks) {
        // Wrap around 24000 ticks
        long dayTime = ticks % 24000;

        // Calculate hours, minutes, and seconds
        long hours = (dayTime / 1000 + 6) % 24;  // Minecraft time starts at 6:00 AM, so add 6 hours
        long minutes = (dayTime % 1000) * 60 / 1000;
        long seconds = ((dayTime % 1000) * 60 % 1000) * 60 / 1000;

        // Format the time as HH:MM:SS
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static double horseJumpStrength2JumpHeight(double strength) {
        double height = 0;
        double velocity = strength;
        while (velocity > 0) {
            height += velocity;
            velocity = (velocity - .08) * .98 * .98;
        }
        return height;
    }

    public static double genericSpeed2BlockPerSecond(double speed) {
        return 42.157796 * speed;
    }

    public static String UppercaseFirstLetter(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
