package xyz.faewulf.backpack;

import com.mojang.datafixers.kinds.Const;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
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
import xyz.faewulf.backpack.platform.Services;
import xyz.faewulf.backpack.registry.ItemTagRegistry;
import xyz.faewulf.backpack.util.DataSync;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class Backpack implements ModInitializer {

    private int timer = 0;

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

                        if (!backpackStatus.hasUpdateBackpackType && backpackStatus.uuid != null) {
                            DataSync.UPDATE_QUEUE.put(s, backpackStatus.uuid);
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


        }


    }

}
