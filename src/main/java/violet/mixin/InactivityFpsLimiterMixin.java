package violet.mixin;

import static violet.Main.mc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.option.InactivityFpsLimiter;
import violet.features.misc.NoFpsLimiter;

@Mixin(InactivityFpsLimiter.class)
public class InactivityFpsLimiterMixin {

   @Inject(method = "update", at = @At("RETURN"), cancellable = true)
   private void onUpdate(CallbackInfoReturnable<Integer> cir) {
       if (!NoFpsLimiter.instance.isActive()) return;
       cir.setReturnValue(mc.options.getMaxFps().getValue());
   }
}