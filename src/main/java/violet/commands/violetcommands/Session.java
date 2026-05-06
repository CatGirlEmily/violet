package violet.commands.violetcommands;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static violet.Main.mc;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import violet.misc.Utils;

public class Session {
        public static void init(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("session").executes(context -> {
            mc.keyboard.setClipboard(mc.getSession().getAccessToken());
            Utils.info("Session ID coppied to the clipboard!");
            return SINGLE_SUCCESS;
        }));
    }
}
