package violet.features.render;

import org.lwjgl.glfw.GLFW;

import meteordevelopment.orbit.EventHandler;
import violet.config.Feature;
import violet.config.SettingBool;
import violet.config.SettingKeybind;
import violet.events.InputEvent;

public class NBTTooltip {
    public static final Feature instance = new Feature("nbttooltip");

    public static final SettingBool showWhileHeld = new SettingBool(false, "showWhileHeld", instance);
    public static final SettingKeybind showKeybind = new SettingKeybind(GLFW.GLFW_KEY_UNKNOWN, "showKeybind", instance);

    public static boolean isKeyHeld = false;

    @EventHandler
    private static void onKey(InputEvent event) {
        isKeyHeld = instance.isActive() && showKeybind.isKey(event.key) && event.action != 0;
    }    
}
