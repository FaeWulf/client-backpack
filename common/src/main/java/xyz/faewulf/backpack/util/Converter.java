package xyz.faewulf.backpack.util;

import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.inter.BackpackModelRecord.DetailBackpack;
import xyz.faewulf.backpack.inter.BackpackStatus;
import xyz.faewulf.backpack.platform.Services;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Converter {

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

    // Update Backpack status based on inv content, server and client handled different
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
        backpackStatus.setHasLightSource(false);

        if (playerInv != null)
            for (int index = 0; index < playerInv.size(); index++) {
                ItemStack stack = playerInv.get(index);

                if (stack.isEmpty())
                    continue;

                // if light source
                if (Services.PLATFORM.isModLoaded("lambdynlights"))
                    if (!backpackStatus.isHasLightSource() && Services.DYNAMIC_LIGHT_HELPER.getLuminance(stack) > 0) {
                        backpackStatus.setHasLightSource(true);
                    }

                // if weapon or tool, and not holding it (main hand and offhand = 40)
                if (backpackStatus.getHoldingSlot() != index && index != 40) {
                    if (serverSide) {
                        if (Compare.isHasTag(stack.getItem(), Constants.MOD_ID + ":tool_and_weapon"))
                            tools.add(stack);
                    } else if (Compare.isHasTagClient(stack.getItem(), "tool_and_weapon"))
                        tools.add(stack);
                }

                // if pocket item (arrow for example)
                if (serverSide) {
                    if (Compare.isHasTag(stack.getItem(), Constants.MOD_ID + ":pocket_item")) {
                        pockets.add(stack);
                    }
                } else if (Compare.isHasTagClient(stack.getItem(), "pocket_item")) {
                    pockets.add(stack);
                }


                // Banner
                if (serverSide) {
                    if (Compare.isHasTag(stack.getItem(), Constants.MOD_ID + ":banner")) {
                        banner = stack;
                    }
                } else if (Compare.isHasTagClient(stack.getItem(), "banner")) {
                    banner = stack;
                }

                // if containers (shulker, bundle for example)
                if (backpackStatus.getHoldingSlot() != index && index != 40) {
                    if (serverSide) {
                        if (Compare.isHasTag(stack.getItem(), Constants.MOD_ID + ":container"))
                            containers.add(stack);
                    } else if (Compare.isHasTagClient(stack.getItem(), "container")) {
                        containers.add(stack);
                    }
                }

                // if liquid (lava, water for example)
                if (serverSide) {
                    if (Compare.isHasTag(stack.getItem(), Constants.MOD_ID + ":liquid")) {
                        liquids.add(stack);
                    }
                } else if (Compare.isHasTagClient(stack.getItem(), "liquid")) {
                    liquids.add(stack);
                }

            }

        backpackStatus.setInvChanged(false);
        backpackStatus.setToolsList(tools);
        backpackStatus.setPocketList(pockets);
        backpackStatus.setContainerList(containers);
        backpackStatus.setLiquidList(liquids);
        backpackStatus.setBanner(banner);

        return backpackStatus;
    }

    public static Optional<DetailBackpack> tryGetBackpackPlacementData(ResourceManager resourceManager, ResourceLocation id) {
        try {
            Resource resource = resourceManager.getResource(id).orElse(null);
            if (resource == null) {
                Constants.LOG.warn("Model data not found: " + id);
                return Optional.empty();
            }

            try (InputStreamReader reader = new InputStreamReader(resource.open(), StandardCharsets.UTF_8)) {
                DetailBackpack modelData = Constants.GSON.fromJson(reader, DetailBackpack.class);
                return Optional.of(modelData);
            }
        } catch (IOException | JsonSyntaxException e) {
            Constants.LOG.error("Failed to load backpack model data from {}", id, e);
        }
        return Optional.empty();
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
}
