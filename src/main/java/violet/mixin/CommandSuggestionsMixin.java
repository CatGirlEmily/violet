package violet.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import violet.commands.CommandHandler;
import violet.features.misc.VioletCommands;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static violet.Main.mc;

@Mixin(CommandSuggestions.class)
public abstract class CommandSuggestionsMixin {

    @Shadow
    private @Nullable ParseResults<ClientSuggestionProvider> currentParse;

    @Shadow
    @Final
    private EditBox input;

    @Shadow
    private CommandSuggestions.SuggestionsList suggestions;

    @Shadow
    private boolean keepSuggestions;

    @Shadow
    private @Nullable CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow
    protected abstract void updateUsageInfo(ParseResults<ClientSuggestionProvider> currentParse, Suggestions suggestions);

    @Inject(
            method = "updateCommandInfo",
            at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z", remap = false),
            cancellable = true
    )
    public void onRefresh(CallbackInfo ci, @Local(name = "reader") StringReader reader) {
        if (!VioletCommands.instance.isActive()) return;

        char prefix = VioletCommands.getPrefix();

        if (reader.canRead() && reader.getString().charAt(reader.getCursor()) == prefix) {
            reader.setCursor(reader.getCursor() + 1);

            if (this.currentParse == null) {
                this.currentParse = CommandHandler.getDispatcher().parse(
                        reader,
                        Objects.requireNonNull(mc.getConnection()).getSuggestionsProvider()
                );
            }

            int cursor = input.getCursorPosition();
            if (cursor >= 1 && (this.suggestions == null || !this.keepSuggestions)) {
                this.pendingSuggestions = CommandHandler.getDispatcher().getCompletionSuggestions(this.currentParse, cursor);
                this.pendingSuggestions.thenAccept(suggestionResult -> {
                    if (this.pendingSuggestions.isDone()) {
                        this.updateUsageInfo(this.currentParse, suggestionResult);
                    }
                });
            }

            ci.cancel();
        }
    }
}