package xyz.faewulf.backpack.registry;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.feature.backpacks.basketBackpack.BasketBackpackModel;
import xyz.faewulf.backpack.feature.backpacks.defaultBackPack.DefaultBackpackModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


public class BackpackModelRegistry {
    private static final Map<String, Function<EntityRendererProvider.Context, EntityModel<EntityRenderState>>> MODEL_REGISTRY = new HashMap<>();
    private static final Map<String, Map<String, ResourceLocation>> VARIANT_REGISTRY = new HashMap<>();

    public static void register() {
        BackpackModelRegistry.registerModel("default", (ctx) -> new DefaultBackpackModel(ctx.bakeLayer(DefaultBackpackModel.LAYER_LOCATION)));
        BackpackModelRegistry.registerModel("basket", (ctx) -> new BasketBackpackModel(ctx.bakeLayer(BasketBackpackModel.LAYER_LOCATION)));
        BackpackModelRegistry.registerModel("default2", (ctx) -> new DefaultBackpackModel(ctx.bakeLayer(DefaultBackpackModel.LAYER_LOCATION)));
    }

    // Register a backpack model
    public static void registerModel(String id, Function<EntityRendererProvider.Context, EntityModel<EntityRenderState>> fn) {
        MODEL_REGISTRY.put(id, fn);
    }

    public static void registerVariant(String modelId, String variantId, String textureLocation) {
        if (!isValidModel(modelId)) {
            Constants.LOG.error("No backpack model registered for identifier: " + modelId);
            return;
        }

        if (!VARIANT_REGISTRY.containsKey(modelId))
            VARIANT_REGISTRY.put(modelId, new HashMap<>());

        VARIANT_REGISTRY.get(modelId).put(variantId, ResourceLocation.tryBuild(Constants.MOD_ID, textureLocation));
    }

    // Get a backpack model by identifier
    public static Function<EntityRendererProvider.Context, EntityModel<EntityRenderState>> getModelClass(String id) {
        return MODEL_REGISTRY.get(id);
    }

    public static ResourceLocation getVariant(String modelId, String variantId) {
        if (VARIANT_REGISTRY.containsKey(modelId))
            return VARIANT_REGISTRY.get(modelId).get(variantId);
        else
            return null;
    }

    public static EntityModel<EntityRenderState> createBackpackModel(String id, EntityRendererProvider.Context context) {
        Function<EntityRendererProvider.Context, EntityModel<EntityRenderState>> modelClass = BackpackModelRegistry.getModelClass(id);

        if (modelClass == null) {
            Constants.LOG.error("No backpack model registered for identifier: " + id);
            return null;
        }

        try {
            return modelClass.apply(context);

        } catch (Exception e) {
            Constants.LOG.error("Failed to create backpack model for identifier: " + id, e);
            return null;
        }
    }

    public static List<String> getModelList() {
        return MODEL_REGISTRY.keySet().stream().toList();
    }

    public static List<String> getVariantList(String modelId) {
        if (VARIANT_REGISTRY.containsKey(modelId))
            return VARIANT_REGISTRY.get(modelId).keySet().stream().toList();

        return new ArrayList<>();
    }

    public static boolean isValidModel(String id) {
        return MODEL_REGISTRY.containsKey(id.toLowerCase());
    }

    public static boolean isValidVariant(String modelId, String variantId) {
        if (VARIANT_REGISTRY.containsKey(modelId))
            return VARIANT_REGISTRY.get(modelId).containsKey(variantId);
        return false;
    }
}
