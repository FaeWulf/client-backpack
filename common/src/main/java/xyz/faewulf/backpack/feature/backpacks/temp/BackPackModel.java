package xyz.faewulf.backpack.feature.backpacks.temp;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import xyz.faewulf.backpack.Constants;

public class BackPackModel<T extends GeoAnimatable> extends GeoModel<T> {
    private final ResourceLocation model = ResourceLocation.tryBuild(Constants.MOD_ID, "geo/example.geo.json");
    private final ResourceLocation texture = ResourceLocation.tryBuild(Constants.MOD_ID, "textures/item/example.png");
    private final ResourceLocation animations = ResourceLocation.tryBuild(Constants.MOD_ID, "animations/example.animation.json");

    @Override
    public ResourceLocation getModelResource(GeoAnimatable geoAnimatable, @Nullable GeoRenderer geoRenderer) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(GeoAnimatable geoAnimatable, @Nullable GeoRenderer geoRenderer) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(GeoAnimatable geoAnimatable) {
        return animations;
    }
}
