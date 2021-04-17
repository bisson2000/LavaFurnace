package bisson2000.LavaFurnace.network;

import bisson2000.LavaFurnace.LavaFurnace;
import bisson2000.LavaFurnace.blocks.LavaFurnaceBlock;
import bisson2000.LavaFurnace.tileentity.LavaFurnaceTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class DumpLavaFurnace {

    private final FluidStack fluidStack;
    private final BlockPos pos;

    public DumpLavaFurnace(PacketBuffer buf) {
        fluidStack = buf.readFluidStack();
        pos = buf.readBlockPos();
    }

    public DumpLavaFurnace(FluidStack fluidStack, BlockPos posIn) {
        this.fluidStack = fluidStack;
        this.pos = posIn;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeFluidStack(fluidStack);
        buf.writeBlockPos(pos);
    }


    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerWorld world = (ServerWorld)ctx.get().getSender().world;
            if (world.isBlockLoaded(pos) && world.getTileEntity(pos) instanceof LavaFurnaceTileEntity) {
                LavaFurnaceTileEntity te = (LavaFurnaceTileEntity) world.getTileEntity(pos);
                te.getFluidTank().setFluid(FluidStack.EMPTY);
                te.getBlockState().with(LavaFurnaceBlock.IS_EMPTY, Boolean.TRUE);
                world.setBlockState(pos, world.getBlockState(pos).with(LavaFurnaceBlock.IS_EMPTY, Boolean.TRUE),3);
                world.setBlockState(pos, world.getBlockState(pos).with(LavaFurnaceBlock.HAS_HOT_FLUID, Boolean.FALSE), 3);
            } else {
                LavaFurnace.LOGGER.error("Dumping fluid in Tank @: " + pos.toString() + " failed because" +
                        " the block is unloaded or not a LavaFurnaceTileEntity");
            }
        });
        return true;
    }

}
