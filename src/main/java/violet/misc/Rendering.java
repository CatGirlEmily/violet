package violet.misc;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

public final class Rendering {

    public static void drawBorder(GuiGraphicsExtractor context, int x, int y, int width, int height, RenderColor color) {
        drawBorder(context, x, y, width, height, color.argb);
    }

    public static void drawBorder(GuiGraphicsExtractor context, int x, int y, int width, int height, int argb) {
        context.fill(x, y, x + width, y + 1, argb);
        context.fill(x, y + height - 1, x + width, y + height, argb);
        context.fill(x, y + 1, x + 1, y + height - 1, argb);
        context.fill(x + width - 1, y + 1, x + width, y + height - 1, argb);
    }
}
