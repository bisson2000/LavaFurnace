package bisson2000.LavaFurnace.client.render;

import bisson2000.LavaFurnace.blocks.LavaFurnaceBlock;
import bisson2000.LavaFurnace.init.TileEntityRegistry;
import bisson2000.LavaFurnace.tileentity.LavaFurnaceTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class LavaFurnaceRenderer extends TileEntityRenderer<LavaFurnaceTileEntity> {

    private static final float FRAME_THICKNESS = 1.0f/16.0f;
    private static final float FURNACE_THICKNESS = 12.0f/16.0f;

    public LavaFurnaceRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(LavaFurnaceTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

        renderTileFluid(tileEntityIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }

    private static void renderTileFluid(LavaFurnaceTileEntity tileEntityIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int lightIn, int overlayLight){

        FluidTank tank = tileEntityIn.getFluidTank();
        if(tank == null || tank.getFluid() == null || tank.getFluid().getFluid() == null || tank.isEmpty())
            return;

        FluidStack fluid = tank.getFluid();
        Fluid renderedFluid = tank.getFluid().getFluid();
        float scale = Math.min( (1.0f - FRAME_THICKNESS/2 - FRAME_THICKNESS) * fluid.getAmount() / (tank.getCapacity()), 1.0f) ;
        if(scale <= 0.0f)
            return;

        final int lightLevel = calculateLightEmitted(fluid, lightIn);

        matrixStackIn.push(); //push

        ResourceLocation stillfluidResourceLocation = renderedFluid.getAttributes().getStillTexture();
        TextureAtlasSprite stillFluidSprite = Minecraft.getInstance().getModelManager().getAtlasTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE).getSprite(stillfluidResourceLocation);
        Minecraft.getInstance().getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
        IVertexBuilder builder = bufferIn.getBuffer(RenderType.getTranslucent());

        int color = renderedFluid.getAttributes().getColor();
        float r = ((color >> 16) & 0xFF) / 255f; // red
        float g = ((color >> 8) & 0xFF) / 255f; // green
        float b = ((color >> 0) & 0xFF) / 255f; // blue
        float a = ((color >> 24) & 0xFF) / 255f; // alpha

        /**
         * the rendering is done on base of 1.0f
         * Fluid texture size is 16x16
         * A block size is 16x16
         * A fluid texture drawn can therefore not be smaller than the block (unless a smaller version of it is used)
         */

        float iconWidth = stillFluidSprite.getWidth();
        float iconHeight = stillFluidSprite.getHeight();
        if (!(iconWidth > 0 && iconHeight > 0)) {
            matrixStackIn.pop(); //pop
            return;
        }

        //Rotation
        int rotationDegrees = 0;
        Direction direction = tileEntityIn.getBlockState().get(LavaFurnaceBlock.FACING);
        switch (direction){
            case EAST:
                rotationDegrees += 90;
            case SOUTH:
                rotationDegrees += 90;
            case WEST:
                rotationDegrees += 90;
            case NORTH: //Facing north by default
                break;
            default:
                break;
        }

        matrixStackIn.translate(.5, .5, .5);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(rotationDegrees));
        matrixStackIn.translate(-.5, -.5, -.5);

        float uMin = stillFluidSprite.getMinU();
        float vMin = stillFluidSprite.getMinV();
        float adaptedMaxU = stillFluidSprite.getMaxU();
        float adaptedMaxV = stillFluidSprite.getMinV();

        //top
        adaptedMaxU = adaptedMaxU(stillFluidSprite, FURNACE_THICKNESS, 1 - FRAME_THICKNESS);
        adaptedMaxV = adaptedMaxV(stillFluidSprite, FRAME_THICKNESS, 1 - FRAME_THICKNESS);
        add(builder, matrixStackIn, r, g, b, a, FURNACE_THICKNESS , scale + FRAME_THICKNESS , 1 - FRAME_THICKNESS, uMin, adaptedMaxV, lightLevel, overlayLight);
        add(builder, matrixStackIn, r, g, b, a,1 - FRAME_THICKNESS , scale + FRAME_THICKNESS , 1 - FRAME_THICKNESS, adaptedMaxU, adaptedMaxV, lightLevel, overlayLight);
        add(builder, matrixStackIn, r, g, b, a,1 - FRAME_THICKNESS, scale + FRAME_THICKNESS , FRAME_THICKNESS, adaptedMaxU, vMin, lightLevel, overlayLight);
        add(builder, matrixStackIn, r, g, b, a, FURNACE_THICKNESS , scale + FRAME_THICKNESS , FRAME_THICKNESS, uMin, vMin, lightLevel, overlayLight);

        //bottom
        add(builder, matrixStackIn, r, g, b, a, FURNACE_THICKNESS , FRAME_THICKNESS , FRAME_THICKNESS, uMin, vMin, lightLevel, overlayLight);
        add(builder, matrixStackIn, r, g, b, a, 1 - FRAME_THICKNESS, FRAME_THICKNESS , FRAME_THICKNESS, adaptedMaxU, vMin, lightLevel, overlayLight);
        add(builder, matrixStackIn, r, g, b, a, 1 - FRAME_THICKNESS , FRAME_THICKNESS , 1 - FRAME_THICKNESS, adaptedMaxU, adaptedMaxV, lightLevel, overlayLight);
        add(builder, matrixStackIn, r, g, b, a, FURNACE_THICKNESS , FRAME_THICKNESS , 1 - FRAME_THICKNESS, uMin, adaptedMaxV, lightLevel, overlayLight);

        //North
        adaptedMaxU = adaptedMaxU(stillFluidSprite, FURNACE_THICKNESS, 1 - FRAME_THICKNESS);
        adaptedMaxV = adaptedMaxV(stillFluidSprite, FRAME_THICKNESS, FRAME_THICKNESS + scale);
        add(builder, matrixStackIn, r, g, b, a, FURNACE_THICKNESS , FRAME_THICKNESS +scale , FRAME_THICKNESS, uMin, vMin, lightLevel, overlayLight);
        add(builder, matrixStackIn, r, g, b, a, 1 - FRAME_THICKNESS, FRAME_THICKNESS +scale , FRAME_THICKNESS, adaptedMaxU, vMin, lightLevel, overlayLight);
        add(builder, matrixStackIn, r, g, b, a, 1 - FRAME_THICKNESS , FRAME_THICKNESS , FRAME_THICKNESS, adaptedMaxU, adaptedMaxV, lightLevel, overlayLight);
        add(builder, matrixStackIn, r, g, b, a, FURNACE_THICKNESS , FRAME_THICKNESS , FRAME_THICKNESS, uMin, adaptedMaxV, lightLevel, overlayLight);

        //East
        adaptedMaxU = adaptedMaxU(stillFluidSprite, FRAME_THICKNESS, 1 - FRAME_THICKNESS);
        adaptedMaxV = adaptedMaxV(stillFluidSprite, FRAME_THICKNESS, FRAME_THICKNESS +scale);
        add(builder, matrixStackIn, r, g, b, a, 1 - FRAME_THICKNESS, FRAME_THICKNESS +scale , FRAME_THICKNESS, uMin, vMin, lightLevel, overlayLight);
        add(builder, matrixStackIn, r, g, b, a, 1 - FRAME_THICKNESS, FRAME_THICKNESS +scale , 1 - FRAME_THICKNESS, adaptedMaxU, vMin, lightLevel, overlayLight);
        add(builder, matrixStackIn, r, g, b, a, 1 - FRAME_THICKNESS, FRAME_THICKNESS , 1 - FRAME_THICKNESS, adaptedMaxU, adaptedMaxV, lightLevel, overlayLight);
        add(builder, matrixStackIn, r, g, b, a, 1 - FRAME_THICKNESS, FRAME_THICKNESS , FRAME_THICKNESS, uMin, adaptedMaxV, lightLevel, overlayLight);

        //South
        adaptedMaxU = adaptedMaxU(stillFluidSprite, FURNACE_THICKNESS, 1 - FRAME_THICKNESS);
        adaptedMaxV = adaptedMaxV(stillFluidSprite, FRAME_THICKNESS, FRAME_THICKNESS + scale);
        add(builder, matrixStackIn, r, g, b, a, FURNACE_THICKNESS , FRAME_THICKNESS , 1 - FRAME_THICKNESS, uMin, adaptedMaxV, lightLevel, overlayLight);
        add(builder, matrixStackIn, r, g, b, a, 1 - FRAME_THICKNESS , FRAME_THICKNESS , 1 - FRAME_THICKNESS, adaptedMaxU, adaptedMaxV, lightLevel, overlayLight);
        add(builder, matrixStackIn, r, g, b, a, 1 - FRAME_THICKNESS, FRAME_THICKNESS +scale , 1 - FRAME_THICKNESS, adaptedMaxU, vMin, lightLevel, overlayLight);
        add(builder, matrixStackIn, r, g, b, a, FURNACE_THICKNESS , FRAME_THICKNESS +scale , 1 - FRAME_THICKNESS, uMin, vMin, lightLevel, overlayLight);
        //West
        //Nothing to render when facing the block itself

        matrixStackIn.pop(); //pop

    }

    private static float adaptedMaxU(TextureAtlasSprite fluidSprite, float u1, float u2){
        float uMin = fluidSprite.getMinU();
        float iconUDif = (fluidSprite.getMaxU() - uMin);
        float percentageU = (u2-u1);
        float adaptedMaxU = uMin + iconUDif * percentageU;
        return adaptedMaxU;
    }

    private static float adaptedMaxV(TextureAtlasSprite fluidSprite, float v1, float v2){
        float vMin = fluidSprite.getMinV();
        float iconVDif = (fluidSprite.getMaxV() - vMin);
        float percentageV = (v2-v1);
        float adaptedMaxV = vMin + iconVDif * percentageV;
        return adaptedMaxV;
    }

    private static int calculateLightEmitted(@Nonnull FluidStack fluid, int basicLightLevel){
        if(fluid.isEmpty())
            return basicLightLevel;

        int fluidGlowLevel = fluid.getFluid().getAttributes().getLuminosity();

        if(fluidGlowLevel >= 15)
            return 0xF000F0;

        int blockLight = LightTexture.getLightBlock(basicLightLevel);
        int skyLight = LightTexture.getLightSky(basicLightLevel);
        return LightTexture.packLight(Math.max(blockLight, fluidGlowLevel), Math.max(skyLight, fluidGlowLevel));

    }

    private static void add(IVertexBuilder renderer, MatrixStack stack, float r, float g, float b, float a,
                            float x, float y, float z, float u, float v, int lightMap, int overlayLight) {
        renderer.pos(stack.getLast().getMatrix(), x, y, z)
                .color(r, g, b, a)
                .tex(u, v)
                .overlay(overlayLight)
                .lightmap(lightMap)
                .normal(1, 0, 0)
                .endVertex();
    }

    public static void register(){
        ClientRegistry.bindTileEntityRenderer(TileEntityRegistry.LAVA_FURNACE_TILE_ENTITY.get(), LavaFurnaceRenderer::new);
    }

}
