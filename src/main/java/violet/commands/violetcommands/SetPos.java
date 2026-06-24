package violet.commands.violetcommands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import violet.misc.Utils;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class SetPos {
    public static void init(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<ClientSuggestionProvider>literal("setpos")
                        .then(RequiredArgumentBuilder.<ClientSuggestionProvider, Double>argument("x", DoubleArgumentType.doubleArg())
                                .then(RequiredArgumentBuilder.<ClientSuggestionProvider, Double>argument("y", DoubleArgumentType.doubleArg())
                                        .then(RequiredArgumentBuilder.<ClientSuggestionProvider, Double>argument("z", DoubleArgumentType.doubleArg())
                                                .executes(ctx -> {
                                                    Utils.setPlayerPos(
                                                            DoubleArgumentType.getDouble(ctx, "x"),
                                                            DoubleArgumentType.getDouble(ctx, "y"),
                                                            DoubleArgumentType.getDouble(ctx, "z")
                                                    );
                                                    return SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
        );
    }
}