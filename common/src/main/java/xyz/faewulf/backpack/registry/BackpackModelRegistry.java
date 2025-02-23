package xyz.faewulf.backpack.registry;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.inter.BackpackModelRecord.DetailBackpack;
import xyz.faewulf.backpack.platform.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BackpackModelRegistry {
    // backpack id: -> (variant's id, variant's ResourceLocation)
    private static final Map<String, Map<String, ResourceLocation>> BACKPACK_LIST = new HashMap<>();
    private static final Map<String, Map<String, DetailBackpack>> BACKPACK_DETAIL_LIST = new HashMap<>();


    public static List<String> getModelList() {
        // Sort and bring default to the head

        List<String> list = new ArrayList<>(BACKPACK_LIST.keySet().stream().sorted().toList());

        list.remove("strap");

        if (list.contains("default")) {
            list.remove("default");
            list.addFirst("default");
        }

        // Make immutable
        return list.stream().toList();
    }

    public static List<String> getVariantList(String modelId) {
        if (BACKPACK_LIST.containsKey(modelId)) {
            // Sort and bring default to the head

            List<String> list = new ArrayList<>(BACKPACK_LIST.get(modelId).keySet().stream().sorted().toList());

            if (list.contains("default")) {
                list.remove("default");
                list.addFirst("default");
            }

            return list.stream().toList();
        }
        return new ArrayList<>();
    }

    public static void addBackpack(String id, String variant, ResourceLocation location) {
        BACKPACK_LIST.computeIfAbsent(id, k -> new HashMap<>()).put(variant, location);
    }

    public static void addBackpackDetail(String id, String variant, DetailBackpack detailBackpack) {
        BACKPACK_DETAIL_LIST.computeIfAbsent(id, k -> new HashMap<>()).put(variant, detailBackpack);
    }

    public static void debugBackpackList() {
        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            BACKPACK_LIST.forEach((s, stringResourceLocationMap) -> {
                        Constants.LOG.info("==== Backpack list ===== {}", s);

                        stringResourceLocationMap.forEach((s1, resourceLocation) -> {
                            Constants.LOG.info("{} : {}", s1, resourceLocation);
                        });
                    }
            );

            BACKPACK_DETAIL_LIST.forEach((s, stringResourceLocationMap) -> {
                        Constants.LOG.info("==== Detail list ===== {}", s);

                        stringResourceLocationMap.forEach((s1, detailBackpack) -> {
                            Constants.LOG.info("{} : {}", s1, detailBackpack);
                        });
                    }
            );
        }

    }

    @Nullable
    public static ResourceLocation getBackpackModel(String id, String variant) {

        if (!isValidBackpack(id, variant))
            return null;

        try {
            return BACKPACK_LIST.get(id).get(variant);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Nullable
    public static DetailBackpack getBackpackDetail(String id, String variant) {

        if (!isValidBackpackDetail(id, variant))
            return null;

        try {
            return BACKPACK_DETAIL_LIST.get(id).get(variant);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static boolean isValidBackpack(String id) {
        return BACKPACK_LIST.containsKey(id);
    }

    public static boolean isValidBackpack(String id, String variant) {

        if (!BACKPACK_LIST.containsKey(id))
            return false;

        try {
            return BACKPACK_LIST.get(id).containsKey(variant);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static boolean isValidBackpackDetail(String id, String variant) {

        if (!BACKPACK_DETAIL_LIST.containsKey(id))
            return false;

        try {
            return BACKPACK_DETAIL_LIST.get(id).containsKey(variant);
        } catch (NullPointerException e) {
            return false;
        }
    }
}
