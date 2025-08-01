package faggot.testmod.block.custom;

import com.mojang.serialization.MapCodec;
import faggot.testmod.block.entity.ModBlockEntities;
import faggot.testmod.block.entity.custom.GrowthChamberCasingEntity;
import faggot.testmod.block.entity.custom.GrowthChamberCoreEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GrowthChamberCore extends BlockWithEntity implements BlockEntityProvider {
    public static final MapCodec<GrowthChamberCore> CODEC = GrowthChamberCore.createCodec(GrowthChamberCore::new);

    public GrowthChamberCore(Settings settings) {
        super(settings);
    }

    //Multiblock scheiße
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state,
                         LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof GrowthChamberCoreEntity core) {
                core.forceNeighborsToCheckForCore();
            }
        }
    }



    //rest oder so ka
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GrowthChamberCoreEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GrowthChamberCoreEntity coreEntity) {
                coreEntity.reset(); // <-- Call reset here
                ItemScatterer.spawn(world, pos, coreEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
                                             PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof GrowthChamberCoreEntity core) {
                if (!core.isInitialized) {
                    core.initializeMultiblock();
                    return ItemActionResult.SUCCESS;
                } else {
                    NamedScreenHandlerFactory screenHandlerFactory = core;
                    if (screenHandlerFactory != null) {
                        player.openHandledScreen(screenHandlerFactory);
                    }
                    return ItemActionResult.SUCCESS;
                }
            }
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if(world.isClient()) {
            return null;
        }

        return validateTicker(type, ModBlockEntities.GROWTH_CHAMBER_CORE_BE,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
}
