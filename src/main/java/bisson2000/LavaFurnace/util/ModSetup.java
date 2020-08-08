package bisson2000.LavaFurnace.util;

import bisson2000.LavaFurnace.LavaFurnace;
import bisson2000.LavaFurnace.network.Networking;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = LavaFurnace.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetup {

    public static void init(final FMLCommonSetupEvent event){
        Networking.registerMessages();
    }

}
