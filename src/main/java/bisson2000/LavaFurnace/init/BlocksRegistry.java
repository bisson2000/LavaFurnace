package bisson2000.LavaFurnace.init;

import bisson2000.LavaFurnace.LavaFurnace;
import bisson2000.LavaFurnace.blocks.LavaFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlocksRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, LavaFurnace.MOD_ID);

    //Blocks
    public static final RegistryObject<Block> LAVA_FURNACE = BLOCKS.register("lava_furnace", LavaFurnaceBlock::new);


}
