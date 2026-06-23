package violet.commands.violetcommands;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class Crash {
    public static void init(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("crash").executes(context -> {
            //mc.scheduleStop();    <- works just like pressing alt+f4 / X button
            Runtime.getRuntime().halt(0);
            return SINGLE_SUCCESS;
        }));
    }
}
