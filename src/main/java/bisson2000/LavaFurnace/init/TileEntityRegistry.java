package bisson2000.LavaFurnace.init;

import bisson2000.LavaFurnace.LavaFurnace;
import bisson2000.LavaFurnace.tileentity.LavaFurnaceTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityRegistry {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, LavaFurnace.MOD_ID);

    //Tile entities
    public static final RegistryObject<TileEntityType<LavaFurnaceTileEntity>> LAVA_FURNACE_TILE_ENTITY =
            TILE_ENTITIES.register("lava_furnace", () ->
                    TileEntityType.Builder.create(LavaFurnaceTileEntity::new, BlocksRegistry.LAVA_FURNACE.get()).build(null));

}
