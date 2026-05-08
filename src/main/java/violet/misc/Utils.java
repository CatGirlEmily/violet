package violet.misc;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.io.IOException;
import java.net.URI;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.*;

import static violet.Main.*;

public class Utils {
    public static final MessageIndicator violetIndicator = new MessageIndicator(0x5ca0bf, null, Text.of("Message from violet mod."), "violet Mod");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Plays a sound at master volume category.
     *
     * @param event  the sound event to play
     * @param volume volume multiplier (1.0 = default)
     * @param pitch  pitch multiplier (1.0 = default)
     */
    public static void playSound(SoundEvent event, float volume, float pitch) {
        mc.getSoundManager().play(PositionedSoundInstance.master(event, pitch, volume));
    }

    /**
     * Plays a sound from a registry entry at master volume category.
     *
     * @param event  registry reference to the sound event
     * @param volume volume multiplier (1.0 = default)
     * @param pitch  pitch multiplier (1.0 = default)
     */
    public static void playSound(RegistryEntry.Reference<SoundEvent> event, float volume, float pitch) {
        playSound(event.value(), volume, pitch);
    }

    /**
     * Plays a sound by its namespaced identifier string at master volume category.
     *
     * @param event  sound identifier, e.g. {@code "minecraft:entity.player.levelup"}
     * @param volume volume multiplier (1.0 = default)
     * @param pitch  pitch multiplier (1.0 = default)
     */
    public static void playSound(String event, float volume, float pitch) {
        playSound(SoundEvent.of(Identifier.of(event)), volume, pitch);
    }
    

    /**
     * Sends a chat message or command on behalf of the player.
     * Messages starting with {@code /} are sent as commands
     *
     * @param message the message or command to send
     */
    public static void say(String message) {
        if (mc.player != null && !message.isEmpty()) {
            if (message.startsWith("/")) {
                mc.player.networkHandler.sendChatCommand(message.substring(1));
            } else {
                mc.player.networkHandler.sendChatMessage(message);
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    /// info (client side message)
    /////////////////////////////////////////////////////////////////////////////////////
    /// 
    public static MutableText getTag() {
        return Text.literal("[Violet] ").withColor(0x5ca0bf);
    }

    public static MutableText getShortTag() {
        return Text.literal("[V] ").withColor(0x5ca0bf);
    }

    /**
     * Sends client-side information message
     * @param message
     */
    public static void info(String message) {
        infoRaw(Text.literal(message));
    }

    public static void infoButton(String message, String command) {
        ClickEvent click = new ClickEvent.RunCommand(command);
        infoRaw(Text.literal(message).setStyle(Style.EMPTY.withClickEvent(click)));
    }

    public static void infoLink(String message, String url) {
        ClickEvent click = new ClickEvent.OpenUrl(URI.create(url));
        infoRaw(Text.literal(message).setStyle(Style.EMPTY.withClickEvent(click)));
    }

    public static void infoRaw(MutableText message) {
        if (message.getStyle() == null || message.getStyle().getColor() == null) {
            message = message.withColor(0xffffff);
        }
        mc.inGameHud.getChatHud().addMessage(getTag().append(message), null, violetIndicator);
    }

    public static void infoFormat(String message, Object... values) {
        infoRaw(Text.literal(format(message, values)));
    }
    ///////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Moves the player along with validating the pos by sending movepacket
     */
    public static void setPlayerPos(double x, double y, double z) {
        if (mc.player == null) return;
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x,y,z,true,mc.player.horizontalCollision));
        mc.player.setPosition(x, y,z);
    }
    
    /**
     * Checks if a PlayerEntity is a real player, and not an enemy or NPC. Some NPCs might falsely return true for a few seconds after spawning.
     */
    public static boolean isPlayer(PlayerEntity entity) {
        ClientPlayNetworkHandler handler = mc.getNetworkHandler();
        if (handler != null) {
            PlayerListEntry listEntry = handler.getPlayerListEntry(entity.getUuid());
            if (listEntry != null) {
                String displayName = listEntry.getProfile().name();
                if (displayName != null) {
                    String name = Formatting.strip(displayName);
                    return !name.isEmpty() && !name.contains(" ");
                }
            }
        }
        return entity == mc.player;
    }

    /**
     * Check if the provided entity is a living entity (and in the case of player entities, if it isn't a real player).
     */
    public static boolean isMob(Entity entity) {
        if (entity instanceof PlayerEntity player) {
            return !isPlayer(player);
        }
        return entity instanceof LivingEntity;
    }


    public static void sendPingPacket() {
        ClientPlayNetworkHandler handler = mc.getNetworkHandler();
        if (handler != null) {
            handler.sendPacket(new QueryPingC2SPacket(Util.getMeasuringTimeMs()));
        }
    }

    /**
     * Returns the armor that the entity is wearing.
     */
    public static List<ItemStack> getEntityArmor(LivingEntity entity) {
        if (entity != null) {
            return List.of(
                    entity.getEquippedStack(EquipmentSlot.HEAD),
                    entity.getEquippedStack(EquipmentSlot.CHEST),
                    entity.getEquippedStack(EquipmentSlot.LEGS),
                    entity.getEquippedStack(EquipmentSlot.FEET)
            );
        }
        return List.of();
    }

    /**
     * Returns the custom data compound of the provided ItemStack, or else null.
     */
    public static NbtCompound getCustomData(ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            NbtComponent data = stack.get(DataComponentTypes.CUSTOM_DATA);
            if (data != null) {
                return data.nbt;
            }
        }
        return null;
    }


