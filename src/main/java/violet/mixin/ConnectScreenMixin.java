package violet.mixin;

import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import violet.Main;
import violet.events.ServerConnectBeginEvent;
import violet.features.misc.ReconnectButton;

@Mixin(ConnectScreen.class)
public abstract class ConnectScreenMixin {

    @Inject(method = "connect(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/multiplayer/resolver/ServerAddress;Lnet/minecraft/client/multiplayer/ServerData;Lnet/minecraft/client/multiplayer/TransferState;)V", at = @At("HEAD"))
    private void tryConnectEvent(Minecraft minecraft, ServerAddress hostAndPort, ServerData server, TransferState transferState, CallbackInfo ci) {
        Main.eventBus.post(new ServerConnectBeginEvent(hostAndPort, server)); // im gonna look into this later but seems like the event doesnt work
        ReconnectButton.lastServerConnection = new ObjectObjectImmutablePair<>(hostAndPort, server);
    }
}
