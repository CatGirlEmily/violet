package violet;

import io.wispforest.owo.config.ui.ConfigScreenProviders;
import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import violet.commands.EnchantVCommand;
import violet.commands.VioletCommand;
import violet.commands.violetcommands._CommandHandler;
import violet.config.Config;
import violet.events.ChatMsgEvent;
import violet.events.OverlayMsgEvent;
import violet.features.chat.ChatPatches;
import violet.features.chat.ChatRules;
import violet.features.chat.CommandAliases;
import violet.features.misc.ClickGuiFeature;
import violet.features.misc.CommandKeybinds;
import violet.features.misc.NoServerPack;
import violet.features.misc.VioletCommands;
import violet.features.movement.AutoSprint;
import violet.features.movement.NoJumpCooldown;
import violet.features.player.BreakDelay;
import violet.features.render.Fullbright;
import violet.features.render.TimeChanger;
import violet.features.render.Zoom;
import violet.hud.HudManager;
import violet.hud.clickgui.ClickGui;
import violet.misc.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.CommandDispatcher;

import java.lang.invoke.MethodHandles;

public class Main implements ModInitializer {
    public static final String MOD_ID = "violet";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final IEventBus eventBus = new EventBus();

    public static Minecraft mc;

    public static void injectRenderDoc() {
        String path = System.getProperty("violet.renderdoc.library_path");
        if (path != null) {
            try {
                System.load(path);
                LOGGER.info("Loaded RenderDoc lib: {}", path);
            } catch (Exception exception) {
                LOGGER.error("Failed to load RenderDoc lib.", exception);
            }
        }
    }

    public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext access) {
        VioletCommand.init(dispatcher);
        EnchantVCommand.init(dispatcher);
        CommandAliases.init(dispatcher);
    }

    @Override
    public void onInitialize() {
        mc = Minecraft.getInstance();
        injectRenderDoc();
        if (Config.isNew()) firstLaunch();
        Config.load();
        ConfigScreenProviders.register(MOD_ID, screen -> new ClickGui());
        _CommandHandler.init();
        ClientCommandRegistrationCallback.EVENT.register(Main::registerCommands);
    
        eventBus.registerLambdaFactory(MOD_ID, (lookupInMethod, glass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, glass, MethodHandles.lookup()));

        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            String msg = Utils.toPlain(message);

            if (overlay) return !eventBus.post(new OverlayMsgEvent(message, msg)).isCancelled();

            boolean cancelled = eventBus.post(new ChatMsgEvent(message, msg)).isCancelled();
            return !cancelled;
        });

        eventBus.subscribe(ClickGuiFeature.class);
        eventBus.subscribe(HudManager.class);

        eventBus.subscribe(Fullbright.class);
        //eventBus.subscribe(NBTTooltip.class);
        eventBus.subscribe(Zoom.class);
        eventBus.subscribe(TimeChanger.class);

        eventBus.subscribe(AutoSprint.class);
        eventBus.subscribe(NoJumpCooldown.class);
        eventBus.subscribe(BreakDelay.class);
        
        eventBus.subscribe(ChatPatches.class);
        eventBus.subscribe(ChatRules.class);
        eventBus.subscribe(CommandKeybinds.class);
        eventBus.subscribe(VioletCommands.class);
        eventBus.subscribe(NoServerPack.class);
    }

    private void firstLaunch() {
        VioletCommands.instance.setActive(true);
    }
}