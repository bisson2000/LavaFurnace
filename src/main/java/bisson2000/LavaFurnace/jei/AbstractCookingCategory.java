package bisson2000.LavaFurnace.jei;

import bisson2000.LavaFurnace.tileentity.LavaFurnaceTileEntity;
import bisson2000.LavaFurnace.util.Config;
import bisson2000.LavaFurnace.LavaFurnace;
import bisson2000.LavaFurnace.gui.LavaFurnaceScreen;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractCookingCategory<T extends AbstractCookingRecipe> implements IRecipeCategory<T> {

    protected static final int inputSlot = 0;
    protected static final int fuelSlot = 1;
    protected static final int outputSlot = 2;
    protected static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(LavaFurnace.MOD_ID, "textures/gui/lava_furnace_gui.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    protected final IDrawableAnimated arrow;
    protected final IDrawableAnimated animatedFlame;
    protected final IDrawableStatic staticFlame;
    protected final IDrawableStatic linesOverFluid;


    public AbstractCookingCategory(IGuiHelper guiHelper, Block icon, String translationKey, int regularCookTime) {

        this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, 13, 8, 124, 61);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(icon));
        this.localizedName = I18n.format(translationKey);
        this.staticFlame = guiHelper.createDrawable(BACKGROUND_TEXTURE, 176, 0, 14, 14);
        this.animatedFlame = guiHelper.createAnimatedDrawable(staticFlame, 300, IDrawableAnimated.StartDirection.TOP, true);
        this.linesOverFluid = guiHelper.createDrawable(BACKGROUND_TEXTURE, 180 + 1, 35 + 1, 20 - 1, 61 - 1);
        this.arrow = guiHelper.drawableBuilder(BACKGROUND_TEXTURE, 176, 14, 24, 17)
                .buildAnimated(regularCookTime, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(T recipe, IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.FLUID, AcceptedFluids.getAcceptedFluids(recipe));
        //Items
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
    }

    @Override
    public void draw(T recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        animatedFlame.draw(matrixStack, 57 - 13, 37 - 8);
        arrow.draw(matrixStack, 79 - 13, 35 - 8);

        float experience = recipe.getExperience();
        if (experience > 0) {
            TranslationTextComponent experienceString = new TranslationTextComponent("gui.jei.category.smelting.experience", experience);
            Minecraft minecraft = Minecraft.getInstance();
            FontRenderer fontRenderer = minecraft.fontRenderer;
            int stringWidth = fontRenderer.func_238414_a_(experienceString);
            fontRenderer.func_238422_b_(matrixStack, experienceString.func_241878_f(), background.getWidth() - stringWidth, 0, 0xFF808080);
        }

//        //Fluid tank
//        if(timer.getValue() != lastTimerValue){
//            lastTimerValue = timer.getValue();
//            displayedFluid = new FluidStack(getRandomAcceptedFluids(), Config.LAVA_FURNACE_TANK_CAPACITY.get());
//        }
//
//        //FluidStack displayedFluid = new FluidStack(Config.BASE_FLUID_MODIFIER, Config.LAVA_FURNACE_TANK_CAPACITY.get());
//        LavaFurnaceScreen.drawFluidInTank(matrixStack, displayedFluid,
//                Config.LAVA_FURNACE_TANK_CAPACITY.get(), 1, 1, 18, 59);
//        linesOverFluid.draw(matrixStack, 0, 0);
//
        //Fluid tooltip
//        List<ITextComponent> tooltip = new ArrayList<>();
//        LavaFurnaceScreen.drawToolTip(new FluidStack(Fluids.LAVA, 10000), tooltip, Config.LAVA_FURNACE_TANK_CAPACITY.get(),
//                0, 0 ,18, 59, (int) mouseX, (int) mouseY);
//        if(!tooltip.isEmpty() && Minecraft.getInstance().currentScreen != null)
//            Minecraft.getInstance().currentScreen.renderToolTip(matrixStack,
//                    tooltip.stream().map(LanguageMap.getInstance()::func_241870_a).collect(ImmutableList.toImmutableList()),
//                    (int) mouseX, (int) mouseY, Minecraft.getInstance().fontRenderer);

//        if (!tooltip.isEmpty())
//            GuiUtils.drawHoveringText(matrixStack, tooltip, (int) mouseX, (int) mouseY, 124, 61, -1,
//                    Minecraft.getInstance().fontRenderer);

    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, T recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

        guiItemStacks.init(inputSlot, true, 56 - 13 - 1, 17 - 8 - 1);
        guiFluidStacks.init(fuelSlot, false, 14 - 13, 9 - 8, 18, 59, 10000, false, linesOverFluid);
        guiItemStacks.init(outputSlot, false, 116 - 13 - 1, 35 - 8 - 1);

        guiItemStacks.set(ingredients);
        guiFluidStacks.set(fuelSlot, AcceptedFluids.getAcceptedFluids(recipe));
    }

    //TODO: replace these methods by implementing a custom Irecipe for the LavaFurnaceBlock
    //      This is not the best way to do it, but it works for now
    //      All methods below are associated with this implementation

    private static abstract class AcceptedFluids {

        private static int lastRecipeCookTime = -1;
        private static final ArrayList<FluidStack> acceptedFluids = new ArrayList<>();

        public static <T extends AbstractCookingRecipe > ArrayList < FluidStack > getAcceptedFluids(T recipe) {

            if (recipe.getCookTime() == lastRecipeCookTime)
                return acceptedFluids;

            acceptedFluids.clear();
            lastRecipeCookTime = recipe.getCookTime();
            Collection<Fluid> fluidList = ForgeRegistries.FLUIDS.getValues();

            for (Fluid fluid : fluidList) {
                if (Config.isFluidValid(fluid) && fluid.isSource(fluid.getDefaultState()))
                    acceptedFluids.add(new FluidStack(fluid, getCookTime(fluid, recipe)));
            }
            return acceptedFluids;
        }

        /**
         * O(1) complexity, does not slow down operation
         *
         * @param fluid
         * @param recipe
         * @param <T>
         * @return
         */
        public static <T extends AbstractCookingRecipe > int getCookTime (Fluid fluid, T recipe){
            if (fluid == null || recipe == null || fluid.isEquivalentTo(Fluids.EMPTY))
                return Integer.MAX_VALUE;

            int tankTemperature = fluid.getAttributes().getTemperature();
            int configTemperature = Math.max(Config.BASE_FLUID_MODIFIER.getAttributes().getTemperature(), 1);
            int cookTimeModifier = MathHelper.clamp(tankTemperature / configTemperature * Config.SMELT_SPEED_MODIFIER.get(),
                    Config.MINIMUM_SMELT_SPEED_MODIFIER.get(), Config.MAXIMUM_SMELT_SPEED_MODIFIER.get());

            return Math.max((int) (recipe.getCookTime() / cookTimeModifier), 1);
        }
    }

}
