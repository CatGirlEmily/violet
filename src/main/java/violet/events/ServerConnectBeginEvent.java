package violet.events;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;

public class ServerConnectBeginEvent {
    public ServerAddress hostAndPort;
    public ServerData server;

    public ServerConnectBeginEvent(ServerAddress hostAndPort, ServerData server) {
        this.hostAndPort = hostAndPort;
        this.server = server;
    }
}
