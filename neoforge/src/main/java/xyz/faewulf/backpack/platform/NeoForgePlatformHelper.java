package xyz.faewulf.backpack.platform;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import xyz.faewulf.backpack.platform.services.IPlatformHelper;

import java.util.ArrayList;
import java.util.List;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public boolean isClientSide() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    @Override
    public List<Class<?>> findClasses(String scannedPackage) {
        List<Class<?>> result = new ArrayList<>();

        ModList.get().getAllScanData().forEach(modFileScanData -> {
            modFileScanData.getClasses().forEach(classData -> {
                if (classData.clazz().getClassName().contains(scannedPackage)) {
                    Class<?> clazz = getClass(classData.clazz().getClassName());
                    if (clazz != null)
                        result.add(clazz);
                }
            });
        });

        return result;
    }

    private Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ignore) {
            return null;
        }
    }

}