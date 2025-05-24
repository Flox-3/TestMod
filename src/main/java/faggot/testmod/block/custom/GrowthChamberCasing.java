package faggot.testmod.block.custom;

import com.mojang.serialization.MapCodec;
import faggot.testmod.block.entity.custom.GrowthChamberCasingEntity;
import faggot.testmod.block.entity.custom.GrowthChamberCoreEntity;
import faggot.testmod.util.ModTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GrowthChamberCasing extends BlockWithEntity implements BlockEntityProvider {
    public GrowthChamberCasing(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }



    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state,
                         LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof GrowthChamberCasingEntity casing) {
                casing.checkForCoreBlocks();
                casing.forceNeighborsToCheckCore();
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof GrowthChamberCasingEntity casing) {
                BlockPos corePos = casing.getSavedCorePos();

                if (corePos != null) {
                    BlockEntity coreEntity = world.getBlockEntity(corePos);
                    if (coreEntity instanceof GrowthChamberCoreEntity core) {
                        core.decrementConnectedCasings();
                    } else {
                        if (!world.isClient) {
                            BlockEntity be = world.getBlockEntity(pos);
                            if (be instanceof GrowthChamberCasingEntity) {
                                casing.forceNeighborsToCheckCoreOnBroken();
                            }
                        }

                    }
                }
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }




    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GrowthChamberCasingEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }


}
