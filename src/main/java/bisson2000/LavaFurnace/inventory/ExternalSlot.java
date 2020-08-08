package bisson2000.LavaFurnace.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public class ExternalSlot extends ItemStackHandler {

    private final TileEntity te;
    private final ItemStackHandler internalSlot;
    private final BiFunction<Integer, ItemStack, Boolean> validSlots;

    public ExternalSlot(int numberOfSlots, TileEntity te, ItemStackHandler internalSlot, @Nonnull BiFunction<Integer, ItemStack, Boolean> validSlots ){
        super(numberOfSlots);
        this.te = te;
        this.internalSlot = internalSlot;
        this.validSlots = validSlots;
    }


    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if(slot == 0)
            return internalSlot.insertItem(slot, stack, simulate);
        else
            return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if(slot == 0)
            return ItemStack.EMPTY;
        else
            return internalSlot.extractItem(slot, amount, simulate);
    }

    @Override
    protected void onContentsChanged(int slot) {
        te.markDirty();
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return validSlots.apply(slot, stack);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        internalSlot.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return internalSlot.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return internalSlot.getStackInSlot(slot);
    }

    @Override
    public void setSize(int size) {
        internalSlot.setSize(size);
    }


}
