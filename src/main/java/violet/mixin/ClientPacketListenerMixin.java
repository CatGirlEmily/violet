package violet.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import violet.features.misc.NoConfirmScreen;
import violet.features.player.SneakFix;

import java.util.ArrayList;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static violet.Main.mc;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "handleSetEntityData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;assignValues(Ljava/util/List;)V"))
    private void onPreTrackerUpdate(ClientboundSetEntityDataPacket packet, CallbackInfo ci, @Local Entity entity) {
        if (entity.equals(mc.player) && SneakFix.instance.isActive()) {
            for (SynchedEntityData.DataValue<?> entry : new ArrayList<>(packet.packedItems())) {
                if (entry.serializer().equals(EntityDataSerializers.POSE)) {
                    packet.packedItems().remove(entry);
                    break;
                }
            }
        }
    }

    @ModifyExpressionValue(method = "sendUnattendedCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;verifyCommand(Ljava/lang/String;)Lnet/minecraft/client/multiplayer/ClientPacketListener$CommandCheckResult;"))
    private ClientPacketListener.CommandCheckResult onParseCommand(ClientPacketListener.CommandCheckResult original) {
        if (NoConfirmScreen.instance.isActive()) {
            return ClientPacketListener.CommandCheckResult.NO_ISSUES;
        }
        return original;
    }

    
}