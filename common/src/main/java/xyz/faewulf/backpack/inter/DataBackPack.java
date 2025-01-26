package xyz.faewulf.backpack.inter;

import xyz.faewulf.backpack.Constants;

public record DataBackPack(String name, String uuid, String model_id, String variant_id) {
    public void updateForPlayer() {
        Constants.PLAYER_INV_STATUS.computeIfPresent(name, (k, v) -> {
            v.setHasUpdateBackpackType(true);
            v.updateModelData(model_id, variant_id);
            return v;
        });
    }
}
