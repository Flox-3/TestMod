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
import org.jetbrains.annotations.Nullable;

public class GrowthChamberGlassEntity extends BlockEntity {
    public GrowthChamberGlassEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GROWTH_CHAMBER_GLASS_BE, pos, state);
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

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.isIn(ModTags.Blocks.CORE)) {
                linkToCore(neighborPos, "directly to core", false, null);
                forceNeighborsToCheckCore();
                return;
            }
        }

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.isIn(ModTags.Blocks.CASING)) {
                BlockEntity neighborEntity = world.getBlockEntity(neighborPos);
                if (neighborEntity instanceof GrowthChamberCasingEntity casing) {
                    if (tryLinkToNeighborCasing(casing, neighborPos)) return;
                } else if (neighborEntity instanceof GrowthChamberGlassEntity glass) {
                    if (tryLinkToNeighborGlass(glass, neighborPos)) return;
                }
            }
        }

        savedCorePos = null;
        coreCount = 0;
        sendMessage("Glass casing at " + pos + " is not connected to any core.");
    }

    private void linkToCore(BlockPos corePos, String messageSource, boolean indirectly, @Nullable BlockPos viaCasing) {
        savedCorePos = corePos;
        coreCount = 1;
        linkedIndirectly = indirectly;
        linkedViaCasing = viaCasing;

        BlockEntity be = world.getBlockEntity(corePos);
        if (be instanceof GrowthChamberCoreEntity coreEntity) {
            coreEntity.incrementConnectedCasings();
            sendMessage("Glass casing at " + pos + " connected " + messageSource + " at " + corePos +
                    " | Core casing count: " + coreEntity.getConnectedCasings());
        }
    }

    private boolean tryLinkToNeighborCasing(GrowthChamberCasingEntity neighborCasing, BlockPos neighborPos) {
        if (neighborCasing.getCoreCount() == 1) {
            if (neighborCasing.isLinkedIndirectly() && neighborCasing.getLinkedViaCasing().equals(this.pos)) {
                forceNeighborsToCheckCoreOnBroken();
                return false;
            }
            linkToCore(neighborCasing.getSavedCorePos(), "via neighbor casing at " + neighborPos, true, neighborPos);
            forceNeighborsToCheckCore();
            return true;
        }
        return false;
    }

    private boolean tryLinkToNeighborGlass(GrowthChamberGlassEntity neighborGlass, BlockPos neighborPos) {
        if (neighborGlass.getCoreCount() == 1) {
            if (neighborGlass.isLinkedIndirectly() && neighborGlass.getLinkedViaCasing().equals(this.pos)) {
                forceNeighborsToCheckCoreOnBroken();
                return false;
            }
            linkToCore(neighborGlass.getSavedCorePos(), "via neighbor glass at " + neighborPos, true, neighborPos);
            forceNeighborsToCheckCore();
            return true;
        }
        return false;
    }

    public void forceNeighborsToCheckCore() {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.isIn(ModTags.Blocks.CASING)) {
                BlockEntity neighborEntity = world.getBlockEntity(neighborPos);
                if (neighborEntity instanceof GrowthChamberCasingEntity casing && casing.getCoreCount() == 0) {
                    casing.checkForCoreBlocks();
                } else if (neighborEntity instanceof GrowthChamberGlassEntity glass && glass.getCoreCount() == 0) {
                    glass.checkForCoreBlocks();
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
                if (neighborEntity instanceof GrowthChamberCasingEntity casing) {
                    casing.checkForCoreBlocks();
                } else if (neighborEntity instanceof GrowthChamberGlassEntity glass) {
                    glass.checkForCoreBlocks();
                }
            }
        }
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
