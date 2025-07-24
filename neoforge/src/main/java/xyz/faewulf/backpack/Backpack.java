package xyz.faewulf.backpack;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class Backpack {

    public Backpack(IEventBus eventBus) {
        Constants.LOG.info("Loading");
        CommonClass.init();
        Constants.LOG.info("Init done");
    }
}