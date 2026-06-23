package violet.hud.elements;

import io.wispforest.owo.ui.core.OwoUIGraphics;
import violet.config.Feature;
import violet.config.SettingBool;
import violet.hud.SimpleTextElement;
import violet.hud.clickgui.Settings;
import violet.misc.Utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.network.chat.Component;

public class Ping extends SimpleTextElement {
    public final SettingBool average = new SettingBool(false, "average", instance);
    public int ticks = 20;
    public long lastPing = 0;
    public List<Long> pingList = new CopyOnWriteArrayList<>();

    public Ping(String text) {
        super(Component.literal(text), new Feature("pingElement"), "Ping Display");
        this.options = this.getBaseSettings(List.of(
                new Settings.Toggle("Average", average, "Tracks and adds your average ping to the element.")
        ));
        this.setDesc("Displays your ping.");
        this.setCategory(Category.Info);
    }

    @Override
    public void draw(OwoUIGraphics context, int mouseX, int mouseY, float partialTicks, float delta) {
        if (this.shouldRender()) {
            if (average.value() && !this.pingList.isEmpty()) {
                long avg = 0;
                for (long previous : this.pingList) {
                    avg += previous;
                }
                this.setText(Utils.format("Ping: §f{}ms §7{}ms", this.lastPing, avg / this.pingList.size()));
            } else {
                this.setText(Utils.format("Ping: §f{}ms", this.lastPing));
            }
            super.draw(context, mouseX, mouseY, partialTicks, delta);
        }
    }

    public void setPing(long ping) {
        if (this.pingList.size() > 30) {
            this.pingList.removeFirst();
        }
        this.lastPing = ping;
        this.pingList.add(ping);
    }

    public void reset() {
        this.ticks = 20;
        this.lastPing = 0;
        this.pingList.clear();
    }
}
