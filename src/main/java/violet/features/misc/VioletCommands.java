package violet.features.misc;

import static violet.Main.mc;

import org.lwjgl.glfw.GLFW;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screens.ChatScreen;
import violet.config.Feature;
import violet.config.SettingBool;
import violet.config.SettingString;
import violet.events.InputEvent;

public class VioletCommands {
    public static final Feature instance = new Feature("violetCommands");

    public static final SettingString prefix = new SettingString(".", "prefix", instance);
    public static final SettingBool openChatOnKeybind = new SettingBool(true, "openChatOnKeybind", instance);
    public static final SettingBool addToHistory = new SettingBool(true, "addToHistory", instance);


    public static char getPrefix() {
        String val = prefix.value();
        if (val == null || val.isEmpty()) return '.';
        return val.charAt(0);
    }

    @EventHandler
    private static void onKey(InputEvent event) {
        if (!openChatOnKeybind.value() || !instance.isActive() || event.action != GLFW.GLFW_PRESS) return;

        int expectedKey = getGlfwKey(getPrefix());
        if (expectedKey == -1 || event.key != expectedKey) return;

        if (mc.gui.screen() == null) {
            mc.gui.setScreen(new ChatScreen("", false));
        }
    }

    private static int getGlfwKey(char c) {
        return switch (c) {
            case '.' -> GLFW.GLFW_KEY_PERIOD;
            case ',' -> GLFW.GLFW_KEY_COMMA;
            case '/' -> GLFW.GLFW_KEY_SLASH;
            case ';' -> GLFW.GLFW_KEY_SEMICOLON;
            case '\'' -> GLFW.GLFW_KEY_APOSTROPHE;
            case '-' -> GLFW.GLFW_KEY_MINUS;
            case '=' -> GLFW.GLFW_KEY_EQUAL;
            case '[' -> GLFW.GLFW_KEY_LEFT_BRACKET;
            case ']' -> GLFW.GLFW_KEY_RIGHT_BRACKET;
            case '\\' -> GLFW.GLFW_KEY_BACKSLASH;
            default -> Character.isLetter(c) ? (int) Character.toUpperCase(c) : -1;
        };
    }
}
