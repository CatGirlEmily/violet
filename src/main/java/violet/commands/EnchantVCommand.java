package violet.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameType;
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
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null) return;

        if (client.gameMode == null ||
            client.gameMode.getPlayerMode() != GameType.CREATIVE) {
            Utils.info("Requires gamemode 1");
            return;
        }

        ItemStack stack = player.getMainHandItem();
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

        HolderLookup.Provider registries = client.level.registryAccess();
        HolderLookup<Enchantment> enchantRegistry = registries.lookupOrThrow(Registries.ENCHANTMENT);

        var enchantOpt = enchantRegistry.get(
            net.minecraft.resources.ResourceKey.create(Registries.ENCHANTMENT, id)
        );

        if (enchantOpt.isEmpty()) {
            Utils.info("§cUnknown enchantment: " + enchantId);
            return;
        }

        Holder<Enchantment> enchantEntry = enchantOpt.get();

        ItemEnchantments current = stack.getOrDefault(
            DataComponents.ENCHANTMENTS,
            ItemEnchantments.EMPTY
        );

        ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(current);
        builder.set(enchantEntry, level);
        stack.set(DataComponents.ENCHANTMENTS, builder.toImmutable());

        int rawSlot = player.getInventory().getSelectedSlot() + 36;
        player.connection.send(new ServerboundSetCreativeModeSlotPacket(rawSlot, stack));
        Utils.info("Applied enchantment §7" + enchantId + " " + level);
    }
}