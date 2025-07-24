package xyz.faewulf.backpack.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.lib.api.v1.config.ConfigScreenHelper;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> {
            xyz.faewulf.lib.util.config.infoScreen.ModInfoScreen modInfoScreen = (xyz.faewulf.lib.util.config.infoScreen.ModInfoScreen) ConfigScreenHelper.getConfigScreen(screen, Constants.MOD_ID);
            modInfoScreen.setUrls(null, Constants.WEBSITE, null, Constants.SOURCE_CODE);
            return modInfoScreen;
        };
    }
}
