package violet.features.chat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import violet.config.Feature;
import violet.config.SettingJson;
import violet.hud.clickgui.Settings;
import violet.hud.clickgui.components.FlatTextbox;
import violet.misc.Rendering;
import violet.misc.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;
import static violet.Main.mc;

/*
    copied from nofrills
 */

public class CommandAliases {
    public static final Feature instance = new Feature("commandAliases");

    public static final SettingJson data = new SettingJson(new JsonObject(), "data", instance);

    public static void init(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        if (instance.isActive() && data.value().has("aliases")) {
            for (JsonElement element : data.value().get("aliases").getAsJsonArray()) {
                JsonObject alias = element.getAsJsonObject();
                String aliasName = alias.get("alias").getAsString();
                if (aliasName.isEmpty()) {
                    continue;
                }
                String name = aliasName.startsWith("/") ? aliasName.substring(1) : aliasName;
                LiteralArgumentBuilder<FabricClientCommandSource> command = literal(name.trim()).executes(context -> {
                    Utils.say(alias.get("message").getAsString());
                    return SINGLE_SUCCESS;
                }).then(argument("param", StringArgumentType.greedyString()).executes(context -> {
                    String param = StringArgumentType.getString(context, "param");
                    Utils.say(Utils.format("{} {}", alias.get("message").getAsString(), param.trim()));
                    return SINGLE_SUCCESS;
                }));
                dispatcher.register(command);
            }
        }
    }

    public static List<FlowLayout> getSettingsList() {
        List<FlowLayout> list = new ArrayList<>();
        Settings.BigButton button = new Settings.BigButton("Add New Alias", btn -> {
            data.edit(object -> {
                if (!object.has("aliases")) {
                    object.add("aliases", new JsonArray());
                }
                JsonObject obj = new JsonObject();
                obj.addProperty("alias", "");
                obj.addProperty("message", "");
                object.get("aliases").getAsJsonArray().add(obj);
            });
            mc.setScreen(buildSettings());
        });
        button.button.verticalSizing(Sizing.fixed(18));
        list.add(button);
        if (data.value().has("aliases")) {
            JsonArray aliases = data.value().get("aliases").getAsJsonArray();
            for (int i = 0; i < aliases.size(); i++) {
                list.add(new Setting(i));
            }
        }
        return list;
    }

    public static Settings buildSettings() {
        Settings settings = new Settings(getSettingsList());
        settings.setTitle(Component.literal("Command Aliases"));
        return settings;
    }

    public static class Setting extends FlowLayout {
        public int index;
        public FlowLayout options;
        public FlatTextbox aliasInput;
        public FlatTextbox messageInput;
        public ButtonComponent delete;

        public Setting(int index) {
            super(Sizing.content(), Sizing.content(), Algorithm.VERTICAL);
            this.padding(Insets.of(5, 5, 4, 5));
            this.horizontalAlignment(HorizontalAlignment.LEFT);

            this.index = index;
            this.options = UIContainers.horizontalFlow(Sizing.content(), Sizing.content());

            this.aliasInput = new FlatTextbox(Sizing.fixed(118));
            this.aliasInput.margins(Insets.of(0, 0, 0, 5));
            this.aliasInput.text(this.getData(data.value()).get("alias").getAsString());
            this.aliasInput.tooltip(Component.literal("The command name of this alias. Example: /dn"));
            this.aliasInput.onChanged().subscribe(value -> data.edit(object -> this.getData(object).addProperty("alias", value)));

            this.messageInput = new FlatTextbox(Sizing.fixed(118));
            this.messageInput.margins(Insets.of(0, 0, 0, 5));
            this.messageInput.text(this.getData(data.value()).get("message").getAsString());
            this.messageInput.tooltip(Component.literal("The message/command that this alias will send. Example: /warp dungeon_hub"));
            this.messageInput.onChanged().subscribe(value -> data.edit(object -> this.getData(object).addProperty("message", value)));

            this.delete = UIComponents.button(Component.literal("Delete").withColor(0xffffff), button -> {
                data.edit(object -> object.get("aliases").getAsJsonArray().remove(this.index));
                mc.setScreen(buildSettings());
            });
            this.delete.positioning(Positioning.relative(100, 50)).verticalSizing(Sizing.fixed(18));
            this.delete.renderer((context, btn, delta) -> {
                context.fill(btn.getX(), btn.getY(), btn.getX() + btn.getWidth(), btn.getY() + btn.getHeight(), 0xff101010);
                Rendering.drawBorder(context, btn.getX(), btn.getY(), btn.getWidth(), btn.getHeight(), 0xffffffff);
            });

            this.options.child(this.aliasInput);
            this.options.child(this.messageInput);
            this.child(this.options);
            this.child(this.delete);
        }

        public JsonObject getData(JsonObject object) {
            return object.get("aliases").getAsJsonArray().get(this.index).getAsJsonObject();
        }
    }
}
