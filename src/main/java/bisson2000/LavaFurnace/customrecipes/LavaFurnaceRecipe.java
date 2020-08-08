//package bisson2000.LavaFurnace.customrecipes;
//
//import mekanism.api.annotations.NonNull;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.crafting.*;
//import net.minecraft.network.PacketBuffer;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.world.World;
//import net.minecraftforge.fluids.FluidStack;
//
//import java.util.function.Predicate;
//
//public class LavaFurnaceRecipe implements IRecipe<NoIInventory>, Predicate<@NonNull ItemStack> { TODO: create a proper recipe for the furnace
//
//    private final ResourceLocation recipeID;
//    private final ItemStack itemInput;
//    private final FluidStack fluidInput;
//    private final ItemStack itemOutput;
//
//    public LavaFurnaceRecipe(ResourceLocation recipeID, ItemStack itemInput, FluidStack fluidInput, ItemStack itemOutput) {
//        this.recipeID = recipeID;
//        this.itemInput = itemInput;
//        this.fluidInput = fluidInput;
//        this.itemOutput = itemOutput;
//    }
//
//    @Override
//    public boolean matches(NoIInventory inv, World worldIn) {
//        return true;
//    }
//
//    @Override
//    public ItemStack getCraftingResult(NoIInventory inv) {
//        return null;
//    }
//
//    @Override
//    public boolean canFit(int width, int height) {
//        return false;
//    }
//
//    @Override
//    public ItemStack getRecipeOutput() {
//        return null;
//    }
//
//    @Override
//    public ResourceLocation getId() {
//        return recipeID;
//    }
//
//    @Override
//    public IRecipeSerializer<?> getSerializer() {
//        return null;
//    }
//
//    @Override
//    public IRecipeType<?> getType() {
//        return null;
//    }
//
//    @Override //To verify
//    public boolean isDynamic() {
//        return true;
//    }
//
//
//
//
//    public void write(PacketBuffer buffer){
//        buffer.writeItemStack(itemInput);
//        buffer.writeFluidStack(fluidInput);
//        buffer.writeItemStack(itemInput);
//    }
//
//    public ItemStack getItemInput(){
//        return itemInput;
//    }
//
//    public ItemStack getItemOutput(){
//        return itemOutput.copy();
//    }
//
//    @Override
//    public boolean test(@NonNull ItemStack itemStack) {
//        Ingredient
//        return this.itemInput
//    }
//}
