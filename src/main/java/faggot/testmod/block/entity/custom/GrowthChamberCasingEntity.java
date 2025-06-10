package faggot.testmod.block.entity.custom;

import faggot.testmod.block.custom.GrowthChamberCasing;
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
import org.jetbrains.annotations.Nullable;

public class GrowthChamberCasingEntity extends BlockEntity implements GrowthChamberGlassEntity{
    public GrowthChamberCasingEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GROWTH_CHAMBER_CASING_BE, pos, state);
    }

    private BlockPos savedCorePos = null;
    private int coreCount = 0;
    private boolean linkedIndirectly = false;
    private BlockPos linkedViaCasing = null;

    public boolean isLinkedIndirectly() {
        return linkedIndirectly;
    }

    public BlockPos getLinkedViaCasing() {
        return linkedViaCasing;
    }

    public int getCoreCount() {
        return coreCount;
    }

    public BlockPos getSavedCorePos() {
        return savedCorePos;
    }

    public void checkForCoreBlocks() {
        if (savedCorePos != null) {
            BlockEntity coreEntity = world.getBlockEntity(savedCorePos);
            if (coreEntity instanceof GrowthChamberCoreEntity core) {
                core.decrementConnectedCasings();
            }
        }

        // Step 1: Directly check for core via tag
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.isIn(ModTags.Blocks.CORE)) {
                linkToCore(neighborPos, "directly to core", false, null);
                forceNeighborsToCheckCore();
                return;
            }
        }

        // Step 2: No direct core — check neighbor casings or glass entities
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.isIn(ModTags.Blocks.CASING)) {
                BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

                // ✅ Handle both casing and glass entities
                if (neighborEntity instanceof GrowthChamberCasingEntity neighborCasing) {
                    if (tryLinkToNeighborCasing(neighborCasing, neighborPos)) return;
                } else if (neighborEntity instanceof GrowthChamberGlassEntity neighborGlass) {
                    if (tryLinkToNeighborCasing(neighborGlass, neighborPos)) return;
                }
            }
        }

        // Step 3: No core or valid neighbor entity found
        reset();
        sendMessage("Casing at " + pos + " is not connected to any core.");
    }

    private void linkToCore(BlockPos corePos, String messageSource, boolean indirectly, @Nullable BlockPos viaCasing) {
        savedCorePos = corePos;
        coreCount = 1;
        linkedIndirectly = indirectly;
        linkedViaCasing = viaCasing;

        BlockEntity be = world.getBlockEntity(corePos);
        if (be instanceof GrowthChamberCoreEntity coreEntity) {
            coreEntity.incrementConnectedCasings();
            sendMessage("Casing at " + pos + " connected " + messageSource + " at " + corePos +
                    " | Core casing count: " + coreEntity.getConnectedCasings());
            updateMultiblockSize(pos);
        }
    }

    // ✅ Generalized method to handle either casing or glass
    private boolean tryLinkToNeighborCasing(Object neighbor, BlockPos neighborPos) {
        if (neighbor instanceof GrowthChamberCasingEntity casing) {
            if (casing.getCoreCount() == 1) {
                if (casing.isLinkedIndirectly() && casing.getLinkedViaCasing().equals(this.pos)) {
                    forceNeighborsToCheckCoreOnBroken();
                    return false;
                }

                linkToCore(casing.getSavedCorePos(), "via neighbor casing at " + neighborPos, true, neighborPos);
                forceNeighborsToCheckCore();
                return true;
            }
        } else if (neighbor instanceof GrowthChamberGlassEntity glass) {
            if (glass.getCoreCount() == 1) {
                if (glass.isLinkedIndirectly() && glass.getLinkedViaCasing().equals(this.pos)) {
                    forceNeighborsToCheckCoreOnBroken();
                    return false;
                }

                linkToCore(glass.getSavedCorePos(), "via neighbor glass at " + neighborPos, true, neighborPos);
                forceNeighborsToCheckCore();
                return true;
            }
        }
        return false;
    }

    public void forceNeighborsToCheckCore() {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.isIn(ModTags.Blocks.CASING)) {
                BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

                if (neighborEntity instanceof GrowthChamberCasingEntity neighborCasing) {
                    if (neighborCasing.getCoreCount() == 0) {
                        neighborCasing.checkForCoreBlocks();
                    }
                } else if (neighborEntity instanceof GrowthChamberGlassEntity neighborGlass) {
                    if (neighborGlass.getCoreCount() == 0) {
                        neighborGlass.checkForCoreBlocks();
                    }
                }
            }
        }
    }

    public void forceNeighborsToCheckCoreOnBroken() {
        reset();

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.isIn(ModTags.Blocks.CASING)) {
                BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

                if (neighborEntity instanceof GrowthChamberCasingEntity neighborCasing) {
                    neighborCasing.checkForCoreBlocks();
                } else if (neighborEntity instanceof GrowthChamberGlassEntity neighborGlass) {
                    neighborGlass.checkForCoreBlocks();
                }
            }
        }
    }

    public void updateMultiblockSize(BlockPos casingPos) {
        if (savedCorePos == null) return;

        BlockEntity be = world.getBlockEntity(savedCorePos);
        if (be instanceof GrowthChamberCoreEntity coreEntity) {
            coreEntity.updateMultiblockSize(casingPos); // Pass the casing’s position to the core
        }
        coreEnt
    }



    public void reset() {
        savedCorePos = null;
        coreCount = 0;
        linkedIndirectly = false;
        linkedViaCasing = null;
        markDirty();
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
        if (linkedIndirectly && linkedViaCasing != null) {
            nbt.putBoolean("linked_indirectly", true);
            nbt.putInt("linked_x", linkedViaCasing.getX());
            nbt.putInt("linked_y", linkedViaCasing.getY());
            nbt.putInt("linked_z", linkedViaCasing.getZ());
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

        linkedIndirectly = nbt.getBoolean("linked_indirectly");
        if (linkedIndirectly) {
            linkedViaCasing = new BlockPos(
                    nbt.getInt("linked_x"),
                    nbt.getInt("linked_y"),
                    nbt.getInt("linked_z")
            );
        } else {
            linkedViaCasing = null;
        }

        coreCount = nbt.getInt("core_count");
    }
}
