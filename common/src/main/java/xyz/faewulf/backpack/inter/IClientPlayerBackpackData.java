package xyz.faewulf.backpack.inter;

public interface IClientPlayerBackpackData {
    default String client_Backpack$getModel() {
        return "default";
    }

    default String client_Backpack$getVariant() {
        return "default";
    }

    void client_Backpack$setModel(String value);

    void client_Backpack$setVariant(String value);
}
