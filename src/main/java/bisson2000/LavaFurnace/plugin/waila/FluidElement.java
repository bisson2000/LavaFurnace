package bisson2000.LavaFurnace.plugin.waila;

import bisson2000.LavaFurnace.LavaFurnace;
import bisson2000.LavaFurnace.gui.LavaFurnaceGuiUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class FluidElement {

    @Nonnull
    private final FluidStack storedFluid;
    private final int capacity;
    private final int borderColor = WailaRenderer.BORDER_COLOR;
    private final int textColor = WailaRenderer.TEXT_COLOR;

    public FluidElement(@Nonnull FluidStack stored, int capacity) {
        this.storedFluid = stored;
        this.capacity = capacity;
    }

    public void render(@Nonnull MatrixStack matrix, int x, int y) {
        int width = getWidth();
        int height = getHeight();
        AbstractGui.fill(matrix, x, y, x + width - 1, y + 1, borderColor);
        AbstractGui.fill(matrix, x, y, x + 1, y + height - 1, borderColor);
        AbstractGui.fill(matrix, x + width - 1, y, x + width, y + height - 1, borderColor);
        AbstractGui.fill(matrix, x, y + height - 1, x + width, y + height, borderColor);
        if (!storedFluid.isEmpty()) {
            int scale = getScaledLevel(width - 2);
            if (scale > 0) {
                LavaFurnaceGuiUtils.drawFluidSprite(matrix, storedFluid, x + 1, y + 1, scale, height - 2);
                //GuiUtils.drawTiledSprite(matrix, x + 1, y + 1, height - 2, scale, height - 2, icon,
                //        16, 16, 0, TilingDirection.DOWN_RIGHT);
                LavaFurnaceGuiUtils.resetColor();
            }
        }
        WailaRenderer.renderScaledText(Minecraft.getInstance(), matrix, x + 4, y + 3, textColor, getWidth() - 8, getToolTipText());
    }

    public int getScaledLevel(int level) {
        if (capacity == 0 || storedFluid.getAmount() == Integer.MAX_VALUE) {
            return level;
        }
        int returnedLevel = level * storedFluid.getAmount() / capacity;
        return returnedLevel < 0 ? level : returnedLevel;
    }

    private ITextComponent getToolTipText() {
        return new StringTextComponent(storedFluid.getAmount() + "mB");
    }

    private int getWidth() {
        return 100;
    }

    private int getHeight() {
        return 13;
    }

}
