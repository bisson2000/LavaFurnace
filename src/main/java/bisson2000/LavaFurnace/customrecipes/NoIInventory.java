//package bisson2000.LavaFurnace.customrecipes;
//
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.inventory.IInventory;
//import net.minecraft.item.ItemStack;
//
//public final class NoIInventory implements IInventory {
//
//    public static final NoIInventory INSTANCE = new NoIInventory();
//
//    private NoIInventory(){
//    }
//
//    @Override
//    public int getSizeInventory() {
//        return 0;
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return true;
//    }
//
//    @Override
//    public ItemStack getStackInSlot(int index) {
//        return ItemStack.EMPTY;
//    }
//
//    @Override
//    public ItemStack decrStackSize(int index, int count) {
//        return ItemStack.EMPTY;
//    }
//
//    @Override
//    public ItemStack removeStackFromSlot(int index) {
//        return ItemStack.EMPTY;
//    }
//
//    @Override
//    public void setInventorySlotContents(int index, ItemStack stack) {
//    }
//
//    @Override
//    public void markDirty() {
//    }
//
//    @Override
//    public boolean isUsableByPlayer(PlayerEntity player) {
//        return false;
//    }
//
//    @Override
//    public void clear() {
//    }
//}
