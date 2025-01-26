package xyz.faewulf.backpack.util.config.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.inter.IClientPlayerBackpackData;
import xyz.faewulf.backpack.util.config.ModConfigs;

import java.util.UUID;

public class DummyPlayer extends AbstractClientPlayer {
    private static DummyPlayer instance;
    private PlayerSkin playerSkin = null;

    public static DummyPlayer createInstance(ClientLevel clientLevel) {
        if (instance == null || instance.clientLevel != clientLevel) instance = new DummyPlayer(clientLevel);
        return instance;
    }

    public DummyPlayer(ClientLevel clientLevel) {
        super(clientLevel, new GameProfile(UUID.randomUUID(), Constants.DUMMY_PLAYER_NAME));
        //super(Minecraft.getInstance(), clientLevel, Minecraft.getInstance().getConnection(), null, null, false, false);
        setUUID(UUID.randomUUID());

        if (Minecraft.getInstance().player != null)
            setUUID(Minecraft.getInstance().player.getUUID());

        Minecraft.getInstance().getSkinManager().getOrLoad(getGameProfile()).thenAccept((textures) -> textures.ifPresent(skin -> playerSkin = skin));

        // Todo: Variant
        if (this instanceof IClientPlayerBackpackData clientPlayerBackpackData) {
            clientPlayerBackpackData.client_Backpack$setModel(ModConfigs.backpack);
            clientPlayerBackpackData.client_Backpack$setVariant(ModConfigs.variant);
        }
    }
}
