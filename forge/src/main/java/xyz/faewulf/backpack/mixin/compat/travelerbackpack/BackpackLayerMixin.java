package xyz.faewulf.backpack.mixin.compat.travelerbackpack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.model.BackpackLayerModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.faewulf.backpack.util.config.ModConfigs;

@Pseudo
@Mixin(targets = "com.tiviacz.travelersbackpack.client.renderer.BackpackLayer")
public class BackpackLayerMixin {
    @Inject(method = "renderBackpackLayer", at = @At("HEAD"), cancellable = true)
    private static void renderBackpackLayerInjection(BackpackLayerModel model, HumanoidModel humanoidModel, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, LivingEntity entity, ItemStack stack, CallbackInfo ci) {
        ci.cancel();
    }
}
