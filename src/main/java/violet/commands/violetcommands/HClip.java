package violet.commands.violetcommands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.world.phys.Vec3;
import violet.misc.Utils;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static violet.Main.mc;

public class HClip {
    public static void init(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<ClientSuggestionProvider>literal("hclip")
                        .then(RequiredArgumentBuilder.<ClientSuggestionProvider, Double>argument("blocks", DoubleArgumentType.doubleArg())
                                .executes(ctx -> {
                                    double blocks = ctx.getArgument("blocks", Double.class);
                                    Vec3 forward = Vec3.directionFromRotation(0, mc.player.getYRot()).normalize();
                                    Utils.setPlayerPos(mc.player.getX() + forward.x * blocks, mc.player.getY(), mc.player.getZ() + forward.z * blocks);
                                    return SINGLE_SUCCESS;
                                })
                        )
        );
    }
}