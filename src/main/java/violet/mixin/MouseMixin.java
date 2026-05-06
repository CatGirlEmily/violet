package violet.mixin;

import net.minecraft.client.Mouse;
import net.minecraft.client.input.MouseInput;
import net.minecraft.entity.player.PlayerInventory;
import violet.events.InputEvent;
import violet.features.player.HotbarScroll;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import static violet.Main.eventBus;

@Mixin(Mouse.class)
public abstract class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, MouseInput input, int action, CallbackInfo ci) {
        if (eventBus.post(new InputEvent(input, action)).isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;setSelectedSlot(I)V"), cancellable = true)
    private void onBeforeSetSlot(long window, double horizontal, double vertical, CallbackInfo ci, @Local PlayerInventory inv) {
        if (!HotbarScroll.instance.isActive()) return;
        
        if (HotbarScroll.lockScroll.value()) ci.cancel();
        else if (HotbarScroll.noOverflow.value()) {
            int selected = inv.getSelectedSlot();
            if (selected == 0 && (horizontal < 0.0 || vertical > 0.0)) {
                ci.cancel();
            } else if (selected == 8 && (horizontal > 0.0 || vertical < 0.0)) {
                ci.cancel();
            }
        }
    }
}
