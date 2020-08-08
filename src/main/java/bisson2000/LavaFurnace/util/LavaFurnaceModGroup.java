package bisson2000.LavaFurnace.util;

import bisson2000.LavaFurnace.init.ItemsRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class LavaFurnaceModGroup extends ItemGroup {

    public static final LavaFurnaceModGroup TAB = new LavaFurnaceModGroup();

    public LavaFurnaceModGroup() {
        super("lavaFurnaceModTab");
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ItemsRegistry.LAVA_FURNACE_ITEM.get());
    }
}
