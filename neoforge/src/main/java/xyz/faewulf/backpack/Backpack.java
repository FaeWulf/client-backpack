package xyz.faewulf.backpack;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import xyz.faewulf.backpack.util.config.infoScreen.ModInfoScreen;

@Mod(Constants.MOD_ID)
public class Backpack {

    public Backpack(IEventBus eventBus) {
        Constants.LOG.info("Loading");

        //MidnightConfig.init(Constants.MOD_ID, ModConfigs.class);

        CommonClass.init();

        //config
        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (client, parent) -> ModInfoScreen.getScreen(parent)
        );

        Constants.LOG.info("Init done");
    }
}