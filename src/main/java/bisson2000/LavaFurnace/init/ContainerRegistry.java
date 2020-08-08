package bisson2000.LavaFurnace.init;

import bisson2000.LavaFurnace.LavaFurnace;
import bisson2000.LavaFurnace.containers.LavaFurnaceContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerRegistry {

    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, LavaFurnace.MOD_ID);

    public static final RegistryObject<ContainerType<LavaFurnaceContainer>> LAVA_FURNACE_CONTAINER =
            CONTAINERS.register("lava_furnace", () -> IForgeContainerType.create(LavaFurnaceContainer::createLavaFurnaceContainer));

}
