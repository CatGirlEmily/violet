package violet.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import violet.hud.clickgui.ClickGui;
import violet.misc.Utils;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class VioletCommand {
    public static void init(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("violet").executes(context -> {
            Utils.setScreen(new ClickGui());
            return SINGLE_SUCCESS;
        }));
    }
}

