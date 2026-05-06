package violet.features.render;

import static violet.Main.mc;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import violet.config.Feature;
import violet.config.SettingInt;
import violet.events.ReceivePacketEvent;

public class TimeChanger {
    public static final Feature instance = new Feature("timeChanger");
    public static final SettingInt time = new SettingInt(12000, "time", instance);

    @EventHandler
    private static void onPacketReceive(ReceivePacketEvent event) {
        if (instance.isActive() && (event.packet instanceof WorldTimeUpdateS2CPacket)) {
            event.cancel();
            if (mc.world != null) mc.world.setTime(time.get().getAsLong(), time.get().getAsLong(), false);
        }
    }
}
