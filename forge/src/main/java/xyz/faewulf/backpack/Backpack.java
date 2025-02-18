package xyz.faewulf.backpack;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import xyz.faewulf.backpack.util.config.infoScreen.ModInfoScreen;

@Mod(Constants.MOD_ID)
public class Backpack {

    public Backpack() {
        Constants.LOG.info("Loading");

        CommonClass.init();

        //config
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> ModInfoScreen.getScreen(parent))
        );

        Constants.LOG.info("Init done");
    }
}