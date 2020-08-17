package bisson2000.LavaFurnace.util;

import bisson2000.LavaFurnace.LavaFurnace;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

@Mod.EventBusSubscriber
public class Config {

    private static final String CATEGORY_GENERAL = "general";
    private static final String SUBCATEGORY_FURNACE_MODIFIERS = "furnaceModifiers";

    public static final Path CONFIG_DIR;

    public static ForgeConfigSpec COMMON_CONFIG;
    //public static ForgeConfigSpec SERVER_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.BooleanValue SHOW_PARTICLES;

    public static ForgeConfigSpec.IntValue SMELT_SPEED_MODIFIER;
    public static ForgeConfigSpec.IntValue MINIMUM_SMELT_SPEED_MODIFIER;
    public static ForgeConfigSpec.IntValue MAXIMUM_SMELT_SPEED_MODIFIER;
    public static ForgeConfigSpec.IntValue LAVA_FURNACE_TANK_CAPACITY;
    public static ForgeConfigSpec.IntValue LAVA_FURNACE_MB_PER_TICK;
    public static ForgeConfigSpec.BooleanValue LAVA_FURNACE_KEEPS_NBT;

    private static ForgeConfigSpec.ConfigValue<String> BASE_FLUID_MODIFIER_ENTRY;
    private static ForgeConfigSpec.ConfigValue<String> WHITELISTED_FLUIDS_ENTRY;
    private static ForgeConfigSpec.ConfigValue<String> BLACKLISTED_FLUIDS_ENTRY;
    public static Fluid BASE_FLUID_MODIFIER = Fluids.EMPTY;
    public static ArrayList<Fluid> WHITELISTED_FLUIDS = null;
    public static ArrayList<Fluid> BLACKLISTED_FLUIDS = null;

    static {

        CONFIG_DIR = FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(LavaFurnace.class.getSimpleName()), LavaFurnace.class.getSimpleName());

        //ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

        setupCommonConfig(COMMON_BUILDER);
        //setupServerConfig(SERVER_BUILDER);
        setupClientConfig(CLIENT_BUILDER);


        COMMON_CONFIG = COMMON_BUILDER.build();
        //SERVER_CONFIG = SERVER_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    private static void setupCommonConfig(ForgeConfigSpec.Builder COMMON_BUILDER) {

        COMMON_BUILDER.comment("Common Settings").push(CATEGORY_GENERAL);
        COMMON_BUILDER.comment("Lava Furnace Settings").push(SUBCATEGORY_FURNACE_MODIFIERS);

        SMELT_SPEED_MODIFIER = COMMON_BUILDER.comment("\n"+
                "Speed modifier of the Lava Furnace. The higher the value, the faster the furnace will be.\n" +
                "The smelt speed is calculated according to the base fluid temperature that applies the modifier\n" +
                "Example: Lava is the base fluid and the speed modifier is 5x. If a fluid 2x hotter than lava is used, then the speed modifier will be 10x.\n" +
                "Default value: 5")
                .defineInRange("speedModifier", 5, 1, Integer.MAX_VALUE);

        MINIMUM_SMELT_SPEED_MODIFIER = COMMON_BUILDER.comment("\n"+
                "Minimum speed modifier of the Lava Furnace.\n" +
                "Default value: 1")
                .defineInRange("minSpeedModifier", 1, 1, Integer.MAX_VALUE);

        MAXIMUM_SMELT_SPEED_MODIFIER = COMMON_BUILDER.comment("\n"+
                "Maximum speed modifier of the Lava Furnace.\n" +
                "Default value: Integer.MAX_VALUE (2147483647)")
                .defineInRange("maxSpeedModifier", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);

        LAVA_FURNACE_TANK_CAPACITY = COMMON_BUILDER.comment("\n"+
                "Tank capacity of the Lava Furnace.\n" +
                "Default value: 10000")
                .defineInRange("tankCapacity", 10000, 0, Integer.MAX_VALUE);

