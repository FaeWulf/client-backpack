package xyz.faewulf.backpack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.faewulf.backpack.inter.BackpackModelRecord.DetailTransform;
import xyz.faewulf.backpack.inter.BackpackModelRecord.LightSourceDetail;
import xyz.faewulf.backpack.inter.BackpackStatus;
import xyz.faewulf.backpack.inter.IDynamicLightCompatLayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {
    public static final String MOD_ID = "client_backpack";
    public static final String MOD_NAME = "Client Backpack";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LightSourceDetail.class, new LightSourceDetail.LightSourceDeserializer())
            .registerTypeAdapter(DetailTransform.class, new DetailTransform.Deserializer())
            .create();

    public static final String DUMMY_PLAYER_NAME = ";-;";

    public static final Map<String, List<ItemStack>> PLAYER_INV = new HashMap<>();
    public static final Map<String, BackpackStatus> PLAYER_INV_STATUS = new HashMap<>();

    public static final Map<String, BackpackStatus> SERVER_PLAYER_INV_STATUS = new HashMap<>();
    public static final Map<String, List<ItemStack>> SERVER_PLAYER_INV = new HashMap<>();

    public static IDynamicLightCompatLayer DYNAMIC_LIGHT_COMPAT_LAYER = null;
}