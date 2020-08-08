package bisson2000.LavaFurnace.inventory;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

public class InternalSlot extends ItemStackHandler {

    private final TileEntity te;

    public InternalSlot(int numberOfSlots, TileEntity te){
        super(numberOfSlots);
        this.te = te;
    }

    @Override
    protected void onContentsChanged(int slot) {
        te.markDirty();
    }

}
