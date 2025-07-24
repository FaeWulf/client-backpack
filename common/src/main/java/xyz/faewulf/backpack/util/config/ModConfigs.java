package xyz.faewulf.backpack.util.config;

import xyz.faewulf.backpack.Constants;
import xyz.faewulf.lib.util.config.Entry;
import xyz.faewulf.lib.util.config.ModConfig;

@ModConfig(mod_id = Constants.MOD_ID)
public class ModConfigs {

    @Entry(category = "Client", name = "Enable mod", require_restart = false)
    public static boolean __enable_mod = true;

    @Entry(category = "Client", name = "Enable Cloud Sync", require_restart = false)
    public static boolean _enable_cloud_sync = true;

    @Entry(category = "Client", name = "Enable dynamic light", require_restart = false)
    public static boolean enable_dynamiclight_compat = true;

    @Entry(category = "Client", name = "Hide backpack if invisible", require_restart = false)
    public static boolean hide_if_invisible = true;

    @Entry(category = "Server", name = "Hide items component data", require_restart = false)
    public static boolean hide_items_component_data = false;

    @Entry(category = "style", name = "Backpack Model Type", require_restart = false, hidden = true)
    public static String backpack = "default";

    @Entry(category = "style", name = "Variant", require_restart = false, hidden = true)
    public static String variant = "default";
}