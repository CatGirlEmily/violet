package violet.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import violet.features.misc.NoConfirmScreen;
import violet.features.player.SneakFix;

import java.util.ArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static violet.Main.mc;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onEntityTrackerUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/data/DataTracker;writeUpdatedEntries(Ljava/util/List;)V"))
    private void onPreTrackerUpdate(EntityTrackerUpdateS2CPacket packet, CallbackInfo ci, @Local Entity ent) {
        if (ent.equals(mc.player) && SneakFix.instance.isActive()) {
            for (DataTracker.SerializedEntry<?> entry : new ArrayList<>(packet.trackedValues())) {
                if (entry.handler().equals(TrackedDataHandlerRegistry.ENTITY_POSE)) {
                    packet.trackedValues().remove(entry);
                    break;
                }
            }
        }
    }

    @ModifyExpressionValue(method = "runClickEventCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;parseCommand(Ljava/lang/String;)Lnet/minecraft/client/network/ClientPlayNetworkHandler$CommandRunResult;"))
    private ClientPlayNetworkHandler.CommandRunResult onParseCommand(ClientPlayNetworkHandler.CommandRunResult original) {
        if (NoConfirmScreen.instance.isActive()) {
            return ClientPlayNetworkHandler.CommandRunResult.NO_ISSUES;
        }
        return original;
    }

    
}