        BASE_FLUID_MODIFIER_ENTRY = COMMON_BUILDER.comment("\n"+
                "Base fluid that is used to calculate the speed modifier of the Lava Furnace according to its temperature.\n"+
                "A fluid less hot than this one will not smelt items. The registry name must be entered ( \"MODID\":\"fluidName\" ) \n" +
                "Default value: minecraft:lava")
                .define("baseFluid", "minecraft:lava", (entry) -> {
                    if(!(entry instanceof String))
                        return false;
                    try {
                        BASE_FLUID_MODIFIER = handleFluidEntry((String) entry);
                    } catch (IllegalArgumentException | ResourceLocationException e){
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                });

        WHITELISTED_FLUIDS_ENTRY = COMMON_BUILDER.comment("\n"+
                "Additionnal fluids allowed to smelt items. The registry name must be entered ( \"MODID\":\"fluidName\" ) \n" +
                "Default value: minecraft:empty")
                .define("whitelistedFluids", "minecraft:empty", (entry) -> {
                    if(!(entry instanceof String))
                        return false;
                    try {
                        WHITELISTED_FLUIDS = handleFluidEntryList((String) entry);
                    } catch (IllegalArgumentException | ResourceLocationException e){
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                });

        BLACKLISTED_FLUIDS_ENTRY = COMMON_BUILDER.comment("\n"+
                "Fluids not allowed to smelt items. The registry name must be entered ( \"MODID\":\"fluidName\" ) \n" +
                "Default value: minecraft:empty")
                .define("blacklistedFluids", "minecraft:empty", (entry) -> {
                    if(!(entry instanceof String))
                        return false;
                    try {
                    BLACKLISTED_FLUIDS = handleFluidEntryList((String) entry);
                    } catch (IllegalArgumentException | ResourceLocationException e){
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                });

        LAVA_FURNACE_MB_PER_TICK = COMMON_BUILDER.comment("\n"+
                "Number of mB per tick used by the Lava Furnace to smelt.\n" +
                "Default value: 2")
                .defineInRange("lavaFurnaceMBperTick", 1, 0, Integer.MAX_VALUE);

        LAVA_FURNACE_KEEPS_NBT = COMMON_BUILDER.comment("\n"+
                "If the Lava Furnace should keeps its nbt value when broken.\n" +
                "In other words, if the Lava Furnace should keep its tank fluid when broken.\n" +
                "Default value: true")
                .define("lavaFurnaceKeepsNBT", true);

        COMMON_BUILDER.pop();
        COMMON_BUILDER.pop();
    }

    private static void setupServerConfig(ForgeConfigSpec.Builder SERVER_BUILDER) {

    }

    private static void setupClientConfig(ForgeConfigSpec.Builder CLIENT_BUILDER) {
        CLIENT_BUILDER.comment("Client settings").push(CATEGORY_GENERAL);

        SHOW_PARTICLES = CLIENT_BUILDER.comment("Show the particles emitted by the lava furnace").define("showLavaFurnaceParticles", true);

        CLIENT_BUILDER.pop();
    }

    private static Fluid handleFluidEntry(String fluidRegistryIn) throws IllegalArgumentException, ResourceLocationException {
        ResourceLocation fluidResource = new ResourceLocation(fluidRegistryIn);
        Fluid fluid = Fluids.EMPTY;
        if(ForgeRegistries.FLUIDS.containsKey(fluidResource)){
            fluid = ForgeRegistries.FLUIDS.getValue(fluidResource);
        } else {
            throw new IllegalArgumentException("The fluid registry @" + fluidRegistryIn + " does not exist in the ForgeRegistries");
        }
        return fluid;
    }

    private static ArrayList<Fluid> handleFluidEntryList(String fluidRegistryIn) throws IllegalArgumentException, ResourceLocationException {
        ArrayList<Fluid> listedFluids = new ArrayList<>();
        String[] fluidRegistryList = fluidRegistryIn.split(",");
        for(String fluidRegistry : fluidRegistryList){
            Fluid fluid = handleFluidEntry(fluidRegistry);
            listedFluids.add(fluid);
        }
        return listedFluids;
    }

    public static void loadConfig(ForgeConfigSpec spec, String path)
    {
        LavaFurnace.LOGGER.info("Loading config: " + path);
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        LavaFurnace.LOGGER.info("Built config: " + path);
        file.load();
        LavaFurnace.LOGGER.info("Loaded config: " + path);
        spec.setConfig(file);
    }

    public static boolean isFluidValid(Fluid fluidToCheck){
        return (fluidToCheck.getAttributes().getTemperature() >= BASE_FLUID_MODIFIER.getAttributes().getTemperature()
                || WHITELISTED_FLUIDS.contains(fluidToCheck)) && !BLACKLISTED_FLUIDS.contains(fluidToCheck);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {

    }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent) {
    }

}
