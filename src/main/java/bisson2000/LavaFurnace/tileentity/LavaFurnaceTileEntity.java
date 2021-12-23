package bisson2000.LavaFurnace.tileentity;

import bisson2000.LavaFurnace.util.Config;
import bisson2000.LavaFurnace.LavaFurnace;
import bisson2000.LavaFurnace.blocks.LavaFurnaceBlock;
import bisson2000.LavaFurnace.containers.LavaFurnaceContainer;
import bisson2000.LavaFurnace.init.TileEntityRegistry;
import bisson2000.LavaFurnace.inventory.*;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.*;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class LavaFurnaceTileEntity extends TileEntity implements IRecipeHolder, IRecipeHelperPopulator, ITickableTileEntity, INamedContainerProvider, INameable {

    protected FluidTank tank = new FluidTank(Config.LAVA_FURNACE_TANK_CAPACITY.get());
    private ITextComponent customName;
    private int burnTime;
    private int recipesUsed;
    private int cookTime;
    private int cookTimeTotal;
    protected final IIntArray furnaceData = new IIntArray() {
        public int get(int index) {
            switch (index) {
                case 0:
                    return LavaFurnaceTileEntity.this.burnTime;
                case 1:
                    return LavaFurnaceTileEntity.this.recipesUsed;
                case 2:
                    return LavaFurnaceTileEntity.this.cookTime;
                case 3:
                    return LavaFurnaceTileEntity.this.cookTimeTotal;
                default:
                    return 0;
            }
        }

        public void set(int index, int value) {
            switch (index) {
                case 0:
                    LavaFurnaceTileEntity.this.burnTime = value;
                    break;
                case 1:
                    LavaFurnaceTileEntity.this.recipesUsed = value;
                    break;
                case 2:
                    LavaFurnaceTileEntity.this.cookTime = value;
                    break;
                case 3:
                    LavaFurnaceTileEntity.this.cookTimeTotal = value;
            }

        }

        public int size() {
            return 4;
        }
    };

    private final Object2IntOpenHashMap<ResourceLocation> field_214022_n = new Object2IntOpenHashMap<>();
    protected final IRecipeType<? extends AbstractCookingRecipe> recipeType;

    public LavaFurnaceTileEntity() {
        super(TileEntityRegistry.LAVA_FURNACE_TILE_ENTITY.get());
        this.recipeType = IRecipeType.SMELTING;
        tank.fill(FluidStack.EMPTY, IFluidHandler.FluidAction.EXECUTE);

    }

    public void setCustomName(ITextComponent name) {
        this.customName = name;
    }

    @Override
    public ITextComponent getName() {
        return this.customName != null ? this.customName : new TranslationTextComponent("container.lava_furnace");
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.getName();
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return customName;
    }

    @Nullable
    @Override
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
        return new LavaFurnaceContainer(p_createMenu_1_, p_createMenu_2_, this, this.furnaceData);
    }

    public static boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 1)
            return false;

        return true;
    }

    @Override
    public void fillStackedContents(RecipeItemHelper helper) {
        IItemHandler handler = getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);
        if (handler == null)
            return;

        for (int i = 0; i < handler.getSlots(); i++) {
            helper.accountStack(handler.getStackInSlot(i));

        }
    }

    @Override
    public void setRecipeUsed(@Nullable IRecipe<?> recipe) {
        if (recipe != null) {
            ResourceLocation resourcelocation = recipe.getId();
            this.field_214022_n.addTo(resourcelocation, 1);
        }
    }

    @Nullable
    @Override
    public IRecipe<?> getRecipeUsed() {
        return null;
    }

    private int lastFluidAmount = -1;

    @Override
    public void tick() {

        if (world.isRemote)
            return;

        boolean flag = this.isBurning();
        boolean flag1 = false;

        if (this.isBurning()) {
            int amount = tank.getFluidAmount();

            if (!this.canBurn())
                this.burnTime = 0;

            if (tank.getFluid().getRawFluid() != Fluids.EMPTY) {
                if((amount - Config.LAVA_FURNACE_MB_PER_TICK.get()) <= 0)
                    tank.getFluid().setAmount(0);
                else
                    tank.getFluid().setAmount(amount - Config.LAVA_FURNACE_MB_PER_TICK.get());
            }

            flag1 = true;
        }

        //BlockStates
        if (tank.getFluidAmount() != this.lastFluidAmount) {

            flag1 = true;

            int configTemperature = Config.BASE_FLUID_MODIFIER.getAttributes().getTemperature();
            int tankTemperature = tank.getFluid().getFluid().getAttributes().getTemperature();
            boolean isWhitelisted = Config.WHITELISTED_FLUIDS.contains(tank.getFluid().getFluid());

            if(tank.isEmpty() && !getBlockState().get(LavaFurnaceBlock.IS_EMPTY))
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(LavaFurnaceBlock.IS_EMPTY, Boolean.valueOf(tank.isEmpty())),3);
            else if(!tank.isEmpty() && getBlockState().get(LavaFurnaceBlock.IS_EMPTY))
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(LavaFurnaceBlock.IS_EMPTY, Boolean.valueOf(tank.isEmpty())),3);

            if (Config.isFluidValid(tank.getFluid().getFluid()) && !getBlockState().get(LavaFurnaceBlock.HAS_HOT_FLUID))
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(LavaFurnaceBlock.HAS_HOT_FLUID, Boolean.valueOf(!tank.isEmpty())), 3);
            else if (!isWhitelisted && tankTemperature < configTemperature && getBlockState().get(LavaFurnaceBlock.HAS_HOT_FLUID))
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(LavaFurnaceBlock.HAS_HOT_FLUID, Boolean.valueOf(false)), 3);
            else if (isWhitelisted && tank.isEmpty())
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(LavaFurnaceBlock.HAS_HOT_FLUID, Boolean.valueOf(false)), 3);

            lastFluidAmount = tank.getFluidAmount();
        }

        if (!internalHandler.isPresent())
            return;
        IItemHandler handler = getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);
        if (handler == null)
            return;

        ItemStack inputStack = handler.getStackInSlot(0);

        if (this.isBurning() || !tank.isEmpty() && !inputStack.isEmpty()) {
            IRecipe<?> irecipe = getMatchingRecipeForInput(this.recipeType, inputStack, world).orElse(null);
            if (!this.isBurning() && this.canSmelt(irecipe)) {
                if (this.canBurn()) {
                    this.cookTimeTotal = this.getCookTime();
                    this.burnTime = 1;
                    this.recipesUsed = this.burnTime;
                }
                if (this.isBurning()) {
                    flag1 = true;
                }
            }

            if (this.isBurning() && this.canSmelt(irecipe)) {
                ++this.cookTime;
                if (this.cookTime == this.cookTimeTotal) {
                    this.cookTime = 0;
                    this.cookTimeTotal = this.getCookTime();
                    this.smelt(irecipe);
                    flag1 = true;
                }
            } else {
                this.cookTime = 0;
                this.burnTime = 0;
            }

        } else if (!this.isBurning() && this.cookTime > 0) {
            this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
        }

        if (flag != this.isBurning()) {
            flag1 = true;
            this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(LavaFurnaceBlock.LIT, Boolean.valueOf(this.isBurning())), 3);
        }

        if (flag1) {
            this.markDirty();
            this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
        }

    }

    private static Optional<? extends AbstractCookingRecipe> getMatchingRecipeForInput(IRecipeType recipeTypeIn, ItemStack itemStack, World world) {
        RecipeManager recipeManager = world.getRecipeManager();
        Inventory singleItemInventory = new Inventory(itemStack);
        Optional<? extends AbstractCookingRecipe> matchingRecipe = recipeManager.getRecipe((IRecipeType<AbstractCookingRecipe>) recipeTypeIn, singleItemInventory, world);
        return matchingRecipe;
    }

    public FluidTank getFluidTank() {
        return this.tank;
    }

    @Override
    public void onCrafting(PlayerEntity player) {
    }

    private final ItemStackHandler internalSlot = new InternalSlot(2, this);
    private final ItemStackHandler externalSlot = new ExternalSlot(2, this, internalSlot,
            LavaFurnaceTileEntity::isItemValidForSlot); //Helps external handling
    private final LazyOptional<ItemStackHandler> internalHandler = LazyOptional.of(() -> internalSlot);
    private final LazyOptional<ItemStackHandler> externalHandler = LazyOptional.of(() -> externalSlot);
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> tank);

    @Override
    public <T> LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(facing == null){
                return internalHandler.cast();
            }
            if (world != null && world.getBlockState(pos).getBlock() != this.getBlockState().getBlock()) {
                LavaFurnace.LOGGER.debug(LavaFurnace.MOD_ID + ":" + this.getClass().getSimpleName() + " Block mismatch at @" + this.pos);
                return internalHandler.cast();
            }
            return externalHandler.cast();
        }
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluidHandler.cast();
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public void remove() {
        super.remove();
        internalHandler.invalidate();
        externalHandler.invalidate();
        fluidHandler.invalidate();
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    protected boolean canSmelt(@Nullable IRecipe<?> recipeIn) {
        IItemHandler handler = getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);

        if (handler != null && !handler.getStackInSlot(0).isEmpty() && recipeIn != null) {
            ItemStack itemstack = recipeIn.getRecipeOutput();
            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack itemstack1 = handler.getStackInSlot(1);
                if (itemstack1.isEmpty()) {
                    return true;
                } else if (!itemstack1.isItemEqual(itemstack)) {
                    return false;
                } else if (itemstack1.getCount() + itemstack.getCount() <= this.getInventoryStackLimit() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
                    return true;
                } else {
                    return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
                }
            }
        } else {
            return false;
        }
    }

    private void smelt(@Nullable IRecipe<?> recipe) {

        IItemHandler handler = getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);

        if (handler != null && recipe != null && this.canSmelt(recipe)) {
            ItemStack itemstack = handler.getStackInSlot(0);
            ItemStack itemstack1 = recipe.getRecipeOutput();
            ItemStack itemstack2 = handler.getStackInSlot(1);
            if (itemstack2.isEmpty()) {
                handler.insertItem(1, itemstack1.copy(), false);
            } else if (itemstack2.getItem() == itemstack1.getItem()) {
                itemstack2.grow(itemstack1.getCount());
            }

            if (!this.world.isRemote) {
                this.setRecipeUsed(recipe);
            }

            itemstack.shrink(1);
        }
    }

    private boolean isBurning() {
        return this.burnTime > 0;
    }

    private boolean canBurn() {
        if (tank.isEmpty())
            return false;

        return Config.isFluidValid(tank.getFluid().getFluid());
    }

    private boolean canBurn(FluidStack fluid) {
        if (tank.isEmpty()) //{
            return false;

        return Config.isFluidValid(fluid.getFluid());
    }

    /**
     * Cook time is divided by 5 by default, based on BASE_FLUID_MODIFIER
     *
     * @return cook time
     */
    protected int getCookTime() {
        IItemHandler handler = getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);
        if (handler == null)
            return 0;

        int tankTemperature = tank.getFluid().getFluid().getAttributes().getTemperature();
        int configTemperature = Math.max(Config.BASE_FLUID_MODIFIER.getAttributes().getTemperature(), 1);
        int cookTimeModifier = MathHelper.clamp(tankTemperature / configTemperature * Config.SMELT_SPEED_MODIFIER.get(),
                Config.MINIMUM_SMELT_SPEED_MODIFIER.get(), Config.MAXIMUM_SMELT_SPEED_MODIFIER.get());
        return Math.max ((int) (getMatchingRecipeForInput(recipeType, handler.getStackInSlot(0), world).map(
                AbstractCookingRecipe::getCookTime).orElse(200) / cookTimeModifier), 1);
    }

    public void func_235645_d_(PlayerEntity p_235645_1_) {
        List<IRecipe<?>> list = this.func_235640_a_(p_235645_1_.world, p_235645_1_.getPositionVec());
        p_235645_1_.unlockRecipes(list);
        this.field_214022_n.clear();
    }

    public List<IRecipe<?>> func_235640_a_(World p_235640_1_, Vector3d p_235640_2_) {
        List<IRecipe<?>> list = Lists.newArrayList();

        for (Object2IntMap.Entry<ResourceLocation> entry : this.field_214022_n.object2IntEntrySet()) {
            p_235640_1_.getRecipeManager().getRecipe(entry.getKey()).ifPresent((p_235642_4_) -> {
                list.add(p_235642_4_);
                func_235641_a_(p_235640_1_, p_235640_2_, entry.getIntValue(), ((AbstractCookingRecipe) p_235642_4_).getExperience());
            });
        }

        return list;
    }

    private static void func_235641_a_(World p_235641_0_, Vector3d p_235641_1_, int p_235641_2_, float p_235641_3_) {
        int i = MathHelper.floor((float) p_235641_2_ * p_235641_3_);
        float f = MathHelper.frac((float) p_235641_2_ * p_235641_3_);
        if (f != 0.0F && Math.random() < (double) f) {
            ++i;
        }

        while (i > 0) {
            int j = ExperienceOrbEntity.getXPSplit(i);
            i -= j;
            p_235641_0_.addEntity(new ExperienceOrbEntity(p_235641_0_, p_235641_1_.x, p_235641_1_.y, p_235641_1_.z, j));
        }

    }

    //Furnace data manipulation
    @Override
    public void read(BlockState stateIn, CompoundNBT nbtIn) {

        super.read(stateIn, nbtIn);

        if (nbtIn.contains("CustomName", 8)) {
            this.customName = ITextComponent.Serializer.getComponentFromJson(nbtIn.getString("CustomName"));
        }

        internalHandler.ifPresent( itemStackHandler -> {
            CompoundNBT invTag = nbtIn.getCompound("inv");
            itemStackHandler.deserializeNBT(invTag);
            this.recipesUsed = ForgeHooks.getBurnTime(itemStackHandler.getStackInSlot(0));
        });

        this.tank.readFromNBT(nbtIn.getCompound("tank"));
        this.burnTime = nbtIn.getInt("BurnTime");
        this.cookTime = nbtIn.getInt("CookTime");
        this.cookTimeTotal = nbtIn.getInt("CookTimeTotal");

        CompoundNBT compoundnbt = nbtIn.getCompound("RecipesUsed");
        for (String s : compoundnbt.keySet()) {
            this.field_214022_n.put(new ResourceLocation(s), compoundnbt.getInt(s));
        }

    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);

        if (this.customName != null) {
            compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
        }

        internalHandler.ifPresent( itemStackHandler -> {
            compound.put("inv", itemStackHandler.serializeNBT());
        });

        CompoundNBT tankTag = tank.writeToNBT(new CompoundNBT());
        compound.put("tank", tankTag);

        compound.putInt("BurnTime", this.burnTime);
        compound.putInt("CookTime", this.cookTime);
        compound.putInt("CookTimeTotal", this.cookTimeTotal);
        CompoundNBT compoundnbt = new CompoundNBT();
        this.field_214022_n.forEach((resourceLocation, intValue) -> {
            compoundnbt.putInt(resourceLocation.toString(), intValue);
        });
        compound.put("RecipesUsed", compoundnbt);
        return compound;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getPos(), 1, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(world.getBlockState(pkt.getPos()), pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    //Unchanged from default
    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.read(state, tag);
    }

}
