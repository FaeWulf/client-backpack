package xyz.faewulf.backpack.mixinClient;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
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
import xyz.faewulf.backpack.inter.IClientPlayerBackpackData;
import xyz.faewulf.backpack.platform.Services;
import xyz.faewulf.backpack.registry.BackpackModelRegistry;
import xyz.faewulf.backpack.util.Compare;
import xyz.faewulf.backpack.util.Converter;
import xyz.faewulf.backpack.util.config.ModConfigs;

import java.util.List;

@Mixin(AbstractClientPlayer.class)
public abstract class ClientPlayerMixin extends Player implements IClientPlayerBackpackData {

    @Unique
    private String client_Backpack$modelType = "default";
    @Unique
    private String client_Backpack$variantType = "default";

    @Unique
    private boolean client_Backpack$runOnce = false;

    public ClientPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    // Set backpack type and variant if they are current local player
    @Inject(method = "<init>", at = @At("TAIL"))
    private void initInject(ClientLevel clientLevel, GameProfile gameProfile, CallbackInfo ci) {
        // init InV
        Constants.PLAYER_INV.put(this.getName().getString(), Converter.takeInventorySnapshot(this));
        // Create status for new player
        this.client_Backpack$createNewStatus();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickInject(CallbackInfo ci) {
        if (this.level().isClientSide) {
            if (!this.isAlive()) {
                // If player died then reset all the status
                Constants.PLAYER_INV.remove(this.getName().getString());
                Constants.PLAYER_INV_STATUS.computeIfPresent(this.getName().getString(), (k, v) -> {
                    v.resetInvData();
                    return v;
                });
            } else {

                // This one runs once when player join the world
                // Get and set backpack
                // Todo: System to make other player can see your backpack's customizations
                if (Minecraft.getInstance().player != null && !client_Backpack$runOnce) {
                    client_Backpack$runOnce = true;

                    // Update backpack
                    //Constants.PLAYER_INV.put(this.getName().getString(), Converter.takeInventorySnapshot(this));

                    // Create status for player just joined the level
                    this.client_Backpack$createNewStatus();

                    if (this.getName().getString().equals(Minecraft.getInstance().player.getName().getString())) {
                        this.client_Backpack$setModel(ModConfigs.backpack);
                        this.client_Backpack$setVariant(ModConfigs.variant);
                    } else {
                        this.client_Backpack$setModel("default");
                        this.client_Backpack$setVariant("default");
                    }
                }

                // Check for inv change if player exists
                // Then update it into PLAYER_INV_STATUS
                // Only for Local player
                if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getName().getString().equals(this.getName().getString())) {
                    Constants.PLAYER_INV_STATUS.computeIfPresent(this.getName().getString(), (k, v) -> {
                        // if inv change
                        if (Compare.hasInventoryChanged(this) || this.getInventory().selected != v.getHoldingSlot()) {
                            v.setHoldingSlot(this.getInventory().selected);
                            v.setInvChanged(true);

                        }

                        // Mod backpack support
                        v.setWearingBackpack(Services.SERVER_HELPER.isWearingBackpack(this));

                        return v;
                    });

                    // update Inventory
                    List<ItemStack> inv = Converter.takeInventorySnapshot(this);

                    if (Services.SERVER_HELPER.isWearingBackpack(this))
                        inv.addAll(Services.SERVER_HELPER.getBackpackInventory(this));

                    Constants.PLAYER_INV.put(this.getName().getString(), inv);
                }
            }
        }
    }

    @Unique
    private void client_Backpack$createNewStatus() {
        Constants.PLAYER_INV_STATUS.computeIfAbsent(this.getName().getString(), k -> {
            BackpackStatus backpackStatus = new BackpackStatus();
            backpackStatus.updateModelData(client_Backpack$modelType, client_Backpack$variantType);
            backpackStatus.setUuid(this.getStringUUID());
            backpackStatus.setHoldingSlot(this.getInventory().selected);
            return backpackStatus;
        });
    }

    @Override
    public String client_Backpack$getModel() {
        return this.client_Backpack$modelType;
    }

    @Override
    public void client_Backpack$setModel(String value) {
        if (BackpackModelRegistry.isValidBackpack(value)) {
            this.client_Backpack$modelType = value;
            // Update status if present
            Constants.PLAYER_INV_STATUS.computeIfPresent(this.getName().getString(), (k, v) -> {
                v.setBackpackType(value);
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
        if (BackpackModelRegistry.isValidBackpack(this.client_Backpack$modelType, value)) {

            this.client_Backpack$variantType = value;
            // Update status if present
            Constants.PLAYER_INV_STATUS.computeIfPresent(this.getName().getString(), (k, v) -> {
                v.setBackpackVariant(value);
                return v;
            });
        } else
            Constants.LOG.error(value + " is not a valid variant id.");
    }
}
