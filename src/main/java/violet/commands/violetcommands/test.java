package violet.commands.violetcommands;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import violet.misc.Utils;

public class test {
    public static void init(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("test").executes(context -> {
            Utils.info("serverip: " + Utils.getServerIP());
            return SINGLE_SUCCESS;
        }));
    }
}
