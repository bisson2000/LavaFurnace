package bisson2000.LavaFurnace.gui;

import bisson2000.LavaFurnace.LavaFurnace;
import bisson2000.LavaFurnace.containers.LavaFurnaceContainer;
import bisson2000.LavaFurnace.network.DumpLavaFurnace;
import bisson2000.LavaFurnace.network.Networking;
import bisson2000.LavaFurnace.tileentity.LavaFurnaceTileEntity;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.*;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class LavaFurnaceScreen extends ContainerScreen<LavaFurnaceContainer> {

    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(LavaFurnace.MOD_ID, "textures/gui/lava_furnace_gui.png");
    private final LavaFurnaceTileEntity lavaFurnaceTileEntity;
    private final FluidTank fluidTank;

    public LavaFurnaceScreen(LavaFurnaceContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        lavaFurnaceTileEntity = screenContainer.getLavaFurnaceTileEntity();
        fluidTank = screenContainer.getLavaFurnaceTileEntity().getFluidTank();
    }

    @Override
    public void init() { //Init
        super.init();
        Objects.requireNonNull(this.container, "Null pointer. There needs to be a container associated with the screen");
        Objects.requireNonNull(this.minecraft, "Null pointer. Minecraft needs to exist");

        this.titleX = (this.xSize - this.font.getStringPropertyWidth(this.title)) / 2;

        //add Button
        addButton(new Button(guiLeft + 37, guiTop + 59, 25, 10, new StringTextComponent("Dump"), (button) -> {
            dumpFluid();
        }));
    }

    private void dumpFluid() {
        Networking.sendToServer(new DumpLavaFurnace(FluidStack.EMPTY, lavaFurnaceTileEntity.getPos()));
    }

    @Override   //Render
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        this.renderBackground(matrixStack); //Render Background
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);

        //tooltip
        List<ITextComponent> tooltip = new ArrayList<>();
        this.drawToolTip(fluidTank.getFluid(), tooltip, fluidTank.getCapacity(), guiLeft + 14, guiTop + 9, 18, 59, mouseX, mouseY);
        if(!tooltip.isEmpty() && Minecraft.getInstance().currentScreen != null)
            Minecraft.getInstance().currentScreen.renderToolTip(matrixStack,
                    tooltip.stream().map(LanguageMap.getInstance()::func_241870_a).collect(ImmutableList.toImmutableList()),
                    mouseX, mouseY, font);

//        if (!tooltip.isEmpty())
//            GuiUtils.drawHoveringText(p_230430_1_, tooltip, mouseX, mouseY, width, height, -1, font);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float p_230450_2_, int mouseX, int mouseY) {

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);

        //Burning Progression
        int i = this.guiLeft;
        int j = this.guiTop;
        this.blit(matrix, i, j, 0, 0, this.xSize, this.ySize); //Blit function
        if (this.container.isBurning()) {
            int k = this.container.getBurnLeftScaled();
            this.blit(matrix, i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }

        //Cook Progression
        int l = this.container.getCookProgressionScaled();
        this.blit(matrix, i + 79, j + 34, 176, 14, l + 1, 16);

        //Fluid
        drawFluidInTank(matrix, fluidTank.getFluid(), fluidTank.getCapacity(), i + 14, j + 9, 18, 59);

        //Draw lines over fluid
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE); //Must rebind after drawing
        this.blit(matrix, i + 13, j + 8, 180, 35, 20, 61);

    }

    public static void drawToolTip(FluidStack fluid, List<ITextComponent> tooltip, int tankCapacity, int xToPlace, int yToPlace,
                                    int width, int height, int mouseX, int mouseY) {

        if (!(mouseX >= xToPlace && mouseX < xToPlace + width && mouseY >= yToPlace && mouseY < yToPlace + height))
            return;

        if (!fluid.isEmpty())
            tooltip.add(applyTooltipFormat(
                    fluid.getDisplayName(),
                    fluid.getFluid().getAttributes().getRarity(fluid).color
            ));
        else
            tooltip.add(new TranslationTextComponent("gui." + LavaFurnace.MOD_ID + ".empty"));

        if (Minecraft.getInstance().gameSettings.advancedItemTooltips && !fluid.isEmpty()) {
            if (!Screen.hasShiftDown())
                tooltip.add(new TranslationTextComponent("desc." + LavaFurnace.MOD_ID + ".info.holdShiftForInfo")); // § : minecraft color code
            else {
                tooltip.add(applyTooltipFormat(new StringTextComponent("Fluid Registry: " + fluid.getFluid().getRegistryName()), TextFormatting.DARK_GRAY));
                tooltip.add(applyTooltipFormat(new StringTextComponent("Density: " + fluid.getFluid().getAttributes().getDensity(fluid)), TextFormatting.DARK_GRAY));
                tooltip.add(applyTooltipFormat(new StringTextComponent("Temperature: " + fluid.getFluid().getAttributes().getTemperature(fluid)), TextFormatting.DARK_GRAY));
                tooltip.add(applyTooltipFormat(new StringTextComponent("Viscosity: " + fluid.getFluid().getAttributes().getViscosity(fluid)), TextFormatting.DARK_GRAY));
                tooltip.add(applyTooltipFormat(new StringTextComponent("NBT Data: " + fluid.getTag()), TextFormatting.DARK_GRAY));
            }
        }

        tooltip.add(applyTooltipFormat(new StringTextComponent(fluid.getAmount() + "/" + tankCapacity + "mB"), TextFormatting.GRAY));
    }

    private static IFormattableTextComponent applyTooltipFormat(ITextComponent component, TextFormatting... formatList) {
        Style style = component.getStyle();
        for (TextFormatting format : formatList)
            style = style.applyFormatting(format);
        return component.deepCopy().setStyle(style);
    }

    public static void drawFluidInTank(MatrixStack matrixStack, FluidStack fluid, int capacity, int xToPlace, int yToPlace, int width, int height) {
        matrixStack.push();
        if (fluid != null && fluid.getFluid() != null) {

            int fluidHeight = Math.min((int) (height * (fluid.getAmount() / (float) capacity)), height);
            LavaFurnaceGuiUtils.drawFluidSprite(matrixStack, fluid, xToPlace, yToPlace + height - fluidHeight, width, fluidHeight);
            LavaFurnaceGuiUtils.resetColor();

        }
        matrixStack.pop();
    }



}
