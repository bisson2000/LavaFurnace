package bisson2000.LavaFurnace;

import bisson2000.LavaFurnace.init.*;
import bisson2000.LavaFurnace.util.ClientSetup;
import bisson2000.LavaFurnace.util.Config;
import bisson2000.LavaFurnace.util.ModSetup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(LavaFurnace.MOD_ID)
public class LavaFurnace {

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "lavafurnace";


    public LavaFurnace() {

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG,  Config.CONFIG_DIR + "/lavafurnace-common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG, Config.CONFIG_DIR + "/lavafurnace-client.toml");
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(Config.CONFIG_DIR + "/lavafurnace-common.toml").toString()); //Prevents crash when first loading the game
        Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(Config.CONFIG_DIR + "/lavafurnace-client.toml").toString()); //Prevents crash when first loading the game


        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientStuff);

        BlocksRegistry.BLOCKS.register(modEventBus);
        ItemsRegistry.ITEMS.register(modEventBus);
        TileEntityRegistry.TILE_ENTITIES.register(modEventBus);
        ContainerRegistry.CONTAINERS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        ModSetup.init(event);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ClientSetup.init(event);
    }


}
