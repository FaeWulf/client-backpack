package xyz.faewulf.backpack;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.faewulf.backpack.inter.BackpackStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class Constants {
    public static final String MOD_ID = "client_backpack";
    public static final String MOD_NAME = "Client Backpack";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static final Map<String, List<ItemStack>> PLAYER_INV = new HashMap<>();
    public static final Map<String, BackpackStatus> PLAYER_INV_STATUS = new HashMap<>();
}