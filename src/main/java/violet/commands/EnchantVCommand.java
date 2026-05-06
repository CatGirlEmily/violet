package violet.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import violet.misc.Utils;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static violet.misc.IdentifierArgumentType.getId;
import static violet.misc.IdentifierArgumentType.identifier;

public class EnchantVCommand {

    public static void init(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("enchantv")
            .then(argument("enchantid", identifier())
                // only id with auto complete lvl
                .executes(ctx -> {
                    String enchantId = getId(ctx, "enchantid");
                    applyEnchant(enchantId, 1);
                    return SINGLE_SUCCESS;
                })
                // id + level
                .then(argument("level", IntegerArgumentType.integer())
                    .executes(ctx -> {
                        String enchantId = getId(ctx, "enchantid");
                        int level = IntegerArgumentType.getInteger(ctx, "level");
                        applyEnchant(enchantId, level);
                        return SINGLE_SUCCESS;
                    })
                )
            )
        );
    }

    private static void applyEnchant(String enchantId, int level) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        if (client.interactionManager == null ||
            client.interactionManager.getCurrentGameMode() != GameMode.CREATIVE) {
            Utils.info("Requires gamemode 1");
            return;
        }

        ItemStack stack = player.getMainHandStack();
        if (stack.isEmpty()) {
            Utils.info("§cYou are not holding any item.");
            return;
        }

        Identifier id = Identifier.tryParse(enchantId);
        if (id == null) id = Identifier.tryParse("minecraft:" + enchantId);
        if (id == null) {
            Utils.info("§cInvalid enchantment id: " + enchantId);
            return;
        }

        RegistryWrapper.WrapperLookup registries = client.world.getRegistryManager();
        RegistryWrapper<Enchantment> enchantRegistry = registries.getOrThrow(RegistryKeys.ENCHANTMENT);

        var enchantOpt = enchantRegistry.getOptional(
            net.minecraft.registry.RegistryKey.of(RegistryKeys.ENCHANTMENT, id)
        );

        if (enchantOpt.isEmpty()) {
            Utils.info("§cUnknown enchantment: " + enchantId);
            return;
        }

        RegistryEntry<Enchantment> enchantEntry = enchantOpt.get();

        ItemEnchantmentsComponent current = stack.getOrDefault(
            DataComponentTypes.ENCHANTMENTS,
            ItemEnchantmentsComponent.DEFAULT
        );

        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(current);
        builder.set(enchantEntry, level);
        stack.set(DataComponentTypes.ENCHANTMENTS, builder.build());

        int rawSlot = player.getInventory().getSelectedSlot() + 36;
        player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(rawSlot, stack));
        Utils.info("Applied enchantment §7" + enchantId + " " + level);
    }
}