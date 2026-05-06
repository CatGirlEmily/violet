package violet.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.hud.ChatHud;
import violet.features.misc.ChatPatches;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @ModifyExpressionValue(method = "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", at = @At(value = "CONSTANT", args = "intValue=100"))
    private int getLimit(int original) {
        if (ChatPatches.instance.isActive() && ChatPatches.extraLines.value()) {
            return ChatPatches.lines.value();
        }
        return original;
    }

    @ModifyExpressionValue(method = "addVisibleMessage", at = @At(value = "CONSTANT", args = "intValue=100"))
    private int getLimitVisible(int original) {
        if (ChatPatches.instance.isActive() && ChatPatches.extraLines.value()) {
            return ChatPatches.lines.value();
        }
        return original;
    }
}