package violet.commands.violetcommands;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import violet.misc.Utils;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static violet.Main.mc;

public class Session {
    public static void init(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ClientSuggestionProvider>literal("session")
                .executes(context -> {
                    mc.keyboardHandler.setClipboard(mc.getUser().getAccessToken());
                    Utils.info("Session ID coppied to the clipboard!");
                    return SINGLE_SUCCESS;
                })
        );
    }
}