package violet.features.misc;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import violet.config.Feature;
import violet.config.SettingBool;
import violet.events.ReceivePacketEvent;
import violet.events.ServerTickEvent;
import violet.misc.Utils;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

public class NoServerPack {
    public static final Feature instance = new Feature("noServerPack");
    public static final SettingBool dump = new SettingBool(false, "dump", instance.key());

    private MutableComponent msg;

    @EventHandler
    private void onPacket(ReceivePacketEvent event) {
        if (!instance.isActive()) return;
        if (!(event.packet instanceof ClientboundResourcePackPushPacket(
                UUID id, String url, String hash, boolean required, Optional<Component> prompt
        ))) return;

        event.cancel();
        event.connection.send(new ServerboundResourcePackPacket(id, ServerboundResourcePackPacket.Action.ACCEPTED));
        event.connection.send(new ServerboundResourcePackPacket(id, ServerboundResourcePackPacket.Action.DOWNLOADED));
        event.connection.send(new ServerboundResourcePackPacket(id, ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED));

        MutableComponent msg = Component.literal("§7==========================\n").append(Component.literal("Resource Pack Skipped!\n").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        msg.append(Component.literal("ID: ").withStyle(ChatFormatting.GRAY)).append(Component.literal(id + "\n").withStyle(ChatFormatting.WHITE));
        msg.append(Component.literal("Required: ").withStyle(ChatFormatting.GRAY)).append(Component.literal(required + "\n").withStyle(required ? ChatFormatting.GOLD : ChatFormatting.GREEN));
        msg.append(Component.literal("Hash: ").withStyle(ChatFormatting.GRAY)).append(Component.literal(hash + "\n").withStyle(ChatFormatting.DARK_AQUA));
        prompt.ifPresent(promptComponent -> msg.append(Component.literal("Prompt: ").withStyle(ChatFormatting.GRAY)).append(promptComponent.copy()).append(Component.literal("\n")));
        MutableComponent urlComponent = Component.literal("[CLICK]").withStyle(style -> style.withColor(ChatFormatting.AQUA).withUnderlined(true).withClickEvent(new ClickEvent.OpenUrl(URI.create(url))).withHoverEvent(new HoverEvent.ShowText(Component.literal("Click To Open The Link:\n§b" + url))));
        msg.append(Component.literal("URL: ").withStyle(ChatFormatting.GRAY)).append(urlComponent).append(Component.literal("\n§7=========================="));
    }

    @EventHandler
    private void onTick(ServerTickEvent event) {
        if (!instance.isActive() || !Utils.canUpdate() || msg == null) return;

        Utils.infoRaw(msg);
        msg = null;
    }
}
