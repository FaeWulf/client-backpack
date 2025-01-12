package xyz.faewulf.backpack.mixinClient;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.faewulf.backpack.Constants;

@Mixin(AbstractClientPlayer.class)
public abstract class ClientPlayerMixin extends Player {
    public ClientPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickInject(CallbackInfo ci) {
        if (this.level().isClientSide) {
            if (!this.isAlive()) {
                Constants.PLAYER_INV.remove(this.getName().getString());
            } else {
                Constants.PLAYER_INV.put(this.getName().getString(), getInventory());
            }
        }
    }
}
