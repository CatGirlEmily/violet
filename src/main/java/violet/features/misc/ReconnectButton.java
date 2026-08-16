package violet.features.misc;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import violet.Main;
import violet.config.Feature;
import violet.config.SettingBool;
import violet.config.SettingDouble;
import violet.events.ServerConnectBeginEvent;

public class ReconnectButton {
    public static final Feature instance = new Feature("reconnectButton");
    public static final String tooltip = "Adds a reconnect button to disconnect screen";

    public static final SettingBool autoReconnect = new SettingBool(false, "autoReconnect", instance);
    public static final SettingDouble autoReconnectTime = new SettingDouble(3.0, "autoReconnectTime", instance);


    public static Pair<ServerAddress, ServerData> lastServerConnection;

    @EventHandler
    private void OnServerConnectBegin(ServerConnectBeginEvent event) {
        Main.LOGGER.info("lastconnection updated! " + lastServerConnection);
        lastServerConnection = new ObjectObjectImmutablePair<>(event.hostAndPort, event.server);
    }
}
