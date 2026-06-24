package violet.commands.violetcommands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Crash {
    public static void init(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ClientSuggestionProvider>literal("crash")
                .executes(context -> {
                    Runtime.getRuntime().halt(0);
                    return SINGLE_SUCCESS;
                })
        );
    }
}