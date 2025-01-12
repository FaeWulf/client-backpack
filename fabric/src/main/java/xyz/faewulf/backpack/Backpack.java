package xyz.faewulf.backpack;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import xyz.faewulf.backpack.feature.BackpackModel;
import xyz.faewulf.backpack.registry.CustomModelLayers;

public class Backpack implements ModInitializer {

    @Override
    public void onInitialize() {
        Constants.LOG.info("Loading");

        EntityModelLayerRegistry.registerModelLayer(CustomModelLayers.BACKPACK, BackpackModel::createBodyLayer);

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
    }

}
