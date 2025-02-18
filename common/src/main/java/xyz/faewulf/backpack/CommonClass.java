package xyz.faewulf.backpack;

import com.mojang.datafixers.kinds.Const;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import xyz.faewulf.backpack.networking.networkConstants;
import xyz.faewulf.backpack.registry.BackpackModelRegistry;
import xyz.faewulf.backpack.platform.Services;
import xyz.faewulf.backpack.registry.ItemTagRegistry;
import xyz.faewulf.backpack.util.DataSync;

public class CommonClass {
    public static void init() {

        //Constants.LOG.info("Hello from Common init on {}! we are currently in a {} environment!", Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());
        //Constants.LOG.info("The ID for diamonds is {}", BuiltInRegistries.ITEM.getKey(Items.DIAMOND));

        networkConstants.init();

        //for debug/testing
        if (Services.PLATFORM.isDevelopmentEnvironment() && Services.PLATFORM.isClientSide())
            SharedConstants.IS_RUNNING_IN_IDE = true;

        //load config, moved to util.mixinPlugin.ConditionalMixinPlugin method: onLoad()
        //Config.init();

        // For mod load
        Constants.CURIOS_LOADED = Services.PLATFORM.isModLoaded("curios");
        Constants.TRINKETS_LOADED = Services.PLATFORM.isModLoaded("trinkets");
        Constants.SOPHISTICATED_BACKPACKS_LOADED = Services.PLATFORM.isModLoaded("sophisticatedbackpacks");
        Constants.TRAVELERS_BACKPACK_LOADED = Services.PLATFORM.isModLoaded("travelersbackpack");
        Constants.INMIS_BACKPACK_LOADED = Services.PLATFORM.isModLoaded("inmis");
    }
}