package violet.features.misc;

import violet.config.Feature;
import violet.config.SettingInt;

public class DebugScreen {
    public static final Feature instance = new Feature("debugScreen");

    public static final SettingInt xyzPrecision = new SettingInt(3, "xyzPrecision", instance);
    public static final SettingInt facingPrecision = new SettingInt(2, "facingPrecision", instance);
}
