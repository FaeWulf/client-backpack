package xyz.faewulf.backpack.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.util.compare;

import java.io.InputStreamReader;
import java.util.*;


public class ItemTagRegistry {

    private static final String BASE_PATH = "client_backpack/item"; // Directory to scan
    private static final Map<String, List<Item>> TYPE_TO_ITEMS_MAP = new HashMap<>();

    public static void loadAllBackpackItems() {
        //reset
        TYPE_TO_ITEMS_MAP.clear();

        try {
            // Get all JSON files in the directory `assets/<namespace>/client_backpack/item/`
            //ResourceLocation baseLocation = ResourceLocation.tryBuild(Constants.MOD_ID, BASE_PATH);
            Minecraft.getInstance().getResourceManager().listResources(BASE_PATH, path -> path.toString().endsWith(".json")).forEach((location, resource) -> {
                try {
                    // Read and parse the JSON file
                    InputStreamReader reader = new InputStreamReader(resource.open());
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    reader.close();

                    // Parse the "type" and "items" fields
                    String type = json.get("type").getAsString();
                    JsonArray itemsArray = json.getAsJsonArray("items");

                    // Process items
                    List<Item> itemList = parseItemsArray(itemsArray);

                    // Add to the map
                    TYPE_TO_ITEMS_MAP.putIfAbsent(type, new ArrayList<>());
                    TYPE_TO_ITEMS_MAP.get(type).addAll(itemList);

                } catch (Exception e) {
                    Constants.LOG.error("Opps something went wrong!");
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            Constants.LOG.error("Opps something went wrong!");
            e.printStackTrace();
        }
    }

    private static List<Item> parseItemsArray(JsonArray itemsArray) {
        List<Item> itemList = new ArrayList<>();

        for (JsonElement element : itemsArray) {
            String itemString = element.getAsString();

            // Check if it's a tag (starts with "#")
            if (itemString.startsWith("#")) {
                String tagId = itemString.substring(1); // Remove the `#` prefix

                BuiltInRegistries.ITEM.stream() // forEach Item and check tag :/ at least it works
                        .forEach(item -> {
                            if (compare.isHasTag(item, tagId)) {
                                itemList.add(item);
                            }
                        });
            } else {
                // Otherwise, it's an item ID
                ResourceLocation itemResource = ResourceLocation.tryParse(itemString);

                // Add to the list
                if (itemResource != null)
                    BuiltInRegistries.ITEM.get(itemResource).ifPresent(itemReference -> itemList.add(itemReference.value()));
            }
        }

        return itemList;
    }

    public static Map<String, List<Item>> getTypeToItemsMap() {
        return TYPE_TO_ITEMS_MAP;
    }
}

