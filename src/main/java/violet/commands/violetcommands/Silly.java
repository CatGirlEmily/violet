package violet.commands.violetcommands;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import violet.misc.Utils;

public class Silly {
    public static boolean silly = false;

    public static void init(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("silly").executes(context -> {
            silly = !silly;
            Utils.info("Silly mode (currently allows for stupid viewmodel modifications): " + silly);
            return SINGLE_SUCCESS;
        }));
    }
}
