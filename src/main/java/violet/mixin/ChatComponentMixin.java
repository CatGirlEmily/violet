package violet.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.components.ChatComponent;
import violet.features.chat.ChatPatches;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {

    @ModifyExpressionValue(method = "addMessageToQueue", at = @At(value = "CONSTANT", args = "intValue=100"))
    private int getLimit(int original) {
        if (ChatPatches.instance.isActive() && ChatPatches.extraLines.value()) {
            return ChatPatches.lines.value();
        }
        return original;
    }

    @ModifyExpressionValue(method = "addMessageToDisplayQueue", at = @At(value = "CONSTANT", args = "intValue=100"))
    private int getLimitVisible(int original) {
        if (ChatPatches.instance.isActive() && ChatPatches.extraLines.value()) {
            return ChatPatches.lines.value();
        }
        return original;
    }
}