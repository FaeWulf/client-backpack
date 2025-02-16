package xyz.faewulf.backpack.feature.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.inter.BackpackModelRecord.DetailBackpack;
import xyz.faewulf.backpack.registry.BackpackModelRegistry;
import xyz.faewulf.backpack.util.Converter;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class BackpackPrepareModelLoading implements PreparableModelLoadingPlugin<Collection<ResourceLocation>> {

    public static final DataLoader<Collection<ResourceLocation>> LOADER = (resourceManager, executor) ->
            CompletableFuture.supplyAsync(() -> {
                        Map<ResourceLocation, Resource> models = resourceManager.listResources("models/client_backpack", location -> location.getPath().endsWith(".json"));

                        List<ResourceLocation> modelIDs = new ArrayList<>();
                        models.forEach((key, resource) -> {
                            String path = key.getPath().substring("models/".length(), key.getPath().length() - ".json".length());
                            ModelResourceLocation location = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(key.getNamespace(), path), "");

                            String[] splitID = path.split("/");

                            if (splitID.length >= 3) {

                                //register model
                                modelIDs.add(location.id());

                                BackpackModelRegistry.addBackpack(splitID[1], splitID[2], location.id());

                                // Try get backpack's placement data on the same dir
                                tryGetBackPackDetail(resourceManager, path, key.getNamespace(), splitID[1], splitID[2]);
                            } else {
                                Constants.LOG.warn("[{}] Cannot load model {}: Wrong path structure.", "BackpackPrepareModelLoading", path);
                            }

                        });

                        BackpackModelRegistry.debugBackpackList();


                        return modelIDs;
                    }, executor
            );

    @Override
    public void initialize(Collection<ResourceLocation> resourceLocations, ModelLoadingPlugin.Context context) {
        context.addModels(resourceLocations);
    }

    private static void tryGetBackPackDetail(ResourceManager manager, String path, String nameSpace, String id, String variant) {
        // File name is the same as model's file name + .placement
        String placementDetailPath = "models/" + path.concat(".placement");

        // Make ResourceLocation out of current namespace and path
        ResourceLocation placementDetailResourceLocation = ResourceLocation.fromNamespaceAndPath(nameSpace, placementDetailPath);

        // Try get data
        Optional<DetailBackpack> detailBackpack = Converter.tryGetBackpackPlacementData(manager, placementDetailResourceLocation);

        // Add data if present
        if (detailBackpack.isPresent()) {
            BackpackModelRegistry.addBackpackDetail(id, variant, detailBackpack.get());
        } else {
            // If not, then try get default's placement value

            // Split by /, replace last string with default.placement
            String[] splitString = placementDetailPath.split("/");

            if (splitString.length > 0)
                splitString[splitString.length - 1] = "default.placement";

            String placementDetailDefaultPath = String.join("/", splitString);

            Constants.LOG.warn(" Replace {}'s Detail data with {} if possible...", placementDetailPath, placementDetailDefaultPath);

            Converter.tryGetBackpackPlacementData(manager, ResourceLocation.fromNamespaceAndPath(nameSpace, placementDetailDefaultPath))
                    .ifPresent(detailBackpack1 -> BackpackModelRegistry.addBackpackDetail(id, variant, detailBackpack1));
        }

    }
}
