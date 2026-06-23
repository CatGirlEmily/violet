package violet.hud.clickgui.components;

import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.gui.GuiGraphics;
import violet.features.misc.ClickGuiFeature;
import violet.misc.Rendering;

public class FlatTextbox extends TextBoxComponent {
    public int borderColor = ClickGuiFeature.getAccentColor() | 0xFF000000;

    public FlatTextbox(Sizing horizontalSizing) {
        super(horizontalSizing);
        this.verticalSizing(Sizing.fixed(18));
        this.margins(Insets.of(0, 0, 0, 8));
        this.setMaxLength(256);
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        context.fill(this.x(), this.y(), this.getX() + this.width() + 4, this.y() + this.height(), 0xff101010);
        Rendering.drawBorder(context, this.x(), this.y(), this.width() + 4, this.height(), this.borderColor);
        super.renderWidget(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean isBordered() {
        return false;
    }

    @Override
    public int getInnerWidth() {
        return this.width - 8;
    }
}
