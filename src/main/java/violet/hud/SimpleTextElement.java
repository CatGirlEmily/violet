package violet.hud;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import violet.config.Feature;
import violet.config.SettingBool;
import violet.config.SettingColor;
import violet.config.SettingEnum;
import violet.hud.clickgui.Settings;
import violet.hud.clickgui.components.PlainLabel;
import violet.misc.RenderColor;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class SimpleTextElement extends HudElement {
    public final SettingEnum<TextAlignment> textAlignment;
    public final SettingBool textShadow;
    public final SettingColor textColor;
    public MutableComponent text;
    public MutableComponent defaultText;
    public LabelComponent label;

    public SimpleTextElement(MutableComponent text, Feature instance, String label) {
        super(UIContainers.horizontalFlow(Sizing.content(), Sizing.content()), instance, label);
        this.textAlignment = new SettingEnum<>(TextAlignment.Left, TextAlignment.class, "align", instance);
        this.textShadow = new SettingBool(true, "shadow", instance);
        this.textColor = new SettingColor(RenderColor.fromHex(0x5ca0bf), "textColor", instance);
        this.text = text.withColor(this.getTextColor());
        this.defaultText = this.text.copy();
        this.label = new PlainLabel(this.text);
        this.label.margins(Insets.of(2));
        this.layout.child(this.label);
    }

    @Override
    public boolean shouldRender() {
        boolean shouldRender = super.shouldRender();
        if (shouldRender) {
            HorizontalAlignment alignment = switch (this.textAlignment.value()) {
                case Left -> HorizontalAlignment.LEFT;
                case Center -> HorizontalAlignment.CENTER;
                case Right -> HorizontalAlignment.RIGHT;
            };
            this.label.shadow(this.textShadow.value());
            this.label.horizontalTextAlignment(alignment);
            this.layout.horizontalAlignment(alignment);
        }
        return shouldRender;
    }

    @Override
    public HudSettings getBaseSettings() {
        return this.getBaseSettings(new ArrayList<>());
    }

    @Override
    public HudSettings getBaseSettings(List<FlowLayout> extra) {
        List<FlowLayout> list = new ArrayList<>(extra);
        list.add(new Settings.Toggle("Shadow", this.textShadow, "Adds a shadow to the element's text."));
        list.add(new Settings.EnumToggle<>("Alignment", this.textAlignment, "The alignment of the element's text."));
        list.add(new Settings.ColorPicker("Text Color", this.textColor, "The base color of the element's text."));
        return super.getBaseSettings(list);
    }

    public void setText(String text) {
        this.label.text(Component.literal(text).withColor(this.getTextColor()));
    }

    public void setText(MutableComponent text) {
        this.label.text(text.withColor(this.getTextColor()));
    }

    public void setDefaultText() {
        this.label.text(this.defaultText);
    }

    public int getTextColor() {
        return this.textColor.value().hex;
    }

    public enum TextAlignment {
        Left,
        Center,
        Right
    }
}
