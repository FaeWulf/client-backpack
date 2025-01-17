package xyz.faewulf.backpack.mixinClient.core;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SpecialBlockModelRenderer;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.faewulf.backpack.util.ModelHelper;

import java.util.Map;

@Mixin(ModelManager.class)
public class ModelManagerMixin {
    @Inject(method = "loadModels", at = @At(value = "HEAD"))
    private static void reloadInject(ProfilerFiller profiler, Map<ResourceLocation, AtlasSet.StitchResult> atlasPreperations, ModelBakery modelBakery, Object2IntMap<BlockState> modelGroups, EntityModelSet entityModelSet, SpecialBlockModelRenderer specialBlockModelRenderer, CallbackInfoReturnable<ModelManager.ReloadState> cir) {
        ModelHelper.JessieLetMeCook = modelBakery;
    }
}
