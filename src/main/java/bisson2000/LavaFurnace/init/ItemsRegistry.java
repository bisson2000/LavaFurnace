package bisson2000.LavaFurnace.init;

import bisson2000.LavaFurnace.LavaFurnace;
import bisson2000.LavaFurnace.util.LavaFurnaceModGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemsRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LavaFurnace.MOD_ID);

    //Items

    //Block Items
    public static final RegistryObject<Item> LAVA_FURNACE_ITEM = ITEMS.register("lava_furnace", () ->
            new BlockItem(BlocksRegistry.LAVA_FURNACE.get(), new Item.Properties().group(LavaFurnaceModGroup.TAB)));


}
