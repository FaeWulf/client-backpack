package xyz.faewulf.backpack.inter;

import xyz.faewulf.backpack.Constants;

public record DataBackPack(String name, String uuid, String model_id, String variant_id) {
    public void updateForPlayer() {
        Constants.PLAYER_INV_STATUS.computeIfPresent(name, (k, v) -> {
            v.hasUpdateBackpackType = true;
            v.backpackType = model_id;
            v.backpackVariant = variant_id;
            return v;
        });
    }
}
