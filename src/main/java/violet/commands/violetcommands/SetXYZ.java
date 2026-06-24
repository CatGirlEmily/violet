package violet.commands.violetcommands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import violet.misc.Utils;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static violet.Main.mc;

public class SetXYZ {
    public static void init(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        for (String axis : new String[]{"x", "y", "z"}) {
            dispatcher.register(
                    LiteralArgumentBuilder.<ClientSuggestionProvider>literal("set" + axis)
                            .then(RequiredArgumentBuilder.<ClientSuggestionProvider, Double>argument(axis, DoubleArgumentType.doubleArg())
                                    .executes(ctx -> {
                                        double val = DoubleArgumentType.getDouble(ctx, axis);
                                        Utils.setPlayerPos(
                                                axis.equals("x") ? val : mc.player.getX(),
                                                axis.equals("y") ? val : mc.player.getY(),
                                                axis.equals("z") ? val : mc.player.getZ()
                                        );
                                        return SINGLE_SUCCESS;
                                    })
                            )
            );
        }
    }
}