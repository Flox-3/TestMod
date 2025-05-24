package faggot.testmod.block.entity.custom;

import faggot.testmod.block.entity.ModBlockEntities;
import faggot.testmod.util.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
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
        // Step 1: Directly check for core
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.isIn(ModTags.Blocks.CORE)) {
                linkToCore(neighborPos, "directly to core");
                forceNeighborsToCheckCore();
                return;
            }
        }

        // Step 2: No direct core — check neighbor casings
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

            if (neighborEntity instanceof GrowthChamberCasingEntity neighborCasing) {
                if (tryLinkToNeighborCasing(neighborCasing, neighborPos)) return;
            }
        }

        // Step 3: No core or valid casing found
        savedCorePos = null;
        coreCount = 0;
        sendMessage("Casing at " + pos + " is not connected to any core.");
    }

    private void linkToCore(BlockPos corePos, String messageSource) {
        savedCorePos = corePos;
        coreCount = 1;

        BlockEntity be = world.getBlockEntity(corePos);
        if (be instanceof GrowthChamberCoreEntity coreEntity) {
            coreEntity.incrementConnectedCasings();
            sendMessage("Casing at " + pos + " connected " + messageSource + " at " + corePos +
                    " | Core casing count: " + coreEntity.getConnectedCasings());
        }
    }

    private boolean tryLinkToNeighborCasing(GrowthChamberCasingEntity neighborCasing, BlockPos neighborPos) {
        if (neighborCasing.getCoreCount() == 1) {
            linkToCore(neighborCasing.getSavedCorePos(), "via neighbor casing at " + neighborPos);
            forceNeighborsToCheckCore();
            return true;
        }
        return false;
    }

    public void forceNeighborsToCheckCore() {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

            if (neighborEntity instanceof GrowthChamberCasingEntity neighborCasing) {
                if (neighborCasing.getCoreCount() == 0) {
                    neighborCasing.checkForCoreBlocks();
                }
            }
        }
    }

    public void forceNeighborsToCheckCoreOnBroken() {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

            if (neighborEntity instanceof GrowthChamberCasingEntity neighborCasing) {
                if (neighborCasing.getCoreCount() == 1) {
                    neighborCasing.checkForCoreBlocks();
                }
            }
        }
    }


    private void sendMessage(String msg) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                player.sendMessage(Text.literal(msg), false);
            }
        }
    }


    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        if (savedCorePos != null) {
            nbt.putInt("core_x", savedCorePos.getX());
            nbt.putInt("core_y", savedCorePos.getY());
            nbt.putInt("core_z", savedCorePos.getZ());
        }
        nbt.putInt("core_count", coreCount);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        if (nbt.contains("core_x")) {
            savedCorePos = new BlockPos(
                    nbt.getInt("core_x"),
                    nbt.getInt("core_y"),
                    nbt.getInt("core_z")
            );
        } else {
            savedCorePos = null;
        }

        coreCount = nbt.getInt("core_count");
    }




}



