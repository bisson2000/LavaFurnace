package bisson2000.LavaFurnace.init;

import bisson2000.LavaFurnace.LavaFurnace;
import bisson2000.LavaFurnace.customrecipes.LavaFurnaceRecipe;
import bisson2000.LavaFurnace.customrecipes.LavaFurnaceRecipeSerializer;
import bisson2000.LavaFurnace.tileentity.LavaFurnaceTileEntity;
import bisson2000.LavaFurnace.util.LavaFurnaceModGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.*;

public class RecipeSerializerRegistry {

    public static final DeferredRegister<IRecipeSerializer<?>> CUSTOM_RECIPE_SERIALIZERS = DeferredRegister.create(
            ForgeRegistries.RECIPE_SERIALIZERS, LavaFurnace.MOD_ID);



//    public static final RegistryObject<IRecipeSerializer<?>> LAVA_FURNACE_RECIPE_SERIALIZER = getSameRegistryRecipe("minecraft", "smelting");
//
//    private static RegistryObject<IRecipeSerializer<?>> getSameRegistryRecipe(String modID, String path){
//
//        final ResourceLocation searchedLocation = new ResourceLocation(modID, path);
//        return RECIPE_SERIALIZERS.register("test" , () -> ForgeRegistries.RECIPE_SERIALIZERS.getValue(searchedLocation));
//    }

//            RECIPE_SERIALIZERS.register(
//            "smelting", () -> ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation("minecraft", "smelting"))
//    );
//public static final RegistryObject<IRecipeSerializer<?>> SMELTING_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(
//        "smelting", () -> new LavaFurnaceRecipeSerializer()
//);



//    public static final RegistryObject<IRecipeSerializer<?>> SMELTING_RECIPE_SERIALIZER = CUSTOM_RECIPE_SERIALIZERS.register
//            ("smelting", () -> new LavaFurnaceRecipeSerializer());

    private static RegistryObject<IRecipeSerializer<?>>
    RegisterSpecialSerializer(DeferredRegister<IRecipeSerializer<?>> deferredRegister){
        RegistryObject<IRecipeSerializer<?>> result = deferredRegister.register("smelting", () -> IRecipeSerializer.SMELTING);
        return result;
    }

    //Recipe types
    public static final IRecipeType<LavaFurnaceRecipe> LAVA_FURNACE_RECIPE_TYPE = registerType(LavaFurnaceRecipe.RECIPE_TYPE_ID);

    private static class RecipeType<T extends IRecipe<?>> implements IRecipeType<T> {
        @Override
        public String toString() {
            return Registry.RECIPE_TYPE.getKey(this).toString();
        }
    }

    private static <T extends IRecipeType> T registerType(ResourceLocation recipeTypeId) {
        return (T) Registry.register(Registry.RECIPE_TYPE, recipeTypeId, new RecipeType<>());
    }

}
