package violet.mixin;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import violet.features.render.Zoom;

@Mixin(Camera.class)
public class CameraMixin {
    @ModifyReturnValue(method = "getFov", at = @At("RETURN"))
    private float modifyFov(float original) {
        if (Zoom.zoomedIn) return Zoom.targetFov;
        return original;
    }
}