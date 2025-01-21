package xyz.faewulf.backpack.mixinClient;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.faewulf.backpack.util.config.util.DummyPlayer;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    //hide label from DummyPlayer
    @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/Entity;D)Z", at = @At("HEAD"), cancellable = true)
    private void hideLabelForDummyPlayer(Entity par1, double par2, CallbackInfoReturnable<Boolean> cir) {
        if (par1 instanceof DummyPlayer dummyPlayer) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
