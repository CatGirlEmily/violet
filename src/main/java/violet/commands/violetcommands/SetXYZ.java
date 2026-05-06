package violet.commands.violetcommands;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import violet.misc.Utils;

import static violet.Main.mc;

public class SetXYZ {
    public static void init(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("setx")
            .then(argument("x", DoubleArgumentType.doubleArg())
                .executes(ctx -> {
                    Utils.setPlayerPos(DoubleArgumentType.getDouble(ctx, "x"),mc.player.getY(),mc.player.getZ());
                    return SINGLE_SUCCESS;
                })
            )
        );
        dispatcher.register(literal("sety")
            .then(argument("y", DoubleArgumentType.doubleArg())
                .executes(ctx -> {
                            Utils.setPlayerPos(mc.player.getX(), DoubleArgumentType.getDouble(ctx, "y"),mc.player.getZ());
                            return SINGLE_SUCCESS;
                })
            )
        );
        dispatcher.register(literal("setz")
            .then(argument("z", DoubleArgumentType.doubleArg())
                .executes(ctx -> {
                    Utils.setPlayerPos(mc.player.getX(),mc.player.getY(), DoubleArgumentType.getDouble(ctx, "z"));
                    return SINGLE_SUCCESS;
                })
            )
        );
    }
}

