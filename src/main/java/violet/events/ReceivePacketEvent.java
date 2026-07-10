package violet.events;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;

public class ReceivePacketEvent extends Cancellable {
    public Packet<?> packet;
    public Connection connection;

    public ReceivePacketEvent(Packet<?> packet, Connection connection) {
        this.setCancelled(false);
        this.packet = packet;
        this.connection = connection;
    }
}
