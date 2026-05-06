package violet.commands.violetcommands;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import violet.misc.Utils;

public class Say {
    public static void init(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("say")
            .then(argument("message", StringArgumentType.greedyString())
                .executes(ctx -> {
                    String message = ctx.getArgument("message", String.class);

                    if (message != null) Utils.sendMessage(message);
                    return SINGLE_SUCCESS;
                })
            )
        );
    }
}
