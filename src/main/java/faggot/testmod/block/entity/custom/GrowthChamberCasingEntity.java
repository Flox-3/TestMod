package faggot.testmod.block.entity.custom;

import faggot.testmod.block.entity.ModBlockEntities;
import faggot.testmod.util.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;


public class GrowthChamberCasingEntity extends BlockEntity {
    public GrowthChamberCasingEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GROWTH_CHAMBER_CASING_BE, pos, state);
    }


    private BlockPos savedCorePos = null;
    private int coreCount = 0;

    public int getCoreCount() {
        return coreCount;
    }

    public BlockPos getSavedCorePos() {
        return savedCorePos;
    }


    public void checkForCoreBlocks() {
        BlockPos foundCore = null;

        // Step 1: Directly check for core
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.isIn(ModTags.Blocks.CORE)) {
                foundCore = neighborPos;
                break;
            }
        }

        if (foundCore != null) {
            savedCorePos = foundCore;
            coreCount = 1;

            BlockEntity be = world.getBlockEntity(foundCore);
            if (be instanceof GrowthChamberCoreEntity coreEntity) {
                coreEntity.incrementConnectedCasings();
                sendMessage("Casing at " + pos + " connected directly to core at " + foundCore +
                        " | Core casing count: " + coreEntity.getConnectedCasings());
            }

            return;
        }

        // Step 2: No direct core — check neighbor casings
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

            if (neighborEntity instanceof GrowthChamberCasingEntity neighborCasing) {
                if (neighborCasing.getCoreCount() == 1) {
                    savedCorePos = neighborCasing.getSavedCorePos();
                    coreCount = 1;

                    BlockEntity be = world.getBlockEntity(savedCorePos);
                    if (be instanceof GrowthChamberCoreEntity coreEntity) {
                        coreEntity.incrementConnectedCasings();
                        sendMessage("Casing at " + pos + " linked to core via neighbor casing at " + neighborPos +
                                " | Core casing count: " + coreEntity.getConnectedCasings());
                    }

                    return;
                }
            }
        }

        // Step 3: No core or valid casing found
        savedCorePos = null;
        coreCount = 0;
        sendMessage("Casing at " + pos + " is not connected to any core.");
    }
    private void sendMessage(String msg) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                player.sendMessage(Text.literal(msg), false);
            }
        }
    }





}



