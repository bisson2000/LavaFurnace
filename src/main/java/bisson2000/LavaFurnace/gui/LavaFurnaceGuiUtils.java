package bisson2000.LavaFurnace.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class LavaFurnaceGuiUtils {

    public static void colorFluid(FluidStack fluid) {
        int color = fluid.getFluid().getAttributes().getColor();
        float r = ((color >> 16) & 0xFF) / 255f; // red
        float g = ((color >> 8) & 0xFF) / 255f; // green
        float b = ((color) & 0xFF) / 255f; // blue
        float a = ((color >> 24) & 0xFF) / 255f; // alpha
        RenderSystem.color4f(r, g, b, a);
    }

    public static void resetColor() {
        RenderSystem.color4f(1, 1, 1, 1);
    }

    public static void drawFluidSprite(MatrixStack matrixStack, FluidStack fluid, float posX, float XOffset, float posY, float YOffset, float w, float h) {
        LavaFurnaceGuiUtils.drawFluidSprite(matrixStack, fluid, posX + XOffset, posY + YOffset, w, h);
    }

    public static void drawFluidSprite(MatrixStack matrixStack, FluidStack fluid, float posX, float posY, float w, float h) {

        ResourceLocation fluidResourceLocation = fluid.getFluid().getAttributes().getStillTexture(fluid);
        TextureAtlasSprite fluidSprite = Minecraft.getInstance().getModelManager().getAtlasTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE).getSprite(fluidResourceLocation);
        Minecraft.getInstance().getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);

        LavaFurnaceGuiUtils.colorFluid(fluid);

        int iconWidth = fluidSprite.getWidth();
        int iconHeight = fluidSprite.getHeight();

        if (!(iconWidth > 0 && iconHeight > 0))
            return;

        float uMax = fluidSprite.getMaxU();
        float uMin = fluidSprite.getMinU();
        float vMax = fluidSprite.getMaxV();
        float vMin = fluidSprite.getMinV();

        int iterMaxW = (int) (w / iconWidth);
        int iterMaxH = (int) (h / iconHeight);
        float leftoverW = w % iconWidth;
        float leftoverH = h % iconHeight;
        float leftoverWf = leftoverW / (float) iconWidth;
        float leftoverHf = leftoverH / (float) iconHeight;
        float iconUDif = uMax - uMin;
        float iconVDif = vMax - vMin;

        for (int ww = 0; ww < iterMaxW; ww++) {
            for (int hh = 0; hh < iterMaxH; hh++)
                customInnerBlit(matrixStack.getLast().getMatrix(), (int) posX + ww * iconWidth, (int) posY + hh * iconHeight, iconWidth, iconHeight,
                        uMin, uMax, vMin, vMax);
            customInnerBlit(matrixStack.getLast().getMatrix(), (int) posX + ww * iconWidth, (int) posY + iterMaxH * iconHeight, iconWidth, (int) leftoverH,
                    uMin, uMax, vMin, (vMin + iconVDif * leftoverHf));


        }
        if (leftoverW > 0) {
            for (int hh = 0; hh < iterMaxH; hh++)
                customInnerBlit(matrixStack.getLast().getMatrix(), (int) posX + iterMaxW * iconWidth, (int) posY + hh * iconHeight, (int) leftoverW, iconHeight,
                        uMin, (uMin + iconUDif * leftoverWf), vMin, vMax);
            customInnerBlit(matrixStack.getLast().getMatrix(), (int) posX + iterMaxW * iconWidth, (int) posY + iterMaxH * iconHeight, (int) leftoverW, (int) leftoverH,
                    uMin, (uMin + iconUDif * leftoverWf), vMin, (vMin + iconVDif * leftoverHf));
        }

    }

    /**
     * Same process as the default minecraft AbstractGui#innerBlit function
     *
     * @param matrix
     * @param x1
     * @param y1
     * @param width
     * @param height
     * @param minU
     * @param maxU
     * @param minV
     * @param maxV
     */
    private static void customInnerBlit(Matrix4f matrix, int x1, int y1, int width, int height, float minU, float maxU, float minV, float maxV) {
        int blitOffset = 0;
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(matrix, (float) x1, (float) y1 + height, (float) blitOffset).tex(minU, maxV).endVertex();
        bufferbuilder.pos(matrix, (float) x1 + width, (float) y1 + height, (float) blitOffset).tex(maxU, maxV).endVertex();
        bufferbuilder.pos(matrix, (float) x1 + width, (float) y1, (float) blitOffset).tex(maxU, minV).endVertex();
        bufferbuilder.pos(matrix, (float) x1, (float) y1, (float) blitOffset).tex(minU, minV).endVertex();
        bufferbuilder.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(bufferbuilder);
    }

}
