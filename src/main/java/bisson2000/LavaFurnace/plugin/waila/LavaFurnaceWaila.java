package bisson2000.LavaFurnace.plugin.waila;


import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.tileentity.TileEntity;

@WailaPlugin
public class LavaFurnaceWaila implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.registerBlockDataProvider(LavaFurnaceBlockWaila.INSTANCE, TileEntity.class);
        registrar.registerComponentProvider(LavaFurnaceBlockWaila.INSTANCE, TooltipPosition.BODY, TileEntity.class);
        registrar.registerTooltipRenderer(LavaFurnaceBlockWaila.WailaToolTip, LavaFurnaceBlockWaila.INSTANCE);
    }


}
