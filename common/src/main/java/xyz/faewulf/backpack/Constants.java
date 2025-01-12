package xyz.faewulf.backpack;

import net.minecraft.world.entity.player.Inventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.WeakHashMap;

public class Constants {
    public static final String MOD_ID = "client_backpack";
    public static final String MOD_NAME = "Client Backpack";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static final Map<String, Inventory> PLAYER_INV = new WeakHashMap<>();
}