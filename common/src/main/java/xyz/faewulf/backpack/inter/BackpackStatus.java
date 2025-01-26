package xyz.faewulf.backpack.inter;

import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BackpackStatus {
    public String backpackType = "default";
    public String backpackVariant = "default";
    public boolean hasUpdateBackpackType = false;

    public String uuid = null;
    public boolean invChanged = true;
    public boolean hasLightSource = false;

    public int holdingSlot = -1;

    // axe, sword, pickaxe,...
    public List<ItemStack> toolsList = new ArrayList<>();
    // lava, water
    public List<ItemStack> liquidList = new ArrayList<>();
    // shulker, bundle,...
    public List<ItemStack> containerList = new ArrayList<>();

    public List<ItemStack> pocketList = new ArrayList<>();

    public ItemStack banner = null;

    public BackpackStatus() {

    }

    public JsonObject uploadDataJson() {
        JsonObject json = new JsonObject();
        json.addProperty("modelId", this.backpackType);
        json.addProperty("variantId", this.backpackVariant);
        return json;
    }
}
