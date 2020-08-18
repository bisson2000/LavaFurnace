package bisson2000.LavaFurnace.customrecipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Same class as @Link{CookingRecipeSerializer}
 * Since interface IFactory cannot be accessed, it has been recreated here.
 */
public class LavaFurnaceRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<LavaFurnaceRecipe> {

    private final int cookingTime;
    private final LavaFurnaceRecipeSerializer.IFactory factory;

    public LavaFurnaceRecipeSerializer() {
        this.cookingTime = 200;
        factory =  LavaFurnaceRecipe::new;
    }

    @Override
    public LavaFurnaceRecipe read(ResourceLocation recipeId, JsonObject json) {
        String s = JSONUtils.getString(json, "group", "");
        JsonElement jsonelement = (JsonElement)(JSONUtils.isJsonArray(json, "ingredient") ? JSONUtils.getJsonArray(json, "ingredient") : JSONUtils.getJsonObject(json, "ingredient"));
        Ingredient ingredient = Ingredient.deserialize(jsonelement);
        //Forge: Check if primitive string to keep vanilla or a object which can contain a count field.
        if (!json.has("result")) throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
        ItemStack itemstack;
        if (json.get("result").isJsonObject()) itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
        else {
            String s1 = JSONUtils.getString(json, "result");
            ResourceLocation resourcelocation = new ResourceLocation(s1);
            itemstack = new ItemStack(Registry.ITEM.func_241873_b(resourcelocation).orElseThrow(() -> {
                return new IllegalStateException("Item: " + s1 + " does not exist");
            }));
        }
        float f = JSONUtils.getFloat(json, "experience", 0.0F);
        int i = JSONUtils.getInt(json, "cookingtime", this.cookingTime);
        return this.factory.create(recipeId, s, ingredient, itemstack, f, i);

        //return new LavaFurnaceRecipe(IRecipeSerializer.SMELTING.read(recipeId, json));
    }

    public LavaFurnaceRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        String s = buffer.readString(32767);
        Ingredient ingredient = Ingredient.read(buffer);
        ItemStack itemstack = buffer.readItemStack();
        float f = buffer.readFloat();
        int i = buffer.readVarInt();
        return this.factory.create(recipeId, s, ingredient, itemstack, f, i);
    }

    public void write(PacketBuffer buffer, LavaFurnaceRecipe recipe) {
        buffer.writeString(recipe.getGroup());
        recipe.getIngredients().forEach(ingredient -> ingredient.write(buffer));
        buffer.writeItemStack(recipe.getRecipeOutput());
        buffer.writeFloat(recipe.getExperience());
        buffer.writeVarInt(recipe.getCookTime());
    }

    public interface IFactory {
        LavaFurnaceRecipe create(ResourceLocation p_create_1_, String p_create_2_, Ingredient p_create_3_, ItemStack p_create_4_, float p_create_5_, int p_create_6_);
    }

}
