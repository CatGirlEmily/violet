package violet.commands.violetcommands;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.world.phys.Vec3;
import violet.misc.Utils;

import static violet.Main.mc;

public class HClip {
    public static void init(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("hclip")
            .then(argument("blocks", DoubleArgumentType.doubleArg())
                .executes(ctx -> {
                    double blocks = ctx.getArgument("blocks", Double.class);
                    Vec3 forward = Vec3.directionFromRotation(0, mc.player.getYRot()).normalize();
                    
                    Utils.setPlayerPos(mc.player.getX() + forward.x * blocks, mc.player.getY(), mc.player.getZ() + forward.z * blocks);
                    return SINGLE_SUCCESS;
                })
            )
        );
    }
}