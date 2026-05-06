package violet.features.movement;

import static violet.Main.mc;

import org.lwjgl.glfw.GLFW;

import meteordevelopment.orbit.EventHandler;
import violet.config.Feature;
import violet.config.SettingKeybind;
import violet.events.InputEvent;
import violet.events.WorldTickEvent;

public class AutoSprint {
    public static final Feature instance = new Feature("autosprint");

    public static final SettingKeybind toggleKey = new SettingKeybind(GLFW.GLFW_KEY_UNKNOWN, "toggleKey", instance);

    public static boolean isSprinting = true;

    private static void setSprinting(boolean sprinting) {
        if (mc.options.getSprintToggled().getValue()) {
            if (mc.options.sprintKey.isPressed() == !sprinting) {
                mc.options.sprintKey.setPressed(true);
            }
        } else {
            mc.options.sprintKey.setPressed(sprinting);
        }
    }

    @EventHandler
    private static void onTick(WorldTickEvent event) {
        if (!instance.isActive()) return;

        setSprinting(isSprinting);
    }

    @EventHandler
    private static void onKey(InputEvent event) {
        if (!instance.isActive() || mc.currentScreen != null) return;
        
        if (toggleKey.isKey(event.key) && event.action == GLFW.GLFW_PRESS) {
            isSprinting = !isSprinting;
        }
    }
}

