package xyz.faewulf.backpack.mixinClient;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.inter.BackpackStatus;
import xyz.faewulf.backpack.util.compare;
import xyz.faewulf.backpack.util.converter;

import java.util.function.Function;

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
                Constants.PLAYER_INV_STATUS.remove(this.getName().getString());
            } else {
                // Check for inv change if player exists
                Constants.PLAYER_INV_STATUS.computeIfPresent(this.getName().getString(), (k, v) -> {
                    // if inv change
                    if (compare.hasInventoryChanged(this) || this.getInventory().selected != v.holdingSlot) {
                        v.holdingSlot = this.getInventory().selected;
                        v.invChanged = true;
                    }
                    return v;
                });

                // Create status for new player
                Constants.PLAYER_INV_STATUS.computeIfAbsent(this.getName().getString(), k -> {
                    BackpackStatus backpackStatus = new BackpackStatus();
                    backpackStatus.holdingSlot = this.getInventory().selected;
                    return backpackStatus;
                });

                Constants.PLAYER_INV.put(this.getName().getString(), converter.takeInventorySnapshot(this));
            }
        }
    }
}
