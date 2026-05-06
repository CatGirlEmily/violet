package violet.features;

import static violet.Main.mc;

import org.lwjgl.glfw.GLFW;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.Screen;
import violet.config.Feature;
import violet.config.SettingBool;
import violet.config.SettingKeybind;
import violet.events.InputEvent;
import violet.hud.clickgui.ClickGui;
import violet.misc.Utils;

public class ClickGuiFeature {
    public static final Feature instance = new Feature("clickgui");

    public static final SettingKeybind openKey = new SettingKeybind(GLFW.GLFW_KEY_RIGHT_SHIFT, "openKey", instance);
    public static final SettingBool closeIfOpen = new SettingBool(true, "closeIfOpen", instance);

    @EventHandler
    private static void onKey(InputEvent event) {
        if (openKey.isKey(event.key) && event.action == GLFW.GLFW_PRESS) {               // on keybind press
            Screen cScreen = mc.currentScreen;
            
            if (cScreen instanceof ClickGui && closeIfOpen.value()) {
                cScreen.close();
            } else if (cScreen == null) {
                Utils.setScreen(new ClickGui());
            }
        }       
    }
}

