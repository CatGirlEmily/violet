package violet.features.misc;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import violet.config.Feature;
import violet.config.SettingBool;
import violet.events.ReceivePacketEvent;
import violet.misc.Utils;

public class NoServerPack {
    public static final Feature instance = new Feature("noServerPack");

    public static final SettingBool dump = new SettingBool(false, "dump", instance.key());

    @EventHandler
    private static void onPacketReceive(ReceivePacketEvent event) {
        if (event.packet instanceof ResourcePackSendS2CPacket && instance.isActive()) {
            event.cancel();
        }
        if (dump.value()) Utils.info("packet info");
    }
}
