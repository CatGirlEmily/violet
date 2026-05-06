package violet.misc;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.entity.SimpleEntityLookup;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static violet.Main.*;

public class Utils {
    public static final MessageIndicator violetIndicator = new MessageIndicator(0x5ca0bf, null, Text.of("Message from violet mod."), "violet Mod");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void playSound(SoundEvent event, float volume, float pitch) {
        mc.getSoundManager().play(PositionedSoundInstance.master(event, pitch, volume));
    }

    public static void playSound(RegistryEntry.Reference<SoundEvent> event, float volume, float pitch) {
        playSound(event.value(), volume, pitch);
    }

    public static void playSound(String event, float volume, float pitch) {
        playSound(SoundEvent.of(Identifier.of(event)), volume, pitch);
    }

    public static void sendMessage(String message) {
        if (mc.player != null && !message.isEmpty()) {
            if (message.startsWith("/")) {
                mc.player.networkHandler.sendChatCommand(message.substring(1));
            } else {
                mc.player.networkHandler.sendChatMessage(message);
            }
        }
    }

    public static MutableText getTag() {
        return Text.literal("[Violet] ").withColor(0x5ca0bf);
    }

    public static MutableText getShortTag() {
        return Text.literal("[V] ").withColor(0x5ca0bf);
    }

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

    public static String getCoordsFormatted(String format) {
        BlockPos pos = mc.player.getBlockPos();
        return format(format, pos.getX(), pos.getY(), pos.getZ());

    }

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

    public static boolean isBaseHealth(LivingEntity entity, float health) {
        float current = entity.getHealth();
        float difference = current - health;
        return current >= health && (current % health == 0 || (current - difference) % health == 0);
    }

    /**
     * Returns the entity's bounding box at their interpolated position.
     */
    public static Box getLerpedBox(Entity entity, float tickProgress) {
        return entity.getDimensions(EntityPose.STANDING).getBoxAt(entity.getLerpedPos(tickProgress));
    }

    public static List<Entity> getEntities() {
        if (mc.world != null) {
            SimpleEntityLookup<Entity> lookup = (SimpleEntityLookup<Entity>) mc.world.entityManager.getLookup();
            return new ArrayList<>(lookup.index.idToEntity.values());
        }
        return new ArrayList<>();
    }

    public static List<Entity> getOtherEntities(Entity except, Box box, Predicate<? super Entity> filter) {
        List<Entity> entities = new ArrayList<>();
        for (Entity ent : getEntities()) {
            if (ent != null && ent != except && (filter == null || filter.test(ent)) && ent.getBoundingBox().intersects(box)) {
                entities.add(ent);
            }
        }
        return entities;
    }

    public static List<Entity> getOtherEntities(Entity from, double distX, double distY, double distZ, Predicate<? super Entity> filter) {
        return getOtherEntities(from, Box.of(from.getEntityPos(), distX, distY, distZ), filter);
    }

    public static List<Entity> getOtherEntities(Entity from, double dist, Predicate<? super Entity> filter) {
        return getOtherEntities(from, Box.of(from.getEntityPos(), dist, dist, dist), filter);
    }

    public static float getTextScale(double dist, float base, float scaling) {
        float distScale = (float) (1 + dist * scaling);
        return Math.max(base * distScale, base);
    }

    public static float getTextScale(double dist, float base) {
        return getTextScale(dist, base, 0.1f);
    }

    public static float getTextScale(Vec3d pos, float base, float scaling) {
        if (mc.player != null) {
            return getTextScale(mc.player.getEntityPos().distanceTo(pos), base, scaling);
        }
        return 0.0f;
    }

    public static float getTextScale(Vec3d pos, float base) {
        return getTextScale(pos, base, 0.1f);
    }

    public static boolean matchesKey(KeyBinding binding, KeyInput keyInput, MouseInput mouseInput) {
        return (keyInput != null && binding.matchesKey(keyInput)) || (mouseInput != null && binding.matchesMouse(new Click(0, 0, mouseInput)));
    }

