package violet.mixin;

import static violet.Main.mc;

import com.mojang.blaze3d.platform.FramerateLimitTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import violet.features.misc.NoFpsLimiter;

@Mixin(FramerateLimitTracker.class)
public class FramerateLimitTrackerMixin {

   @Inject(method = "getFramerateLimit", at = @At("RETURN"), cancellable = true)
   private void onUpdate(CallbackInfoReturnable<Integer> cir) {
       if (!NoFpsLimiter.instance.isActive()) return;
       cir.setReturnValue(mc.options.framerateLimit().get());
   }
}