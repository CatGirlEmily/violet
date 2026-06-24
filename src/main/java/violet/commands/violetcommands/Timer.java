package violet.commands.violetcommands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import violet.misc.Utils;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static violet.Main.mc;

public class Timer {
    private static Thread timerThread = null;

    public static void init(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<ClientSuggestionProvider>literal("timer")
                        .then(LiteralArgumentBuilder.<ClientSuggestionProvider>literal("start")
                                .then(RequiredArgumentBuilder.<ClientSuggestionProvider, Integer>argument("seconds", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            int seconds = IntegerArgumentType.getInteger(ctx, "seconds");

                                            if (timerThread != null && timerThread.isAlive())
                                                timerThread.interrupt();

                                            timerThread = new Thread(() -> {
                                                try {
                                                    Utils.info("Timer started: " + seconds + "s");
                                                    for (int i = seconds; i > 0; i--) {
                                                        Thread.sleep(1000);
                                                    }
                                                    if (mc.player != null) {
                                                        Utils.info("Timer finished!");
                                                        Utils.showTitle("Timer Finished","",5,40,5);
                                                        Utils.playSound(SoundEvents.ANVIL_LAND, 1, 1f);
                                                    }
                                                } catch (InterruptedException ignored) {}
                                            });
                                            timerThread.setDaemon(true);
                                            timerThread.start();

                                            return SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(LiteralArgumentBuilder.<ClientSuggestionProvider>literal("stop")
                                .executes(ctx -> {
                                    if (timerThread != null && timerThread.isAlive()) {
                                        timerThread.interrupt();
                                        Utils.info("Timer stopped.");
                                    } else {
                                        Utils.info("No timer running.");
                                    }
                                    return SINGLE_SUCCESS;
                                })
                        )
        );
    }
}