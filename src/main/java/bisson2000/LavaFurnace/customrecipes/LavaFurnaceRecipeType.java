//package bisson2000.LavaFurnace.customrecipes;
//
//import bisson2000.LavaFurnace.LavaFurnace;
//import bisson2000.LavaFurnace.init.RecipeSerializerRegistry;
//import net.minecraft.item.crafting.AbstractCookingRecipe;
//import net.minecraft.item.crafting.FurnaceRecipe;
//import net.minecraft.item.crafting.IRecipe;
//import net.minecraft.item.crafting.IRecipeType;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.registry.Registry;
//
//import java.util.Collections;
//import java.util.List;
//
//public class LavaFurnaceRecipeType<T extends IRecipe<?>> implements IRecipeType<T> {
//
//    public static final LavaFurnaceRecipeType<LavaFurnaceRecipe> LAVA_FURNACE_RECIPE_TYPE = registerType( , IRecipeType.SMELTING);
//
//    public LavaFurnaceRecipeType(){
//    }
//
//
//
//    public static <T extends IRecipeType> T registerType(ResourceLocation recipeTypeId) {
//        return (T) Registry.register(Registry.RECIPE_TYPE, recipeTypeId, new LavaFurnaceRecipeType<T>());
//    }
//
//    static <T extends IRecipe<?>> IRecipeType<T> registerType(final String key, IRecipeType r) {
//        return Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(key), new LavaFurnaceRecipeType<>());
//    }
//
//    @Override
//    public String toString() {
//        return Registry.RECIPE_TYPE.getKey(this).toString();
//    }
//
//}
