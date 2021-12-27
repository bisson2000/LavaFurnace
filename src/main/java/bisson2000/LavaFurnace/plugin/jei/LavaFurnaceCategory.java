package bisson2000.LavaFurnace.plugin.jei;

import bisson2000.LavaFurnace.util.Config;
import bisson2000.LavaFurnace.init.BlocksRegistry;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.util.ResourceLocation;

public class LavaFurnaceCategory extends AbstractCookingCategory<FurnaceRecipe> {

    private static final String translationKey = "gui.lavafurnace.smelting"; //Name displayed for the Icon
    private static final int regularCookTime = Math.max( 200 / Config.SMELT_SPEED_MODIFIER.get(), 1);

    public LavaFurnaceCategory(IGuiHelper guiHelper) {
        super(guiHelper, BlocksRegistry.LAVA_FURNACE.get(), translationKey, regularCookTime);
    }

    @Override
    public ResourceLocation getUid() {
        return LavaFurnaceJEI.LAVAFURNACE_ID;
    }

    @Override
    public Class<? extends FurnaceRecipe> getRecipeClass() {
        return FurnaceRecipe.class;
    }
}
