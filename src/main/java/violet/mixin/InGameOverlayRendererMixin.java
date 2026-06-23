package violet.mixin;
import violet.features.render.LowFire;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class InGameOverlayRendererMixin {
	@Inject(method = "renderFire",at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
	private static void onRenderFireOverlay(PoseStack matrices, MultiBufferSource v, TextureAtlasSprite sprite, CallbackInfo ci) {
		if (LowFire.instance.isActive()) matrices.translate(0.0, -0.3, 0.0);
	}

	@Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
	private static void onRenderFireOverlay2(PoseStack matrices, MultiBufferSource v, TextureAtlasSprite sprite, CallbackInfo ci) {
		if (LowFire.noRender.value()) ci.cancel();
	}
}