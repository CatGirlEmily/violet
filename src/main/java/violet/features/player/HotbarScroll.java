package violet.features.player;

import violet.config.Feature;
import violet.config.SettingBool;

public class HotbarScroll {
    public static final Feature instance = new Feature("hotbarScroll");

    public static final SettingBool lockScroll = new SettingBool(true, "lockScroll", instance);
    public static final SettingBool noOverflow = new SettingBool(false, "noOverflow", instance);
}