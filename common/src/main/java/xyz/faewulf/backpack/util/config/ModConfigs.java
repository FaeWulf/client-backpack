package xyz.faewulf.backpack.util.config;

public class ModConfigs {

    @Entry(category = "general", name = "Enable mod", require_restart = false)
    public static boolean _enable_mod = true;

    @Entry(category = "general", name = "Enable dynamic light", require_restart = false)
    public static boolean enable_dynamiclight_compat = true;

    @Entry(category = "general", name = "Hide backpack if invisible", require_restart = false)
    public static boolean hide_if_invisible = true;

    @Entry(category = "general", name = "backpack", require_restart = false)
    public static BACKPACK_TYPE backpack = BACKPACK_TYPE.DEFAULT;


    public enum BACKPACK_TYPE {
        BASKET, DEFAULT
    }
}