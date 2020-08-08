package bisson2000.LavaFurnace.containers;

import bisson2000.LavaFurnace.init.BlocksRegistry;
import bisson2000.LavaFurnace.init.ContainerRegistry;
import bisson2000.LavaFurnace.tileentity.LavaFurnaceTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.Objects;

public class LavaFurnaceContainer extends Container {

    private final LavaFurnaceTileEntity te;
    private final IIntArray furnaceData;
    protected final World world;
    protected final PlayerInventory playerInventory;
    private final IRecipeType<? extends AbstractCookingRecipe> recipeType = IRecipeType.SMELTING;
    private final IWorldPosCallable canInteractWithCallable;

    public LavaFurnaceContainer(int windowId, PlayerInventory playerInventory, LavaFurnaceTileEntity te) {
        this(windowId, playerInventory, te, new IntArray(4));
    }

    public LavaFurnaceContainer(int windowId, PlayerInventory playerInventory, LavaFurnaceTileEntity te, IIntArray furnaceDataIn){
        super(ContainerRegistry.LAVA_FURNACE_CONTAINER.get(), windowId);

        assertIntArraySize(furnaceDataIn, 4);
        this.te = te;
        this.furnaceData = furnaceDataIn;
        this.world = playerInventory.player.world;
        this.playerInventory = playerInventory;
        this.canInteractWithCallable = IWorldPosCallable.of(te.getWorld(), te.getPos());

        //Adding slots
        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent( h -> {
            addSlot(new LavaFurnaceInputSlot(h, 0, 56, 17));
            addSlot(new LavaFurnaceResultSlot(playerInventory.player, te, h, 1, 116, 35));
        });

        layoutPlayerInventorySlots(8,84);

        this.trackIntArray(furnaceDataIn);

    }

    public static LavaFurnaceContainer createLavaFurnaceContainer(int windowId, PlayerInventory playerInventory, LavaFurnaceTileEntity te, IIntArray furnaceDataIn){
        return new LavaFurnaceContainer(windowId, playerInventory, te, furnaceDataIn);
    }

    public static LavaFurnaceContainer createLavaFurnaceContainer(int windowId, PlayerInventory playerInventory, PacketBuffer extraData) {
        LavaFurnaceTileEntity te = getTileEntity(playerInventory, extraData);
        return new LavaFurnaceContainer(windowId, playerInventory, te);
    }

    public final LavaFurnaceTileEntity getLavaFurnaceTileEntity(){
        return te;
    }

    private static LavaFurnaceTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
        Objects.requireNonNull(playerInventory, "Null pointer. PlayerInventory has to exist");
        Objects.requireNonNull(data, "Null pointer. Data has to exist");
        final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
        if (tileAtPos instanceof LavaFurnaceTileEntity) {
            return (LavaFurnaceTileEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is incorrect at: " + tileAtPos);
    }

    private int addSlotRange(PlayerInventory playerInventory, int index, int x, int y, int amount, int dx){
        for(int j = 0 ; j < amount; j++){
            addSlot(new Slot(playerInventory, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    public int addSlotBox(PlayerInventory playerInventory, int index, int x, int y, int horizontalAmount, int dx, int verticalAmount, int dy){
        for(int i = 0; i < verticalAmount; i++){
            index = addSlotRange(playerInventory, index, x, y, horizontalAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow){
        //Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        //HotBar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9,18);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(canInteractWithCallable, playerIn, BlocksRegistry.LAVA_FURNACE.get());
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {

        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 1) {
                if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index != 0) {
                if (this.hasRecipe(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 2 && index < 29) {
                    if (!this.mergeItemStack(itemstack1, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 29 && index < 38 && !this.mergeItemStack(itemstack1, 2, 29, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 2, 38, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    protected boolean hasRecipe(ItemStack stack) {
        return this.world.getRecipeManager().getRecipe((IRecipeType)this.recipeType, new Inventory(stack), this.world).isPresent();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
    }

    @OnlyIn(Dist.CLIENT)
    public int getCookProgressionScaled() {
        int i = this.furnaceData.get(2);
        int j = this.furnaceData.get(3);
        return j != 0 && i != 0 ? i * 24 / j : 0;
    }

    @OnlyIn(Dist.CLIENT)
    public int getBurnLeftScaled() {
        return te.getFluidTank().getFluidAmount() * 13 / te.getFluidTank().getCapacity();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isBurning() {
        return this.furnaceData.get(0) > 0;
    }

}
