package violet.commands.violetcommands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.world.level.GameType;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static violet.Main.mc;

public class Gm {
    public static void init(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<ClientSuggestionProvider>literal("gm")
                        .then(RequiredArgumentBuilder.<ClientSuggestionProvider, Integer>argument("mode", IntegerArgumentType.integer(0, 3))
                                .executes(ctx -> {
                                    int mode = IntegerArgumentType.getInteger(ctx, "mode");
                                    assert mc.gameMode != null;
                                    mc.gameMode.setLocalMode(GameType.byId(mode));
                                    return SINGLE_SUCCESS;
                                })
                        )
        );
    }
}