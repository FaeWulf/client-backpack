package xyz.faewulf.backpack.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.feature.backpacks.basketBackpack.BasketBackpackModel;
import xyz.faewulf.backpack.feature.backpacks.defaultBackPack.DefaultBackpackModel;


@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModelLayerRegistry {
    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // Register your custom model layer
        event.registerLayerDefinition(DefaultBackpackModel.LAYER_LOCATION, DefaultBackpackModel::createBodyLayer);
        event.registerLayerDefinition(BasketBackpackModel.LAYER_LOCATION, BasketBackpackModel::createBodyLayer);
    }
}
