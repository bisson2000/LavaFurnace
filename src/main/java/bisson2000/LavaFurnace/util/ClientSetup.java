package bisson2000.LavaFurnace.util;

import bisson2000.LavaFurnace.LavaFurnace;
import bisson2000.LavaFurnace.gui.LavaFurnaceScreen;
import bisson2000.LavaFurnace.init.BlocksRegistry;
import bisson2000.LavaFurnace.init.ContainerRegistry;
import bisson2000.LavaFurnace.client.render.*;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = LavaFurnace.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static void init(final FMLClientSetupEvent event) {

        ScreenManager.registerFactory(ContainerRegistry.LAVA_FURNACE_CONTAINER.get(), LavaFurnaceScreen::new);

        LavaFurnaceRenderer.register();

        RenderTypeLookup.setRenderLayer(BlocksRegistry.LAVA_FURNACE.get(), RenderType.getCutout());

    }

}
