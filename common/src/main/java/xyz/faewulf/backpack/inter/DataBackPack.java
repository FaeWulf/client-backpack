package xyz.faewulf.backpack.inter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.util.config.Config;
import xyz.faewulf.backpack.util.config.ConfigScreen.CustomizeScreen;
import xyz.faewulf.backpack.util.config.ModConfigs;
import xyz.faewulf.backpack.util.config.util.DummyPlayer;
import xyz.faewulf.backpack.util.misc;

import java.util.Objects;

public record DataBackPack(String name, String uuid, String model_id, String variant_id) {
    public void updateForPlayer() {

        // If uuid match Client player, then update into Mod's Config
        if (Minecraft.getInstance().player != null && Objects.equals(Minecraft.getInstance().player.getUUID().toString(), uuid)) {
            ModConfigs.variant = variant_id;
            ModConfigs.backpack = model_id;
            Config.save();

            // Refresh data related to CustomizeScreen
            ClientLevel clientLevel = Minecraft.getInstance().level;
            if (clientLevel != null) {
                DummyPlayer.createInstance(clientLevel).refreshBackpackData();
            }
            CustomizeScreen.updateRequest = true;

            misc.sendSystemToast(Component.translatable("backpack.system.upload.syncLocal.done"), null);
        }

        Constants.PLAYER_INV_STATUS.computeIfPresent(name, (k, v) -> {
            v.setHasUpdateBackpackType(true);
            v.updateModelData(model_id, variant_id);
            return v;
        });
    }
}
