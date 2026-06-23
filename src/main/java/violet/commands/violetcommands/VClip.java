package violet.commands.violetcommands;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import violet.misc.Utils;

import static violet.Main.mc;

public class VClip {
    public static void init(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("vclip")
            .then(argument("blocks", DoubleArgumentType.doubleArg())
                .executes(ctx -> {
                    double blocks = DoubleArgumentType.getDouble(ctx, "blocks");

                    // sometimes works sometimes doesnt...
                    int packetsRequired = (int) Math.ceil(Math.abs(blocks / 10));
                    if (packetsRequired > 20) packetsRequired = 1;

                    ////////////// vehicle
                    if (mc.player.isPassenger()) {
                        for (int i = 0; i < packetsRequired - 1; i++) {
                            mc.player.connection.send(ServerboundMoveVehiclePacket.fromEntity(mc.player.getVehicle()));
                        }
                        mc.player.getVehicle().setPos(mc.player.getVehicle().getX(),mc.player.getVehicle().getY() + blocks,mc.player.getVehicle().getZ());
                        mc.player.connection.send(ServerboundMoveVehiclePacket.fromEntity(mc.player.getVehicle()));
                    } else {
                    //////////// player
                        for (int i = 0; i < packetsRequired - 1; i++) {
                            mc.player.connection.send(new ServerboundMovePlayerPacket.StatusOnly(true, mc.player.horizontalCollision));
                        }
                        Utils.setPlayerPos(mc.player.getX(), mc.player.getY() + blocks, mc.player.getZ());
                    }

                    return SINGLE_SUCCESS;
                })
            )
        );
    }
}