    public static void atomicWrite(Path path, String content) throws IOException {
        Path parent = path.getParent();
        String fileName = path.getFileName().toString();
        Path tempPath = parent.resolve(Utils.format("{}-Temp-{}.{}",
                fileName.substring(0, fileName.indexOf(".")),
                Util.getMeasuringTimeMs(),
                fileName.substring(fileName.indexOf(".") + 1)
        ));
        if (!Files.exists(parent)) {
            Files.createDirectory(parent);
        }
        Files.writeString(tempPath, content);
        try {
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ignored) {
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
        }
        Files.deleteIfExists(tempPath);
    }

    public static void atomicWrite(Path path, JsonObject content) throws IOException {
        atomicWrite(path, GSON.toJson(content));
    }

    /**
     * Used for Entity mixins to check if the mixin is being applied to our own player entity.
     * <p>
     * <code>
     * if (isSelf(this)) {
     * do stuff...
     * }
     * </code>
     */
    public static boolean isSelf(Object entity) {
        return entity == mc.player;
    }

    public static String toLower(String string) {
        return string.toLowerCase(Locale.ROOT);
    }

    public static String toUpper(String string) {
        return string.toUpperCase(Locale.ROOT);
    }


    /**
     * Gets the string out of a Text object and removes any formatting codes.
     */
    public static String toPlain(Text text) {
        if (text != null) {
            return Formatting.strip(text.getString());
        }
        return "";
    }

    public static Optional<Integer> parseInt(String value) {
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

    public static Optional<Integer> parseHex(String value) {
        try {
            return Optional.of((int) Long.parseLong(value.replace("0x", ""), 16));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

    public static Optional<Double> parseDouble(String value) {
        try {
            return Optional.of(Double.parseDouble(value));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

    public static Optional<Long> parseLong(String value) {
        try {
            return Optional.of(Long.parseLong(value));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

    public static String parseDate(Calendar calendar) {
        return format("{} {}",
                calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()),
                DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault()).format(calendar.getTime())
        );
    }

    /**
     * Formats the string by replacing each set of curly brackets "{}" with one of the values in order, similarly to Rust's format macro.
     */
    public static String format(String string, Object... values) {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        for (String section : Splitter.on("{}").split(string)) {
            builder.append(section);
            if (index < values.length) {
                builder.append(values[index]);
            }
            index++;
        }
        return builder.toString();
    }

    public static String formatDecimal(double number) {
        return formatDecimal(number, 2);
    }

    public static String formatDecimal(float number) {
        return formatDecimal(number, 2);
    }

    public static String formatDecimal(double number, int spaces) {
        return new DecimalFormat("0." + "0".repeat(spaces)).format(number);
    }

    public static String formatDecimal(float number, int spaces) {
        return formatDecimal((double) number, spaces);
    }

    public static long getMeasuringTime() {
        return Util.getMeasuringTimeMs();
    }

    public static String getPercentageColor(double percentage, boolean inverse) {
        if (percentage > 0.66) {
            return inverse ? "§a" : "§c";
        }
        if (percentage > 0.33) {
            return "§6";
        }
        return inverse ? "§c" : "§a";
    }

    public static String getPercentageColor(float percentage, boolean inverse) {
        return getPercentageColor((double) percentage, inverse);
    }

    public static String getPercentageColor(double percentage) {
        return getPercentageColor(percentage, false);
    }

    public static String getPercentageColor(float percentage) {
        return getPercentageColor((double) percentage, false);
    }

    public static void setScreen(Screen screen) {
        mc.send(() -> mc.setScreen(screen));
    }

    public static void showTitle(MutableText title, MutableText subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        mc.inGameHud.setTitle(title);
        mc.inGameHud.setSubtitle(subtitle);
        mc.inGameHud.setTitleTicks(fadeInTicks, stayTicks, fadeOutTicks);
    }

    public static void showTitle(String title, String subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        showTitle(Text.literal(title), Text.literal(subtitle), fadeInTicks, stayTicks, fadeOutTicks);
    }

    public static String getServerIP() {
        ServerInfo info = mc.getCurrentServerEntry();
        if (info == null) return "singleplayer";
        return toLower(info.address);
    }
}
