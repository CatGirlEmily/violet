package violet.commands.violetcommands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static violet.Main.mc;

public class Rotate {
    public static void init(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<ClientSuggestionProvider>literal("rotation")
                        .then(LiteralArgumentBuilder.<ClientSuggestionProvider>literal("set")
                                .then(RequiredArgumentBuilder.<ClientSuggestionProvider, Float>argument("yaw", FloatArgumentType.floatArg(-180, 180))
                                        .then(RequiredArgumentBuilder.<ClientSuggestionProvider, Float>argument("pitch", FloatArgumentType.floatArg(-90, 90))
                                                .executes(ctx -> {
                                                    float yaw = FloatArgumentType.getFloat(ctx, "yaw");
                                                    float pitch = FloatArgumentType.getFloat(ctx, "pitch");
                                                    mc.player.setYRot(yaw);
                                                    mc.player.setXRot(pitch);
                                                    return SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                        .then(LiteralArgumentBuilder.<ClientSuggestionProvider>literal("add")
                                .then(RequiredArgumentBuilder.<ClientSuggestionProvider, Float>argument("yaw", FloatArgumentType.floatArg(-180, 180))
                                        .then(RequiredArgumentBuilder.<ClientSuggestionProvider, Float>argument("pitch", FloatArgumentType.floatArg(-90, 90))
                                                .executes(ctx -> {
                                                    float yaw = FloatArgumentType.getFloat(ctx, "yaw");
                                                    float pitch = FloatArgumentType.getFloat(ctx, "pitch");
                                                    mc.player.setYRot(mc.player.getYRot() + yaw);
                                                    mc.player.setXRot(mc.player.getXRot() + pitch);
                                                    return SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
        );
    }
}