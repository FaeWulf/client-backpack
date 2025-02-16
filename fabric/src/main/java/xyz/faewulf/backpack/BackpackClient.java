package xyz.faewulf.backpack;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import xyz.faewulf.backpack.feature.fabric.BackpackPrepareModelLoading;
import xyz.faewulf.backpack.registry.ItemTagRegistry;
import xyz.faewulf.backpack.util.DataSync;

@Environment(EnvType.CLIENT)
public class BackpackClient implements ClientModInitializer {

    private static int timer = 0;

    @Override
    public void onInitializeClient() {

        // Client only event
        registerModelLoader();

        // Client tick event
        registerReloadEvent();

        // resource reload event
        registerClientTickEvent();
    }

    private static void registerModelLoader() {
        PreparableModelLoadingPlugin.register(BackpackPrepareModelLoading.LOADER, new BackpackPrepareModelLoading());
    }

    private static void registerReloadEvent() {
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

    private static void registerClientTickEvent() {
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
    }
}
