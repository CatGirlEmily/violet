package violet.commands.violetcommands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import violet.misc.Utils;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static violet.Main.mc;

public class Silly {

    public static boolean silly = false;

    public static void init(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ClientSuggestionProvider>literal("session")
                .executes(context -> {
                    silly = !silly;
                    Utils.info("Silly mode: " + silly);
                    return SINGLE_SUCCESS;
                })
        );
    }
}