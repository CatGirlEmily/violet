package violet.misc;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.RegistryKeys;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class IdentifierArgumentType implements ArgumentType<String> {

    public static IdentifierArgumentType identifier() {
        return new IdentifierArgumentType();
    }

    public static String getId(CommandContext<FabricClientCommandSource> ctx, String name) {
        return ctx.getArgument(name, String.class);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        StringBuilder result = new StringBuilder();
        while (reader.canRead() && !Character.isWhitespace(reader.peek())) {
            result.append(reader.read());
        }
        return result.toString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> ctx, SuggestionsBuilder builder) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return builder.buildFuture();

        List<String> ids = client.world.getRegistryManager()
            .getOrThrow(RegistryKeys.ENCHANTMENT)
            .streamKeys()
            .map(key -> key.getValue().toString())
            .toList();

        return CommandSource.suggestMatching(ids, builder);
    }
}