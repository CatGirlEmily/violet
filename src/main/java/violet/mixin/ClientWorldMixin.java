package violet.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import violet.events.EntityRemovedEvent;
import violet.events.WorldTickEvent;
import violet.features.render.NoBlockBreakParticles;

//import violet.features.general.NoBlockBreakParticles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static violet.Main.eventBus;
import static violet.Main.mc;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World {

    protected ClientWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onWorldTick(CallbackInfo ci) {
        if (mc.player != null) {
            eventBus.post(new WorldTickEvent());
        }
    }

    @Inject(method = "removeEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setRemoved(Lnet/minecraft/entity/Entity$RemovalReason;)V"))
    private void onBeforeRemoveEntity(int entityId, Entity.RemovalReason removalReason, CallbackInfo ci, @Local Entity ent) {
        eventBus.post(new EntityRemovedEvent(ent, removalReason));
    }


    @Inject(method = "addBlockBreakParticles", at = @At("HEAD"), cancellable = true)
    private void onBreakParticle(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (NoBlockBreakParticles.instance.isActive()) {
            ci.cancel();
        }
    }

    @Inject(method = "spawnBlockBreakingParticle", at = @At("HEAD"), cancellable = true)
    private void onBreakingParticle(BlockPos
        pos, Direction direction, CallbackInfo ci) {
        if (NoBlockBreakParticles.instance.isActive()) {
            ci.cancel();
        }
    }

}