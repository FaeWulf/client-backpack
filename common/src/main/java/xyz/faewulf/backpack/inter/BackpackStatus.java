package xyz.faewulf.backpack.inter;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BackpackStatus {
    public String backpackType = "default";
    public String backpackVariant = "default";

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
}
