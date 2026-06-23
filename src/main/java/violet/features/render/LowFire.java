package violet.features.render;

import violet.config.Feature;
import violet.config.SettingBool;

public class LowFire {
    public static final Feature instance = new Feature("lowFire");

    public static final SettingBool noRender = new SettingBool(false, "lowFire", instance);
}
