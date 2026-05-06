package violet.mixin;

import static violet.Main.mc;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.math.MathHelper;
import violet.features.render.DebugScreen;

@Mixin(DebugHud.class)
public class DebugHudMixin {

    @Inject(method = "drawText", at = @At("HEAD"), cancellable = true)
    private void onDrawText(DrawContext context, List<String> lines, boolean left, CallbackInfo ci) {
        if (!DebugScreen.instance.isActive()) return;
        lines.replaceAll(line -> {
            if (mc.player == null) return line;

            // facing
            if (line.startsWith("Facing:")) return "Facing: %s (%%.%df / %%.%df)".formatted(
                mc.player.getHorizontalFacing(),
                DebugScreen.facingPrecision.value(),
                DebugScreen.facingPrecision.value()
            ).formatted(
                MathHelper.wrapDegrees(mc.player.getYaw()),
                mc.player.getPitch()
            );

            // xyz
            if (line.startsWith("XYZ:")) return "XYZ: %%.%df / %%.%df / %%.%df".formatted(
                DebugScreen.xyzPrecision.value(),
                DebugScreen.xyzPrecision.value(),
                DebugScreen.xyzPrecision.value()
            ).formatted(
                mc.player.getX(),
                mc.player.getY(),
                mc.player.getZ()
            );
            return line;
        });
    }
}