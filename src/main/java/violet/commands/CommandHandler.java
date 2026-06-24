package violet.commands;

import static violet.Main.mc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import violet.commands.violetcommands.*;
import violet.features.misc.VioletCommands;

import java.util.Objects;

public class CommandHandler {
    private static final CommandDispatcher<ClientSuggestionProvider> dispatcher = new CommandDispatcher<>();

    public static void init() {
        Crash.init(dispatcher);
        VClip.init(dispatcher);
        Rotation.init(dispatcher);
        Say.init(dispatcher);
        Session.init(dispatcher);
        Silly.init(dispatcher);
        HClip.init(dispatcher);
        SetPos.init(dispatcher);
        SetXYZ.init(dispatcher);
        Gm.init(dispatcher);
        Timer.init(dispatcher);
    }

    // true if parsed as command
    public static boolean handle(String message, boolean addToList) {
        if (!VioletCommands.instance.isActive()) return false;
        if (message.isEmpty() || message.charAt(0) != VioletCommands.getPrefix()) return false;

        String input = message.substring(1);
        Minecraft client = Minecraft.getInstance();
        FabricClientCommandSource source = (FabricClientCommandSource) Objects.requireNonNull(client.getConnection()).getSuggestionsProvider();

        try {
            dispatcher.execute(input, (ClientSuggestionProvider) source);
        } catch (CommandSyntaxException e) {
            violet.misc.Utils.info(e.getMessage());
        }

        if (VioletCommands.addToHistory.value() && addToList) mc.gui.getChat().addRecentChat(VioletCommands.getPrefix() + input);
        return true;
    }

    public static boolean handle(String message) {
        return handle(message, true);
    }

    public static CommandDispatcher<ClientSuggestionProvider> getDispatcher() {
        return dispatcher;
    }
}