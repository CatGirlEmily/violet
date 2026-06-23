package violet.commands.violetcommands;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Mth;

import static violet.Main.mc;

public class Rotate {
    public static void init(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("rotate")
            .then(literal("set")
                .then(argument("yaw", FloatArgumentType.floatArg(-180, 180))
                    .executes(ctx -> {
                        mc.player.setYRot(FloatArgumentType.getFloat(ctx, "yaw"));
                        return SINGLE_SUCCESS;
                    })
                    .then(argument("pitch", FloatArgumentType.floatArg(-90, 90))
                        .executes(ctx -> {
                            mc.player.setYRot(FloatArgumentType.getFloat(ctx, "yaw"));
                            mc.player.setXRot(FloatArgumentType.getFloat(ctx, "pitch"));
                            return SINGLE_SUCCESS;
                        })
                    )
                )
            )
            .then(literal("add")
                .then(argument("yaw", FloatArgumentType.floatArg(-180, 180))
                    .executes(ctx -> {
                        mc.player.setYRot(Mth.wrapDegrees(mc.player.getYRot() + FloatArgumentType.getFloat(ctx, "yaw")));
                        return SINGLE_SUCCESS;
                    })
                    .then(argument("pitch", FloatArgumentType.floatArg(-90, 90))
                        .executes(ctx -> {
                            mc.player.setYRot(Mth.wrapDegrees(mc.player.getYRot() + FloatArgumentType.getFloat(ctx, "yaw")));
                            float pitch = mc.player.getXRot() + FloatArgumentType.getFloat(ctx, "pitch");
                            mc.player.setXRot(pitch >= 0 ? Math.min(pitch, 90) : Math.max(pitch, -90));
                            return SINGLE_SUCCESS;
                        })
                    )
                )
            )
        );
    }
}