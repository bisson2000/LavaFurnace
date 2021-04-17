package bisson2000.LavaFurnace.blocks;

import bisson2000.LavaFurnace.LavaFurnace;
import bisson2000.LavaFurnace.util.Config;
import bisson2000.LavaFurnace.init.TileEntityRegistry;
import bisson2000.LavaFurnace.tileentity.LavaFurnaceTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.Random;
import java.util.stream.Stream;

public class LavaFurnaceBlock extends Block {

    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty HAS_HOT_FLUID = BooleanProperty.create("hashotfluid");
    public static final BooleanProperty IS_EMPTY = BooleanProperty.create("issempty");

    private static final VoxelShape SHAPE_N = Stream.of(
            Block.makeCuboidShape(15, 15, 1, 16, 16, 15),
            Block.makeCuboidShape(15, 0, 15, 16, 16, 16),
            Block.makeCuboidShape(15, 0, 0, 16, 16, 1),
            Block.makeCuboidShape(12, 1, 0, 15, 15, 0),
            Block.makeCuboidShape(12, 1, 16, 15, 15, 16),
            Block.makeCuboidShape(12, 0, 15, 15, 1, 16),
            Block.makeCuboidShape(12, 15, 15, 15, 16, 16),
            Block.makeCuboidShape(12, 0, 0, 15, 1, 1),
            Block.makeCuboidShape(12, 15, 0, 15, 16, 1),
            Block.makeCuboidShape(15, 0, 1, 16, 1, 15),
            Block.makeCuboidShape(16, 1, 1, 16, 15, 15),
            Block.makeCuboidShape(12, 0, 1, 15, 0, 15),
            Block.makeCuboidShape(0, 0, 0, 12, 16, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_E = Stream.of(
            Block.makeCuboidShape(1, 15, 15, 15, 16, 16),
            Block.makeCuboidShape(0, 0, 15, 1, 16, 16),
            Block.makeCuboidShape(15, 0, 15, 16, 16, 16),
            Block.makeCuboidShape(16, 1, 12, 16, 15, 15),
            Block.makeCuboidShape(0, 1, 12, 0, 15, 15),
            Block.makeCuboidShape(0, 0, 12, 1, 1, 15),
            Block.makeCuboidShape(0, 15, 12, 1, 16, 15),
            Block.makeCuboidShape(15, 0, 12, 16, 1, 15),
            Block.makeCuboidShape(15, 15, 12, 16, 16, 15),
            Block.makeCuboidShape(1, 0, 15, 15, 1, 16),
            Block.makeCuboidShape(1, 1, 16, 15, 15, 16),
            Block.makeCuboidShape(1, 0, 12, 15, 0, 15),
            Block.makeCuboidShape(0, 0, 0, 16, 16, 12)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_S = Stream.of(
            Block.makeCuboidShape(0, 15, 1, 1, 16, 15),
            Block.makeCuboidShape(0, 0, 0, 1, 16, 1),
            Block.makeCuboidShape(0, 0, 15, 1, 16, 16),
            Block.makeCuboidShape(1, 1, 16, 4, 15, 16),
            Block.makeCuboidShape(1, 1, 0, 4, 15, 0),
            Block.makeCuboidShape(1, 0, 0, 4, 1, 1),
            Block.makeCuboidShape(1, 15, 0, 4, 16, 1),
            Block.makeCuboidShape(1, 0, 15, 4, 1, 16),
            Block.makeCuboidShape(1, 15, 15, 4, 16, 16),
            Block.makeCuboidShape(0, 0, 1, 1, 1, 15),
            Block.makeCuboidShape(0, 1, 1, 0, 15, 15),
            Block.makeCuboidShape(1, 0, 1, 4, 0, 15),
            Block.makeCuboidShape(4, 0, 0, 16, 16, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_W = Stream.of(
            Block.makeCuboidShape(1, 15, 0, 15, 16, 1),
            Block.makeCuboidShape(15, 0, 0, 16, 16, 1),
            Block.makeCuboidShape(0, 0, 0, 1, 16, 1),
            Block.makeCuboidShape(0, 1, 1, 0, 15, 4),
            Block.makeCuboidShape(16, 1, 1, 16, 15, 4),
            Block.makeCuboidShape(15, 0, 1, 16, 1, 4),
            Block.makeCuboidShape(15, 15, 1, 16, 16, 4),
            Block.makeCuboidShape(0, 0, 1, 1, 1, 4),
            Block.makeCuboidShape(0, 15, 1, 1, 16, 4),
            Block.makeCuboidShape(1, 0, 0, 15, 1, 1),
            Block.makeCuboidShape(1, 1, 0, 15, 15, 0),
            Block.makeCuboidShape(1, 0, 1, 15, 0, 4),
            Block.makeCuboidShape(0, 0, 4, 16, 16, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    public LavaFurnaceBlock() {

        super(Properties.create(Material.IRON)
                .hardnessAndResistance(5.0f, 6.0f)
                .sound(SoundType.METAL)
                .harvestLevel(1)
                .harvestTool(ToolType.PICKAXE)
        );
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING,
                Direction.NORTH).with(LIT, Boolean.FALSE)
                .with(HAS_HOT_FLUID, Boolean.FALSE).with(IS_EMPTY, Boolean.TRUE));
        if (Config.LAVA_FURNACE_KEEPS_NBT.get())
            this.lootTable = new ResourceLocation(LavaFurnace.MOD_ID, "blocks/lava_furnace_nbt");
        else
            this.lootTable = new ResourceLocation(LavaFurnace.MOD_ID, "blocks/lava_furnace");
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        switch (state.get(FACING)) {
            case EAST:
                return SHAPE_E;
            case SOUTH:
                return SHAPE_S;
            case WEST:
                return SHAPE_W;
            default:
                return SHAPE_N;
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return TileEntityRegistry.LAVA_FURNACE_TILE_ENTITY.get().create();
    }

    /**
     * If Networkhooks is not used, PacketBuffer will be null and the gui will not work
     * {@link bisson2000.LavaFurnace.containers.LavaFurnaceContainer#getTileEntity(PlayerInventory, PacketBuffer)}
     *
     * @param state
     * @param worldIn
     * @param pos
     * @param player
     * @param handIn
     * @param hit
     * @return
     */
    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        if (worldIn.isRemote) {
            return ActionResultType.CONSUME;
        }

        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof LavaFurnaceTileEntity) {

            boolean showGUI = true;
            if (player.getHeldItem(handIn).getItem() instanceof BucketItem)
                showGUI = !FluidUtil.interactWithFluidHandler(player, handIn, worldIn, pos, hit.getFace());
            if (showGUI)
                NetworkHooks.openGui((ServerPlayerEntity) player, (LavaFurnaceTileEntity) tileentity, pos);
            player.addStat(Stats.INTERACT_WITH_FURNACE);
            return ActionResultType.SUCCESS;

        }

        return ActionResultType.FAIL;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.isIn(newState.getBlock())) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof LavaFurnaceTileEntity) {
                tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(h -> {
                    for (int i = 0; i < h.getSlots(); i++)
                        InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), h.getStackInSlot(i));
                });
                worldIn.updateComparatorOutputLevel(pos, this);
            }

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, HAS_HOT_FLUID, IS_EMPTY);
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (stack.hasDisplayName()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof LavaFurnaceTileEntity) {
                ((LavaFurnaceTileEntity) tileentity).setCustomName(stack.getDisplayName());
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {

        LavaFurnaceTileEntity te;
        if (!(worldIn.getTileEntity(pos) instanceof LavaFurnaceTileEntity))
            return;
        else
            te = (LavaFurnaceTileEntity) worldIn.getTileEntity(pos);

        if (stateIn.get(LIT) && rand.nextDouble() < 0.7) {

            final float FRAME_THICKNESS = 1.0f / 16.0f;
            final float FURNACE_THICKNESS = 12.0f / 16.0f;
            final float SCALE = Math.min((1.0f - FRAME_THICKNESS / 2 - FRAME_THICKNESS) * te.getFluidTank().getFluidAmount() / (te.getFluidTank().getCapacity()), 1.0f);

            double baseX = pos.getX() + FURNACE_THICKNESS;
            double baseY = pos.getY();
            double baseZ = pos.getZ() + FRAME_THICKNESS;

            if (rand.nextDouble() < 0.1D) {
                worldIn.playSound(baseX, baseY, baseZ, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }
            if (rand.nextDouble() < 0.1D) {
                worldIn.playSound(baseX, baseY, baseZ, SoundEvents.BLOCK_LAVA_POP, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
            }

            Direction direction = stateIn.get(FACING);
            double d3 = rand.nextDouble() * (1.0f - FRAME_THICKNESS - FURNACE_THICKNESS);
            double d4 = SCALE + FRAME_THICKNESS / 4;
            double d5 = rand.nextDouble() * (1.0f - FRAME_THICKNESS);

            switch (direction) {
                case NORTH:
                    break;
                case EAST:
                    baseX = pos.getX() + FRAME_THICKNESS;
                    d3 = rand.nextDouble() * (1.0f - FRAME_THICKNESS);
                    baseZ = pos.getZ() + FURNACE_THICKNESS;
                    d5 = rand.nextDouble() * (1.0f - FRAME_THICKNESS - FURNACE_THICKNESS);
                    break;
                case SOUTH:
                    baseX = pos.getX() + FRAME_THICKNESS;
                    break;
                case WEST:
                    baseX = pos.getX() + FRAME_THICKNESS;
                    d3 = rand.nextDouble() * (1.0f - FRAME_THICKNESS);
                    baseZ = pos.getZ() + FRAME_THICKNESS;
                    d5 = rand.nextDouble() * (1.0f - FRAME_THICKNESS - FURNACE_THICKNESS);
                    break;
            }

            if (Config.SHOW_PARTICLES.get()) {
                worldIn.addParticle(ParticleTypes.SMOKE, baseX + d3, baseY + d4, baseZ + d5, 0.0D, 0.02D, 0.0D);
                worldIn.addParticle(ParticleTypes.FLAME, baseX + d3, baseY + d4, baseZ + d5, 0.0D, 0.02D, 0.0D);
            }
        }

    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        if (state.get(HAS_HOT_FLUID)) {
            return 15;
        }
        return 0;
    }

}
