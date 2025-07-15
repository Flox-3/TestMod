package faggot.testmod.block.entity.custom;

import faggot.testmod.block.entity.ModBlockEntities;
import faggot.testmod.util.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
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
    private BlockPos linkedViaCasing = null;

    public GrowthChamberGlassEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GROWTH_CHAMBER_GLASS_BE, pos, state);
    }

    // === MultiblockMember backing fields ===

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
        // Unlink if currently connected
        if (corePos != null) {
            unlinkFromCore();
        }

        // === Try to link directly to a core ===
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.isIn(ModTags.Blocks.CORE)) {
                linkToCore(neighborPos, "directly to core", false, null);
                forceNeighborsToCheckCore();
                return;
            }
        }

        // === Try to link indirectly through other casings ===
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

            if (neighborEntity instanceof MultiblockMember neighborMember) {
                if (neighborMember.getCoreCount() == 1) {
                    BlockPos coreCandidate = neighborMember.getCorePos();
                    if (coreCandidate != null) {
                        linkToCore(coreCandidate, "via neighbor at " + neighborPos, true, neighborPos);
                        forceNeighborsToCheckCore();
                        return;
                    }
                }
            }
        }

        // === No core found — reset ===
        reset();
        sendMessage("Casing at " + pos + " is not connected to any core.");
    }

    public void unlinkFromCore() {
        if (corePos != null) {
            BlockEntity coreEntity = world.getBlockEntity(corePos);
            if (coreEntity instanceof GrowthChamberCoreEntity core) {
                core.decrementConnectedCasings();
                core.setInitializedfalse(); // <- Added here
            }
        }

        corePos = null;
        coreCount = 0;
        linkedIndirectly = false;
        linkedViaCasing = null;
    }



    private void linkToCore(BlockPos corePos, String messageSource, boolean indirectly, @Nullable BlockPos viaCasing) {
        setCorePos(corePos);
        setCoreCount(1);
        setLinkedIndirectly(indirectly);
        setLinkedViaCasing(viaCasing);

        BlockEntity be = world.getBlockEntity(corePos);
        if (be instanceof GrowthChamberCoreEntity coreEntity) {
            coreEntity.incrementConnectedCasings();
            sendMessage("Casing at " + pos + " connected " + messageSource + " at " + corePos +
                    " | Core casing count: " + coreEntity.getConnectedCasings());
            updateMultiblockSize(pos);
        }
    }

    public void forceNeighborsToCheckCore() {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

            if (neighborEntity instanceof MultiblockMember neighborMember) {
                if (neighborMember.getCoreCount() == 0) {
                    neighborMember.checkForCoreBlocks();
                }
            }
        }
    }

    public void forceNeighborsToCheckCoreOnBroken() {
        reset();
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);
            if (neighborEntity instanceof MultiblockMember neighborMember) {
                neighborMember.checkForCoreBlocks();
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

    public void reset() {
        unlinkFromCore();
        markDirty();
    }

    public void notifyCoreOfCasingBreak() {
        if (corePos != null) {
            BlockEntity be = world.getBlockEntity(corePos);
            if (be instanceof GrowthChamberCoreEntity coreEntity) {
                coreEntity.setInitializedfalse();
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

    // === NBT ===

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        writeMultiblockDataToNbt(nbt);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        readMultiblockDataFromNbt(nbt);
    }
}
