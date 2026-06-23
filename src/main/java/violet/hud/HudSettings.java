package violet.hud;

import io.wispforest.owo.ui.container.FlowLayout;
import violet.hud.clickgui.Settings;

import java.util.List;

import static violet.Main.mc;

public class HudSettings extends Settings {
    public HudSettings(List<FlowLayout> settings) {
        super(settings);
    }

    @Override
    public void onClose() {
        mc.setScreen(new HudEditorScreen());
    }
}