    public static boolean matchesKey(KeyBinding binding, KeyInput keyInput) {
        return matchesKey(binding, keyInput, null);
    }

    public static boolean matchesKey(KeyBinding binding, MouseInput mouseInput) {
        return matchesKey(binding, null, mouseInput);
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

    public static boolean hasItemQuantity(String name) {
        return Pattern.matches(".* x[0-9]*", name);
    }

    public static GameProfile getTextures(ItemStack stack) {
        ProfileComponent profile = stack.getComponents().get(DataComponentTypes.PROFILE);
        if (!stack.isEmpty() && profile != null) {
            return profile.getGameProfile();
        }
        return null;
    }

    public static String getTextureUrl(GameProfile profile) {
        if (profile != null) {
            MinecraftSessionService service = mc.getApiServices().sessionService();
            Property property = service.getPackedTextures(profile);
            MinecraftProfileTextures textures = service.unpackTextures(property);
            if (textures.skin() != null) {
                return textures.skin().getUrl();
            }
        }
        return "";
    }

    public static String getTextureUrl(ItemStack stack) {
        return getTextureUrl(getTextures(stack));
    }

    public static boolean isTextureEqual(GameProfile profile, String textureId) {
        String url = getTextureUrl(profile);
        if (url != null) {
            return url.endsWith("texture/" + textureId);
        }
        return false;
    }

    public static List<Text> getLoreText(ItemStack stack) {
        LoreComponent lore = stack.getComponents().get(DataComponentTypes.LORE);
        if (lore != null) {
            return lore.lines();
        }
        return new ArrayList<>();
    }

    /**
     * Returns every line of the stack's lore with no formatting, or else an empty list.
     */
    public static List<String> getLoreLines(ItemStack stack) {
        List<String> lines = new ArrayList<>();
        for (Text line : getLoreText(stack)) {
            lines.add(toPlain(line).trim());
        }
        return lines;
    }

    /**
     * Tries to find ground (any block that isn't air) below the specified BlockPos, and returns the BlockPos of that block if found. Otherwise, returns the same BlockPos.
     *
     * @param maxDistance The maximum downward Y distance the check will travel
     */
    public static BlockPos findGround(BlockPos pos, int maxDistance) {
        int dist = Math.clamp(maxDistance, 0, 256);
        for (int i = 0; i <= dist; i++) {
            BlockPos below = pos.down(i);
            if (!mc.world.getBlockState(below).isAir()) {
                return below;
            }
        }
        return pos;
    }

    /**
     * Makes the 1st letter of each word in the string uppercase.
     *
     * @param replaceUnderscores if true, automatically replace all underscores with spaces
     */
    public static String uppercaseFirst(String text, boolean replaceUnderscores) {
        return Arrays.stream(replaceUnderscores ? text.replaceAll("_", " ").split("\\s") : text.split("\\s"))
                .map(word -> Character.toTitleCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" ")).trim();
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

    private static int getVersionNumber(String version) {
        String[] numbers = version.split("\\.");
        if (numbers.length >= 3) {
            return parseInt(numbers[0]).orElse(0) * 1000 + parseInt(numbers[1]).orElse(0) * 100 + parseInt(numbers[2]).orElse(0);
        }
        return 0;
    }

    /**
     * Checks if our player entity is currently within an area, made from 2 sets of coordinates.
     */
    public static boolean isInZone(double x1, double y1, double z1, double x2, double y2, double z2) {
        Box area = new Box(x1, y1, z1, x2, y2, z2);
        return area.contains(mc.player.getEntityPos());
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

    public static float horizontalDistance(Vec3d from, Vec3d to) {
        float x = (float) (from.getX() - to.getX());
        float z = (float) (from.getZ() - to.getZ());
        return MathHelper.sqrt(x * x + z * z);
    }

    public static float horizontalDistance(Entity from, Entity to) {
        return horizontalDistance(from.getEntityPos(), to.getEntityPos());
    }


    /**
     * Returns every slot that is part of the container screen handler, excluding the player inventory slots.
     *
     * @param inverse if true, returns the slots that are part of the player inventory instead of the container itself.
     */
    public static List<Slot> getContainerSlots(GenericContainerScreenHandler handler, boolean inverse) {
        if (inverse) {
            return handler.slots.stream().filter(slot -> slot.id >= handler.getRows() * 9).toList();
        }
        return handler.slots.stream().filter(slot -> slot.id < handler.getRows() * 9).toList();
    }

    public static List<Slot> getContainerSlots(GenericContainerScreenHandler handler) {
        return getContainerSlots(handler, false);
    }

    public static List<Slot> getContainerSlots(ScreenHandler handler, boolean inverse) {
        if (handler instanceof GenericContainerScreenHandler containerHandler) {
            return getContainerSlots(containerHandler, inverse);
        }
        return List.of();
    }

    public static List<Slot> getContainerSlots(ScreenHandler handler) {
        return getContainerSlots(handler, false);
    }

    public static ItemStack getHeldItem() {
        return mc.player != null ? mc.player.getMainHandStack() : ItemStack.EMPTY;
    }

    public static String toLower(String string) {
        return string.toLowerCase(Locale.ROOT);
    }

    public static String toUpper(String string) {
        return string.toUpperCase(Locale.ROOT);
    }

    public static String toID(String string) {
        return toUpper(string.replace("'s", "").replaceAll(" ", "_"));
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

    public static Optional<Style> getStyle(Text text, Predicate<String> predicate) {
        return text.visit((textStyle, textString) -> {
            if (predicate.test(textString)) {
                return Optional.of(textStyle);
            }
            return Optional.empty();
        }, Style.EMPTY);
    }

    public static boolean hasColor(Style style, Formatting color) {
        return color.getColorValue() != null && hasColor(style, color.getColorValue());
    }

    public static boolean hasColor(Style style, int hex) {
        return style != null && style.getColor() != null && style.getColor().getRgb() == hex;
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

    public static int difference(int first, int second) {
        return Math.abs(Math.abs(first) - Math.abs(second));
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

    public static String formatSeparator(long number) {
        return new Formatter().format(Locale.ENGLISH, "%,d", number).toString();
    }

    public static String formatSeparator(int number) {
        return formatSeparator((long) number);
    }

    public static String formatSeparator(double number) {
        return new Formatter().format(Locale.ENGLISH, "%,.1f", number).toString();
    }

    public static String formatSeparator(float number) {
        return formatSeparator((double) number);
    }

    public static long getMeasuringTime() {
        return Util.getMeasuringTimeMs();
    }

    public static String ticksToTime(long ticks) {
        if (ticks < 20) {
            return "0s";
        }
        StringBuilder builder = new StringBuilder();
        long current = ticks;
        String[] units = new String[]{"h", "m", "s"};
        int[] durations = new int[]{72000, 1200, 20};
        for (int i = 0; i <= 2; i++) {
            int amount = 0;
            while (current >= durations[i]) {
                amount++;
                current -= durations[i];
            }
            if (amount > 0) {
                builder.append(amount).append(units[i]);
            }
        }
        return builder.toString();
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

    public static Screen getScreen() {
        return mc.currentScreen;
    }

    public static void showTitle(MutableText title, MutableText subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        mc.inGameHud.setTitle(title);
        mc.inGameHud.setSubtitle(subtitle);
        mc.inGameHud.setTitleTicks(fadeInTicks, stayTicks, fadeOutTicks);
    }

    public static void showTitle(String title, String subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        showTitle(Text.literal(title), Text.literal(subtitle), fadeInTicks, stayTicks, fadeOutTicks);
    }
}
