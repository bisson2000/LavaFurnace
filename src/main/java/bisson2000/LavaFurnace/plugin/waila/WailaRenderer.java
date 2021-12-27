package bisson2000.LavaFurnace.plugin.waila;

import bisson2000.LavaFurnace.gui.LavaFurnaceGuiUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class WailaRenderer {

    public static final int BORDER_COLOR = 0xFF000000;
    public static final int TEXT_COLOR = 0xFFFFFF;

    public static void renderScaledText(Minecraft mc, @Nonnull MatrixStack matrix, int x, int y, int color, int maxWidth, ITextComponent component) {
        int length = mc.fontRenderer.getStringPropertyWidth(component);
        if (length <= maxWidth) {
            mc.fontRenderer.drawText(matrix, component, x, y, color);
        } else {
            float scale = (float) maxWidth / length;
            float reverse = 1 / scale;
            float yAdd = 4 - (scale * 8) / 2F;
            matrix.push();
            matrix.scale(scale, scale, scale);
            mc.fontRenderer.drawText(matrix, component, (int) (x * reverse), (int) ((y * reverse) + yAdd), color);
            matrix.pop();
        }

        LavaFurnaceGuiUtils.resetColor();
    }

}
