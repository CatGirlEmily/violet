package violet.hud;

import io.wispforest.owo.ui.hud.Hud;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import violet.events.*;
import violet.hud.elements.*;
import violet.misc.Utils;

import java.util.ArrayList;
import java.util.List;

import static violet.Main.mc;

public class HudManager {
    public static final List<HudElement> elements = new ArrayList<>();
    public static final FPS fps = new FPS("FPS: §f0");
    public static final TPS tps = new TPS("TPS: §f20.00");
    public static final Ping ping = new Ping("Ping: §f0ms");
    public static final Day day = new Day("Day: §f0");
    public static final Armor armor = new Armor();
    public static final Inventory inventory = new Inventory();
    public static final LagMeter lagMeter = new LagMeter("Last server tick was 0.00s ago");

    private static CustomTitle currentTitle = new CustomTitle(Text.empty(), 0);

    public static boolean isEditingHud() {
        return mc.currentScreen instanceof HudEditorScreen;
    }

    public static List<HudElement> getElements() {
        return elements;
    }

    public static void addNew(HudElement element) {
        elements.add(element);
    }

    public static void registerElements() {
        for (HudElement element : elements) {
            Identifier identifier = element.getIdentifier();
            if (!Hud.hasComponent(identifier)) {
                Hud.add(identifier, () -> element);
            }
        }
    }

    public static void setCustomTitle(MutableText text, int ticks) {
        currentTitle = new CustomTitle(text, ticks);
    }

    public static void setCustomTitle(String text, int ticks) {
        setCustomTitle(Text.literal(text), ticks);
    }

    @EventHandler
    private static void onRenderHud(HudRenderEvent event) {
        if (!isEditingHud()) {
            for (HudElement element : HudManager.elements) {
                if (element.isAdded()) element.updatePosition();
            }
        }
        if (currentTitle.isActive()) {
            currentTitle.draw(event.context);
        }
    }

    @EventHandler
    private static void onJoinServer(ServerJoinEvent event) {
        ping.reset();
        tps.reset();
        fps.reset();
        lagMeter.setTickTime(0);
        for (HudElement element : elements) {
            if (element instanceof TickTimerElement tickTimer && tickTimer.isAutoPause()) {
                tickTimer.pause();
            }
            if (element instanceof TimerElement timer && timer.isAutoPause()) {
                timer.pause();
            }
        }
        currentTitle.reset();
    }

    @EventHandler
    private static void onPing(ReceivePacketEvent event) {
        if (event.packet instanceof PingResultS2CPacket(long startTime)) {
            if (ping.isActive()) {
                ping.setPing(Util.getMeasuringTimeMs() - startTime);
                ping.ticks = 20;
            }
        }
    }

    @EventHandler
    private static void onWorldTick(WorldTickEvent event) {
        if (day.isActive() && mc.world != null) {
            day.setDay(mc.world.getLevelProperties().getTimeOfDay() / 24000L);
        }
        if (ping.isActive()) { // pings every second when element is enabled, waits until ping result is received
            if (ping.ticks > 0) {
                ping.ticks -= 1;
                if (ping.ticks == 0) {
                    Utils.sendPingPacket();
                }
            }
        }
        if (tps.isActive()) {
            if (tps.clientTicks > 0) {
                tps.clientTicks -= 1;
                if (tps.clientTicks == 0) {
                    tps.setTps(tps.serverTicks);
                    tps.clientTicks = 20;
                    tps.serverTicks = 0;
                }
            }
        }
        if (fps.isActive()) {
            if (fps.ticks > 0) {
                fps.ticks -= 1;
                if (fps.ticks == 0) {
                    fps.setFps(mc.getCurrentFps());
                    fps.ticks = 20;
                }
            }
        }
        if (armor.isActive()) {
            armor.updateArmor();
        }
        if (inventory.isActive()) {
            inventory.updateInventory();
        }
        if (currentTitle.isActive()) {
            currentTitle.tick();
        }
    }

    @EventHandler
    private static void onServerTick(ServerTickEvent event) {
        if (lagMeter.isActive()) {
            lagMeter.setTickTime(Util.getMeasuringTimeMs());
        }
        if (tps.isActive()) {
            tps.serverTicks += 1;
        }
    }

    public static class CustomTitle {
        public MutableText text;
        public int ticks;

        public CustomTitle(MutableText text, int ticks) {
            this.text = text;
            this.ticks = ticks;
        }

        public boolean isActive() {
            return this.ticks > 0;
        }

        public void tick() {
            this.ticks--;
        }

        public void reset() {
            this.ticks = 0;
        }

        public void draw(DrawContext context) {
            context.getMatrices().pushMatrix();
            context.getMatrices().translate(context.getScaledWindowWidth() * 0.5f, context.getScaledWindowHeight() * 0.5f);
            context.getMatrices().pushMatrix();
            context.getMatrices().scale(4.0F, 4.0F);
            int width = mc.textRenderer.getWidth(this.text);
            context.drawTextWithBackground(mc.textRenderer, this.text, -width / 2, -context.getScaledWindowHeight() / 12, width, -1);
            context.getMatrices().popMatrix();
            context.getMatrices().popMatrix();
        }
    }
}
