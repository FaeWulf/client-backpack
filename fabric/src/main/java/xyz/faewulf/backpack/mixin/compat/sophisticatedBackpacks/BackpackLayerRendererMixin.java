package xyz.faewulf.backpack.mixin.compat.sophisticatedBackpacks;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.IBackpackModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackLayerRenderer")
public abstract class BackpackLayerRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public BackpackLayerRendererMixin(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Inject(
            method = "renderBackpack",
            at = @At(
                    value = "HEAD"
            ), cancellable = true)
    private static  <T extends LivingEntity, M extends EntityModel<T>>  void renderInject(M parentModel, LivingEntity livingEntity, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, ItemStack backpack, boolean wearsArmor, IBackpackModel model, CallbackInfo ci) {
        // Todo: port for neoforge
        if(livingEntity instanceof AbstractClientPlayer) {
            ci.cancel();
        }
    }
}
