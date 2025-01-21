package xyz.faewulf.backpack.util.config.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;
import java.util.function.Function;

public class DummyPlayer extends LocalPlayer {
    private static DummyPlayer instance;
    private PlayerSkin playerSkin = null;
    public Function<EquipmentSlot, ItemStack> equippedStackSupplier = slot -> ItemStack.EMPTY;

    public static DummyPlayer createInstance(ClientLevel clientLevel) {
        if (instance == null) instance = new DummyPlayer(clientLevel);
        return instance;
    }

    public DummyPlayer(ClientLevel clientLevel) {
        super(Minecraft.getInstance(), clientLevel, Minecraft.getInstance().getConnection(), null, null, false, false);
        setUUID(UUID.randomUUID());
        Minecraft.getInstance().getSkinManager().getOrLoad(getGameProfile()).thenAccept((textures) -> textures.ifPresent(skin -> playerSkin = skin));
    }
}
