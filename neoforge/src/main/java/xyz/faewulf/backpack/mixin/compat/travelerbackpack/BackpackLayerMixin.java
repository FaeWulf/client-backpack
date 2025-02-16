package xyz.faewulf.backpack.mixin.compat.travelerbackpack;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.tiviacz.travelersbackpack.client.renderer.BackpackLayer")
public class BackpackLayerMixin {
    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/PlayerRenderState;FF)V", at = @At("HEAD"), cancellable = true)
    private void renderInjection(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, PlayerRenderState state, float limbSwing, float limbSwingAmount, CallbackInfo ci) {
        // Todo: port for neoforge
        ci.cancel();
    }
}
