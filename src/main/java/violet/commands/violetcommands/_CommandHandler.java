package violet.commands.violetcommands;

import static violet.Main.mc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import violet.features.misc.VioletCommands;

public class _CommandHandler {
    private static CommandDispatcher<FabricClientCommandSource> dispatcher = new CommandDispatcher<>();

    public static void init() {
        Silly.init(dispatcher);
        Crash.init(dispatcher);
        SetPos.init(dispatcher);
        SetXYZ.init(dispatcher);
        VClip.init(dispatcher);
        HClip.init(dispatcher);
        Rotate.init(dispatcher);
        Say.init(dispatcher);
        Session.init(dispatcher);
    }

    // true if parsed as command
    public static boolean handle(String message) {
        if (!VioletCommands.instance.isActive()) return false;
        if (message.isEmpty() || message.charAt(0) != VioletCommands.getPrefix()) return false;

        String input = message.substring(1);
        MinecraftClient client = MinecraftClient.getInstance();
        FabricClientCommandSource source = (FabricClientCommandSource) client.getNetworkHandler().getCommandSource();

        try {
            dispatcher.execute(input, source);
        } catch (CommandSyntaxException e) {
            violet.misc.Utils.info(e.getMessage());
        }

        if (VioletCommands.addToHistory.value()) mc.inGameHud.getChatHud().addToMessageHistory(VioletCommands.getPrefix() + input);
        return true;
    }
}