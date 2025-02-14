package xyz.faewulf.backpack;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import xyz.faewulf.backpack.feature.fabric.BackpackPrepareModelLoading;
import xyz.faewulf.backpack.platform.Services;
import xyz.faewulf.backpack.registry.ItemTagRegistry;
import xyz.faewulf.backpack.util.DataSync;

public class Backpack implements ModInitializer {

    private int timer = 0;

    @Override
    public void onInitialize() {
        Constants.LOG.info("Loading");

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

        // Client only event
        if (Services.PLATFORM.isClientSide()) {

            // Todo: port for neoforge
            // Client tick event
            // For fetching not yet update player's backpack data
            ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
                if (minecraft.level == null || minecraft.player == null)
                    return;

                timer++;

                if (timer >= 100) {
                    timer = 0;

                    Constants.PLAYER_INV_STATUS.forEach((s, backpackStatus) -> {
                        // Skip LocalPlayer
                        if (s.equals(Constants.DUMMY_PLAYER_NAME) || s.equals(minecraft.player.getName().getString()))
                            return;

                        // Max 20 to avoid api rate limit
                        if (DataSync.UPDATE_QUEUE.size() > 20)
                            return;

                        if (!backpackStatus.isHasUpdateBackpackType() && backpackStatus.getUuid() != null) {
                            DataSync.UPDATE_QUEUE.put(s, backpackStatus.getUuid());
                        }
                    });

                    DataSync.requestUpdateData();
                }
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

            // Test for new model loading
            // Todo: fix the problerm
            PreparableModelLoadingPlugin.register(BackpackPrepareModelLoading.LOADER, new BackpackPrepareModelLoading());
        }


    }

}
