package faggot.testmod.block.custom;

import com.mojang.serialization.MapCodec;
import faggot.testmod.block.entity.custom.GrowthChamberCoreEntity;
import faggot.testmod.block.entity.custom.GrowthChamberGlassEntity;
import faggot.testmod.util.ModTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GrowthChamberGlass extends BlockWithEntity implements BlockEntityProvider {
    public GrowthChamberGlass(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
                                             PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof GrowthChamberGlassEntity glass) {
                glass.checkForCoreBlocks();
            }
        }
        return ItemActionResult.SUCCESS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state,
                         LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof GrowthChamberGlassEntity glass) {
                glass.checkForCoreBlocks();
                glass.forceNeighborsToCheckCore();
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof GrowthChamberGlassEntity glass) {
                BlockPos corePos = glass.getCorePos();

                if (corePos != null) {
                    BlockEntity coreEntity = world.getBlockEntity(corePos);
                    if (coreEntity instanceof GrowthChamberCoreEntity core) {
                        core.decrementConnectedCasings();
                    }
                }
                if (!world.isClient) {
                    glass.forceNeighborsToCheckCoreOnBroken();
                }
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos,
                               Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);

        if (!world.isClient) {
            if (block.getDefaultState().isIn(ModTags.Blocks.CORE) || block.getDefaultState().isIn(ModTags.Blocks.CASING)) {
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof GrowthChamberGlassEntity glass) {
                    glass.checkForCoreBlocks();
                }
            }
        }
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GrowthChamberGlassEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
