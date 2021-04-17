package bisson2000.LavaFurnace.jei;


import bisson2000.LavaFurnace.LavaFurnace;
import bisson2000.LavaFurnace.containers.LavaFurnaceContainer;
import bisson2000.LavaFurnace.gui.LavaFurnaceScreen;
import bisson2000.LavaFurnace.init.BlocksRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

@JeiPlugin
public class LavaFurnaceJEI implements IModPlugin {

    private static final ClientWorld world = Minecraft.getInstance().world;
    public static final ResourceLocation LAVAFURNACE_ID = BlocksRegistry.LAVA_FURNACE.get().getRegistryName();


    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(LavaFurnace.MOD_ID, "jei_plugin_" + LavaFurnace.MOD_ID);
    }

    //Register categories
    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new LavaFurnaceCategory(guiHelper));

    }

    //Register recipes for categories
    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        RecipeRegistryHandler(registry, LAVAFURNACE_ID, IRecipeType.SMELTING);
    }

    //Register which categories a block will be associated with in jei
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        CatalystRegistryHandler(registry, BlocksRegistry.LAVA_FURNACE.get() , VanillaRecipeCategoryUid.FURNACE);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registry) {
        registry.addRecipeTransferHandler(LavaFurnaceContainer.class, LAVAFURNACE_ID, 0, 1, 2, 36);
    }

    //Tells which jei categories will be opened on clicked area
    // VanillaRecipeCategoryUid.FURNACE is not added
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(LavaFurnaceScreen.class, 79, 35, 24, 17, LAVAFURNACE_ID);
    }

    public static void CatalystRegistryHandler(IRecipeCatalystRegistration registry, Block blockProvider, ResourceLocation... additionalCategories) {
        ResourceLocation[] categories = new ResourceLocation[additionalCategories.length + 1];
        categories[0] = blockProvider.getRegistryName();
        System.arraycopy(additionalCategories, 0, categories, 1, additionalCategories.length);
        registry.addRecipeCatalyst(new ItemStack(blockProvider), categories);
    }

    public static <T extends AbstractCookingRecipe> void RecipeRegistryHandler(IRecipeRegistration registry, ResourceLocation id, IRecipeType<T> type) {
        world.getRecipeManager().func_241447_a_(type); //getRecipes
        List<T> recipes = world.getRecipeManager().func_241447_a_(type);
        registry.addRecipes(recipes, id);
    }


}
