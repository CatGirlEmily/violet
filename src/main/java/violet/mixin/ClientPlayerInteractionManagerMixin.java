package violet.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import violet.events.BlockBreakingCooldownEvent;

import static violet.Main.eventBus;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @Shadow private int blockBreakingCooldown;

    @Redirect(method = "updateBlockBreakingProgress", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", opcode = Opcodes.PUTFIELD, ordinal = 1), require = 0)
    private void creativeBreakDelayChange(ClientPlayerInteractionManager interactionManager, int value) {
        BlockBreakingCooldownEvent event = eventBus.post(new BlockBreakingCooldownEvent(value));
        blockBreakingCooldown = event.cooldown;
    }

    @Redirect(method = "updateBlockBreakingProgress", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", opcode = Opcodes.PUTFIELD, ordinal = 2), require = 0)
    private void survivalBreakDelayChange(ClientPlayerInteractionManager interactionManager, int value) {
        BlockBreakingCooldownEvent event = eventBus.post(new BlockBreakingCooldownEvent(value));
        blockBreakingCooldown = event.cooldown;
    }

    @Redirect(method = "attackBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", opcode = Opcodes.PUTFIELD), require = 0)
    private void attackBlockBreakDelayChange(ClientPlayerInteractionManager interactionManager, int value) {
        BlockBreakingCooldownEvent event = eventBus.post(new BlockBreakingCooldownEvent(value));
        blockBreakingCooldown = event.cooldown;
    }
}