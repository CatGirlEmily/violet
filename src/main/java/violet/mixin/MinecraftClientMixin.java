package violet.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import violet.features.player.UseDelay;

@Mixin(value = MinecraftClient.class)
public class MinecraftClientMixin {
    
    @Shadow
    private int itemUseCooldown;

    @Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemEnabled(Lnet/minecraft/resource/featuretoggle/FeatureSet;)Z"))
    private void onDoItemUseHand(CallbackInfo ci, @Local ItemStack itemStack) {
        if (UseDelay.instance.isActive()) itemUseCooldown = 0;
    }
}
