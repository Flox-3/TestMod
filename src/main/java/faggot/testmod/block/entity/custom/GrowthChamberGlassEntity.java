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

public class GrowthChamberGlassEntity extends BlockEntity implements MultiblockMember {

    private BlockPos corePos = null;
    private int coreCount = 0;
    private boolean linkedIndirectly = false;
    private boolean MustReset = false;
    private BlockPos linkedViaCasing = null;

    public GrowthChamberGlassEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GROWTH_CHAMBER_GLASS_BE, pos, state);
    }

    @Override
    public @Nullable BlockPos getCorePos() {
        return corePos;
    }

    @Override
    public void setCorePos(@Nullable BlockPos pos) {
        this.corePos = pos;
    }

    @Override
    public boolean isLinkedIndirectly() {
        return linkedIndirectly;
    }

    @Override
    public boolean mustReset() {
        return MustReset; // default: no reset needed
    }

    @Override
    public void setLinkedIndirectly(boolean indirect) {
        this.linkedIndirectly = indirect;
    }

    @Override
    public @Nullable BlockPos getLinkedViaCasing() {
        return linkedViaCasing;
    }

    @Override
    public void setLinkedViaCasing(@Nullable BlockPos pos) {
        this.linkedViaCasing = pos;
    }

    @Override
    public int getCoreCount() {
        return coreCount;
    }

    @Override
    public void setCoreCount(int count) {
        this.coreCount = count;
    }



    @Override
    public void checkForCoreBlocks() {
        if (corePos != null) {
            unlinkFromCore();
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
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

            if (neighborEntity instanceof MultiblockMember neighborMember && neighborMember.getCoreCount() == 1) {
                BlockPos coreCandidate = neighborMember.getCorePos();
                if (coreCandidate != null) {
                    linkToCore(coreCandidate, "via neighbor at " + neighborPos, true, neighborPos);
                    forceNeighborsToCheckCore();
                    return;
                }
            }
        }

        sendMessage("Glass at " + pos + " is not connected to any core.");
    }

    public void unlinkFromCore() {
        if (corePos != null) {
            BlockEntity coreEntity = world.getBlockEntity(corePos);
            if (coreEntity instanceof GrowthChamberCoreEntity core) {
                core.decrementConnectedCasings();
            }
        }

        corePos = null;
        coreCount = 0;
        linkedIndirectly = false;
        linkedViaCasing = null;
        markDirty();
    }

    private void linkToCore(BlockPos corePos, String messageSource, boolean indirectly, @Nullable BlockPos viaCasing) {
        setCorePos(corePos);
        setCoreCount(1);
        setLinkedIndirectly(indirectly);
        setLinkedViaCasing(viaCasing);
        forceNeighborsToCheckCore();
        MustReset = false;

        BlockEntity be = world.getBlockEntity(corePos);
        if (be instanceof GrowthChamberCoreEntity coreEntity) {
            coreEntity.incrementConnectedCasings();
            sendMessage("Casing at " + pos + " connected " + messageSource + " at " + corePos +
                    " | Core casing count: " + coreEntity.getConnectedCasings());

            updateMultiblockSize(pos);
            ValidateMultiblock(
                    new BlockPos(coreEntity.xMin, coreEntity.yMin, coreEntity.zMin),
                    new BlockPos(coreEntity.xMax, coreEntity.yMax, coreEntity.zMax)
            );
        }
    }

    public void forceNeighborsToCheckCore() {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

            if (neighborEntity instanceof MultiblockMember neighborMember && neighborMember.getCoreCount() == 0) {
                neighborMember.checkForCoreBlocks();
            }
        }
    }

    public void forceNeighborsToCheckCoreOnBroken() {
        BlockPos oldCore = this.corePos; // Store before unlinking
        MustReset = true;
        unlinkFromCore(); // Unlink this block

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

            if (neighborEntity instanceof MultiblockMember neighborMember) {
                if (neighborMember.getCorePos() != null && neighborMember.getCorePos().equals(oldCore) && neighborMember.isLinkedIndirectly()) {
                    neighborMember.reset();
                }
            }
        }
    }



    public void updateMultiblockSize(BlockPos casingPos) {
        if (corePos == null) return;

        BlockEntity be = world.getBlockEntity(corePos);
        if (be instanceof GrowthChamberCoreEntity coreEntity) {
            coreEntity.updateMultiblockSize(casingPos);
        }
    }

    /**
     * Reset this block's multiblock data and recursively reset neighbors.
     * Avoid infinite loops by tracking visited positions.
     */


    public void reset() {
        if (shouldSkipReset()) {
            // Optionally keep a debug message:
            // sendMessage("Skipping reset for " + pos + " because it's adjacent to the core at " + corePos);
            markDirty();
            return;
        }

        BlockPos fortnite = corePos;
        unlinkFromCore();


        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockEntity neighborBE = world.getBlockEntity(neighborPos);

            if (neighborBE instanceof MultiblockMember neighborMember && neighborMember.getCorePos() == fortnite) {
                neighborMember.reset();
            }
        }
    }

    private boolean shouldSkipReset() {
        // No world or no core saved → cannot skip
        if (world == null || corePos == null) return false;

        if (!(world.getBlockEntity(corePos) instanceof GrowthChamberCoreEntity core) || core.MustReset) {
            return false;
        } else if (isLinkedIndirectly()) {
            BlockEntity be = world.getBlockEntity(linkedViaCasing);

            if (be instanceof MultiblockMember neighbor && neighbor.mustReset()) {
                MustReset = true;
                return false;
            } else {
                return true;
            }

        }

        return true;
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
        writeMultiblockDataToNbt(nbt);  // Use interface default
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        readMultiblockDataFromNbt(nbt);  // Use interface default
    }

    public void ValidateMultiblock(BlockPos min, BlockPos max) {
        int matchCount = 0;

        if (pos.getX() == min.getX() || pos.getX() == max.getX()) matchCount++;
        if (pos.getY() == min.getY() || pos.getY() == max.getY()) matchCount++;
        if (pos.getZ() == min.getZ() || pos.getZ() == max.getZ()) matchCount++;

        if (matchCount == 1) {
            if (corePos != null) {
                BlockEntity be = world.getBlockEntity(corePos);
                if (be instanceof GrowthChamberCoreEntity core) {
                    core.setMultiblockValid(true);
                }
            }
        } else {
            if (corePos != null) {
                BlockEntity be = world.getBlockEntity(corePos);
                if (be instanceof GrowthChamberCoreEntity core) {
                    core.setMultiblockValid(false);
                }
            }
        }
    }
}
