package violet.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import violet.events.HudRenderEvent;
import violet.features.chat.ChatPatches;
import violet.hud.HudManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static violet.Main.eventBus;
import static violet.Main.mc;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!mc.options.hudHidden) {
            eventBus.post(new HudRenderEvent(context, this.getTextRenderer(), tickCounter));
        }
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;<init>(Lnet/minecraft/client/MinecraftClient;)V"))
    private void onInit(MinecraftClient client, CallbackInfo ci) {
        HudManager.registerElements();
    }

    @WrapWithCondition(method = "clear", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;clear(Z)V"))
    private boolean shouldClearChat(ChatHud instance, boolean clearHistory) {
        return !(ChatPatches.instance.isActive() && ChatPatches.keepHistory.value());
    }
}
