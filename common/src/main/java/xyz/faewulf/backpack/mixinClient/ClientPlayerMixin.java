package xyz.faewulf.backpack.mixinClient;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.inter.BackpackStatus;
import xyz.faewulf.backpack.inter.IClientPlayerBackpackData;
import xyz.faewulf.backpack.registry.BackpackModelRegistry;
import xyz.faewulf.backpack.util.compare;
import xyz.faewulf.backpack.util.config.ModConfigs;
import xyz.faewulf.backpack.util.converter;

@Mixin(AbstractClientPlayer.class)
public abstract class ClientPlayerMixin extends Player implements IClientPlayerBackpackData {

    @Unique
    private String client_Backpack$modelType = "default";
    @Unique
    private String client_Backpack$variantType = "default";

    @Unique
    private boolean runOnce = false;

    public ClientPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    // Set backpack type and variant if they are current local player
    @Inject(method = "<init>", at = @At("TAIL"))
    private void initInject(ClientLevel clientLevel, GameProfile gameProfile, CallbackInfo ci) {
        // init InV
        Constants.PLAYER_INV.put(this.getName().getString(), converter.takeInventorySnapshot(this));
        // Create status for new player
        this.client_Backpack$createNewStatus();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickInject(CallbackInfo ci) {
        if (this.level().isClientSide) {
            if (!this.isAlive()) {
                Constants.PLAYER_INV.remove(this.getName().getString());
                Constants.PLAYER_INV_STATUS.remove(this.getName().getString());
            } else {
                if (Minecraft.getInstance().player != null && !runOnce) {
                    runOnce = true;
                    Constants.PLAYER_INV.put(this.getName().getString(), converter.takeInventorySnapshot(this));

                    // Create status for new player
                    this.client_Backpack$createNewStatus();

                    if (this.getName().getString().equals(Minecraft.getInstance().player.getName().getString())) {
                        this.client_Backpack$setModel(ModConfigs.backpack);
                    } else {
                        this.client_Backpack$setModel("default");
                    }
                }

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

    @Unique
    private void client_Backpack$createNewStatus() {
        Constants.PLAYER_INV_STATUS.computeIfAbsent(this.getName().getString(), k -> {
            BackpackStatus backpackStatus = new BackpackStatus();
            backpackStatus.backpackVariant = client_Backpack$variantType;
            backpackStatus.backpackType = client_Backpack$modelType;
            return backpackStatus;
        });
    }

    @Override
    public String client_Backpack$getModel() {
        return this.client_Backpack$modelType;
    }

    @Override
    public void client_Backpack$setModel(String value) {
        if (BackpackModelRegistry.isValid(value)) {
            this.client_Backpack$modelType = value;
            // Update status if present
            Constants.PLAYER_INV_STATUS.computeIfPresent(this.getName().getString(), (k, v) -> {
                v.backpackType = value;
                return v;
            });
        } else
            Constants.LOG.error(value + " is not a valid backpack id.");
    }

    @Override
    public String client_Backpack$getVariant() {
        return this.client_Backpack$variantType;
    }

    @Override
    public void client_Backpack$setVariant(String value) {
        this.client_Backpack$variantType = value;
        // Update status if present
        Constants.PLAYER_INV_STATUS.computeIfPresent(this.getName().getString(), (k, v) -> {
            v.backpackVariant = value;
            return v;
        });
    }
}
