package xyz.faewulf.backpack.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.inter.BackpackStatus;
import xyz.faewulf.backpack.platform.Services;
import xyz.faewulf.backpack.util.Compare;
import xyz.faewulf.backpack.util.Converter;

import java.util.List;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    public ServerPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initInject(MinecraftServer p_254143_, ServerLevel p_254435_, GameProfile p_253651_, CallbackInfo ci) {
        // init InV
        Constants.SERVER_PLAYER_INV.put(this.getName().getString(), Converter.takeInventorySnapshot(this));
        // Create status for new player
        this.client_Backpack$createNewStatus();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickInject(CallbackInfo ci) {
        if (!this.level().isClientSide) {
            if (!this.isAlive()) {
                // If player died then reset all the status
                Constants.SERVER_PLAYER_INV.remove(this.getName().getString());
                Constants.SERVER_PLAYER_INV_STATUS.remove(this.getName().getString());
            } else {
                // Check for inv change if player exists
                // Then update it into PLAYER_INV_STATUS
                Constants.SERVER_PLAYER_INV_STATUS.computeIfPresent(this.getName().getString(), (k, v) -> {
                    // if inv change
                    if (Compare.hasInventoryChanged(this) || this.getInventory().selected != v.getHoldingSlot()) {
                        v.setHoldingSlot(this.getInventory().selected);
                        v.setInvChanged(true);

                    }

                    // Mod support
                    v.setWearingBackpack(Services.SERVER_HELPER.isWearingBackpack(this));
                    return v;
                });

                //update Inventory
                List<ItemStack> inv = Converter.takeInventorySnapshot(this);

                if (Services.SERVER_HELPER.isWearingBackpack(this))
                    inv.addAll(Services.SERVER_HELPER.getBackpackInventory(this));

                Constants.SERVER_PLAYER_INV.put(this.getName().getString(), inv);
            }
        }
    }

    @Unique
    private void client_Backpack$createNewStatus() {
        Constants.SERVER_PLAYER_INV_STATUS.computeIfAbsent(this.getName().getString(), k -> {
            BackpackStatus backpackStatus = new BackpackStatus();
            backpackStatus.setHoldingSlot(this.getInventory().selected);
            return backpackStatus;
        });
    }
}
