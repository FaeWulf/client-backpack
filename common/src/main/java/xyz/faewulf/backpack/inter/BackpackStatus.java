package xyz.faewulf.backpack.inter;

import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BackpackStatus {
    String backpackType = "default";
    String backpackVariant = "default";
    boolean hasUpdateBackpackType = false;

    String uuid = null;
    boolean invChanged = true;
    boolean hasLightSource = false;

    int holdingSlot = -1;
    // axe, sword, pickaxe,...
    List<ItemStack> toolsList = new ArrayList<>();
    // lava, water
    List<ItemStack> liquidList = new ArrayList<>();
    // shulker, bundle,...
    List<ItemStack> containerList = new ArrayList<>();

    List<ItemStack> pocketList = new ArrayList<>();

    ItemStack banner = null;

    public BackpackStatus() {

    }

    public void resetInvData() {
        this.pocketList.clear();
        this.toolsList.clear();
        this.containerList.clear();
        this.liquidList.clear();
        this.hasLightSource = false;
        this.banner = null;
    }

    // Only update data related to inv
    public void updateInvData(BackpackStatus backpackStatus) {
        this.toolsList = backpackStatus.toolsList;
        this.liquidList = backpackStatus.liquidList;
        this.containerList = backpackStatus.containerList;
        this.pocketList = backpackStatus.pocketList;
        this.banner = backpackStatus.banner;
        this.holdingSlot = backpackStatus.holdingSlot;
        this.hasLightSource = backpackStatus.hasLightSource;
        this.invChanged = backpackStatus.invChanged;
    }

    // Only update data related to model
    public void updateModelData(BackpackStatus backpackStatus) {
        this.backpackType = backpackStatus.backpackType;
        this.backpackVariant = backpackStatus.backpackVariant;
    }

    public void updateModelData(String model_id, String variant_id) {
        this.backpackType = model_id;
        this.backpackVariant = variant_id;
    }

    public JsonObject uploadDataJson() {
        JsonObject json = new JsonObject();
        json.addProperty("modelId", this.backpackType);
        json.addProperty("variantId", this.backpackVariant);
        return json;
    }

    public ItemStack getBanner() {
        return banner;
    }

    public void setBanner(ItemStack banner) {
        this.banner = banner;
    }

    public List<ItemStack> getPocketList() {
        return pocketList;
    }

    public void setPocketList(List<ItemStack> pocketList) {
        this.pocketList = pocketList;
    }

    public List<ItemStack> getContainerList() {
        return containerList;
    }

    public void setContainerList(List<ItemStack> containerList) {
        this.containerList = containerList;
    }

    public List<ItemStack> getLiquidList() {
        return liquidList;
    }

    public void setLiquidList(List<ItemStack> liquidList) {
        this.liquidList = liquidList;
    }

    public List<ItemStack> getToolsList() {
        return toolsList;
    }

    public void setToolsList(List<ItemStack> toolsList) {
        this.toolsList = toolsList;
    }

    public int getHoldingSlot() {
        return holdingSlot;
    }

    public void setHoldingSlot(int holdingSlot) {
        this.holdingSlot = holdingSlot;
    }

    public boolean isHasLightSource() {
        return hasLightSource;
    }

    public void setHasLightSource(boolean hasLightSource) {
        this.hasLightSource = hasLightSource;
    }

    public boolean isInvChanged() {
        return invChanged;
    }

    public void setInvChanged(boolean invChanged) {
        this.invChanged = invChanged;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isHasUpdateBackpackType() {
        return hasUpdateBackpackType;
    }

    public void setHasUpdateBackpackType(boolean hasUpdateBackpackType) {
        this.hasUpdateBackpackType = hasUpdateBackpackType;
    }

    public String getBackpackVariant() {
        return backpackVariant;
    }

    public void setBackpackVariant(String backpackVariant) {
        this.backpackVariant = backpackVariant;
    }

    public String getBackpackType() {
        return backpackType;
    }

    public void setBackpackType(String backpackType) {
        this.backpackType = backpackType;
    }
}
