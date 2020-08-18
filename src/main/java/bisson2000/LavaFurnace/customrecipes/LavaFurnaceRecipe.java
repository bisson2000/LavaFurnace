package bisson2000.LavaFurnace.customrecipes;

import bisson2000.LavaFurnace.LavaFurnace;
import bisson2000.LavaFurnace.init.BlocksRegistry;
import bisson2000.LavaFurnace.init.RecipeSerializerRegistry;
import bisson2000.LavaFurnace.util.Config;
import com.google.gson.JsonObject;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public class LavaFurnaceRecipe extends FurnaceRecipe{

    public static final ArrayList<FluidStack> acceptedFluids = getAcceptedFluids();
    public static final ResourceLocation RECIPE_TYPE_ID = new ResourceLocation(LavaFurnace.MOD_ID, "lavafurnacerecipe");

    public LavaFurnaceRecipe(ResourceLocation idIn, String groupIn, Ingredient ingredientIn,
                             ItemStack resultIn, float experienceIn, int cookTimeIn) {
        super(idIn, groupIn, ingredientIn, resultIn, experienceIn, cookTimeIn);
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(BlocksRegistry.LAVA_FURNACE.get());
    }

//    @Override
//    public IRecipeSerializer<?> getSerializer() {
//        return IRecipeSerializer.SMELTING;
//        //return ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation("minecraft", "smelting"));
//    }
//
//    @Nonnull
//    @Override
//    public IRecipeType<?> getType(){
//        return Registry.RECIPE_TYPE.getOrDefault(RECIPE_TYPE_ID);
//    }

    private static ArrayList<FluidStack> getAcceptedFluids(){
        Collection<Fluid> fluidList = ForgeRegistries.FLUIDS.getValues();
        ArrayList<FluidStack> acceptedFluids = new ArrayList<>();

        for(Fluid fluid : fluidList){
            if(Config.isFluidValid(fluid) && fluid.isSource(fluid.getDefaultState()))
                acceptedFluids.add(new FluidStack(fluid, Config.LAVA_FURNACE_TANK_CAPACITY.get()));
        }
        return acceptedFluids;
    }


}

//public class LavaFurnaceRecipe implements ILavaFurnaceRecipe{
//
//    private final ResourceLocation id;
//    private Ingredient input;
//    private FluidStack fluidInput;
//    private final ItemStack output;
//
//    public LavaFurnaceRecipe(ResourceLocation id, Ingredient input, ItemStack output) {
//        this.id = id;
//        this.input = input;
//        this.output = output;
//    }
//
//    @Override
//    public boolean matches(RecipeWrapper inv, World worldIn) {
//
//        if(this.input.test(inv.getStackInSlot(0)) && Config.isFluidValid(fluidInput.getFluid())){
//            return true;
//        }
//
//        return false;
//    }
//
//    @Override
//    public Ingredient getInput() {
//        return this.input;
//    }
//
//    @Override
//    public ItemStack getCraftingResult(RecipeWrapper inv) {
//        return this.output;
//    }
//
//    @Override
//    public ItemStack getRecipeOutput() {
//        return this.output;
//    }
//
//    @Override
//    public ResourceLocation getId() {
//        return this.id;
//    }
//
//    @Override
//    public IRecipeSerializer<?> getSerializer() {
//        return RecipeSerializerRegistry.LAVA_FURNACE_RECIPE_SERIALIZER.get();
//    }
//
//    @Override
//    public NonNullList<Ingredient> getIngredients() {
//        return NonNullList.from(null, this.input);
//    }
//}


//public class LavaFurnaceRecipe implements IRecipe<NoIInventory>, Predicate<ItemStack> {
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
//    public boolean test(@Nonnull ItemStack itemStack) {
//        Ingredient
//        return this.itemInput
//    }
//}
