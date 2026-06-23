package violet.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import violet.events.HudRenderEvent;
import violet.features.chat.ChatPatches;
import violet.features.render.HeldItemTooltip;
import violet.hud.HudManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static violet.Main.eventBus;
import static violet.Main.mc;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    public abstract Font getFont();

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void onRender(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!mc.options.hideGui) {
            eventBus.post(new HudRenderEvent(graphics, this.getFont(), deltaTracker));
        }
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;<init>(Lnet/minecraft/client/Minecraft;)V"))
    private void onInit(Minecraft client, CallbackInfo ci) {
        HudManager.registerElements();
    }

    @WrapWithCondition(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;clearMessages(Z)V"))
    private boolean shouldClearChat(ChatComponent instance, boolean clearHistory) {
        return !(ChatPatches.instance.isActive() && ChatPatches.keepHistory.value());
    }

    @Inject(method = "extractSelectedItemName", at = @At("HEAD"), cancellable = true)
    private void onRenderSelectedItemName(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (HeldItemTooltip.instance.isActive()) ci.cancel();
    }
}
