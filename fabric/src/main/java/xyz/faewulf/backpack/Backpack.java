package xyz.faewulf.backpack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import xyz.faewulf.backpack.feature.backpacks.basketBackpack.BasketBackpackModel;
import xyz.faewulf.backpack.feature.backpacks.defaultBackPack.DefaultBackpackModel;
import xyz.faewulf.backpack.registry.ItemTagRegistry;

public class Backpack implements ModInitializer {

    @Override
    public void onInitialize() {
        Constants.LOG.info("Loading");

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            EntityModelLayerRegistry.registerModelLayer(DefaultBackpackModel.LAYER_LOCATION, DefaultBackpackModel::createBodyLayer);
            EntityModelLayerRegistry.registerModelLayer(BasketBackpackModel.LAYER_LOCATION, BasketBackpackModel::createBodyLayer);
        }

        loadCommand();
        loadEvent();

        CommonClass.init();

        Constants.LOG.info("Init done");
    }

    private void loadCommand() {
        Constants.LOG.info("Register commands...");
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            //slimechunk.register(dispatcher);
        });
    }

    private void loadEvent() {
        Constants.LOG.info("Register events...");
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
        });

        // resource reload event
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return null;
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                ItemTagRegistry.loadAllBackpackItems();
            }
        });


    }

}
