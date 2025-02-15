package xyz.faewulf.backpack.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.inter.BackpackModelRecord.DetailBackpack;
import xyz.faewulf.backpack.registry.BackpackModelRegistry;
import xyz.faewulf.backpack.util.Converter;

import java.util.Map;
import java.util.Optional;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class onLoadingAdditionalModel {
    @SubscribeEvent
    public static void onRegisterAdditionalModel(ModelEvent.RegisterAdditional event) {

        ResourceManager manager = Minecraft.getInstance().getResourceManager();

        Map<ResourceLocation, Resource> models = manager.listResources("models/client_backpack", location -> location.getPath().endsWith(".json"));

        models.forEach((key, resource) -> {
            String path = key.getPath().substring("models/".length(), key.getPath().length() - ".json".length());
            ModelResourceLocation location = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(key.getNamespace(), path), "");


            String[] splitID = path.split("/");

            if (splitID.length >= 3) {

                //register model
                event.register(location.id());

                BackpackModelRegistry.addBackpack(splitID[1], splitID[2], location.id());

                // Try get backpack's placement data on the same dir
                tryGetBackPackDetail(manager, path, key.getNamespace(), splitID[1], splitID[2]);
            } else {
                Constants.LOG.warn("[{}] Cannot load model {}: Wrong path structure.", "BackpackPrepareModelLoading", path);
            }

        });
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