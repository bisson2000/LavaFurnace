package bisson2000.LavaFurnace.plugin.waila;

import bisson2000.LavaFurnace.LavaFurnace;
import bisson2000.LavaFurnace.tileentity.LavaFurnaceTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.mobius.waila.api.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.awt.*;
import java.util.List;


public class LavaFurnaceBlockWaila implements IServerDataProvider<TileEntity>, IComponentProvider, ITooltipRenderer  {

    public static final ResourceLocation WailaToolTip = new ResourceLocation(LavaFurnace.MOD_ID, "waila_tooltip");
    public static final String lavaFurnaceNBTData = "LavaFurnaceData";
    public static final String textNBTData = "text";
    public static final String fluidStoredNBTData = "fluid";
    public static final String maxNBTData = "max";
    public static final String smeltingProgressNBTData = "smeltingProgress";
    public static final String smeltingTotalNBTData = "smeltingTotal";

    public static final LavaFurnaceBlockWaila INSTANCE = new LavaFurnaceBlockWaila();

    @Override
    public void appendServerData(CompoundNBT compoundNBT, ServerPlayerEntity serverPlayerEntity, World world, TileEntity tileEntity) {
        if (tileEntity instanceof LavaFurnaceTileEntity) {
            LavaFurnaceTileEntity lavaFurnaceTileEntity = (LavaFurnaceTileEntity) tileEntity;
            final ListNBT data = this.addFluidData(lavaFurnaceTileEntity);
            compoundNBT.put(lavaFurnaceNBTData, data);
        }
    }

    private ListNBT addFluidData(LavaFurnaceTileEntity lavaFurnaceTileEntity) {
        final ListNBT returnedData = new ListNBT();
        final FluidTank fluidTank = lavaFurnaceTileEntity.getFluidTank();

        // Fluid data inside the FluidTank
        if(!fluidTank.isEmpty()) {
            CompoundNBT textData = new CompoundNBT();
            final TranslationTextComponent displayName = new TranslationTextComponent("gui." + LavaFurnace.MOD_ID + ".liquid");
            displayName.appendSibling(fluidTank.getFluid().getDisplayName());
            textData.putString(textNBTData, ITextComponent.Serializer.toJson(displayName));
            returnedData.add(textData);
        }

        // FluidTank data
        CompoundNBT fluidData = new CompoundNBT();
        fluidData.put(fluidStoredNBTData, fluidTank.getFluid().writeToNBT(new CompoundNBT()));
        fluidData.putInt(maxNBTData, fluidTank.getCapacity());
        returnedData.add(fluidData);

        // Cooking data
        // CompoundNBT furnaceTag = lavaFurnaceTileEntity.write(new CompoundNBT());
        // CompoundNBT extractedFurnaceTag = new CompoundNBT();
        // extractedFurnaceTag.putInt(smeltingProgressNBTData, furnaceTag.getInt("CookTime"));
        // extractedFurnaceTag.putInt(smeltingTotalNBTData, furnaceTag.getInt("CookTimeTotal"));
        // returnedData.add(extractedFurnaceTag);

        return returnedData;
    }

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        CompoundNBT data = accessor.getServerData();
        if (data.contains(lavaFurnaceNBTData, NBT.TAG_LIST)) {
            ListNBT list = data.getList(lavaFurnaceNBTData, NBT.TAG_COMPOUND);
            if (!list.isEmpty()) {
                CompoundNBT lavaFurnaceData = new CompoundNBT();
                lavaFurnaceData.put(lavaFurnaceNBTData, list);
                tooltip.add(new RenderableTextComponent(WailaToolTip, lavaFurnaceData));
            }
        }
    }

    @Override
    public Dimension getSize(CompoundNBT compoundNBT, ICommonAccessor iCommonAccessor) {
        ListNBT list = compoundNBT.getList(lavaFurnaceNBTData, NBT.TAG_COMPOUND);
        return new Dimension(102, 15 * list.size());
    }

    @Override
    public void draw(CompoundNBT compoundNBT, ICommonAccessor iCommonAccessor, int x, int y) {
        ListNBT list = compoundNBT.getList(lavaFurnaceNBTData, NBT.TAG_COMPOUND);
        MatrixStack matrix = new MatrixStack();
        int currentX = x + 1;
        int currentY = y + 1;
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT elementData = list.getCompound(i);
            if (elementData.contains(textNBTData, NBT.TAG_STRING)) {
                ITextComponent text = ITextComponent.Serializer.getComponentFromJson(elementData.getString(textNBTData));
                if (text != null) {
                    WailaRenderer.renderScaledText(Minecraft.getInstance(), matrix, currentX + 4, currentY + 3, 0xFFFFFF, 92, text);
                    currentY += 15;
                }
                continue;
            } else if (elementData.contains(fluidStoredNBTData, NBT.TAG_COMPOUND)) {
                FluidElement fluidElement = new FluidElement(FluidStack.loadFluidStackFromNBT(elementData.getCompound(fluidStoredNBTData)), elementData.getInt(maxNBTData));
                fluidElement.render(matrix, currentX, currentY);
            } else {
                continue;
            }
            currentY += 15;
        }
    }

}
