package faggot.testmod.block.entity.custom;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface MultiblockMember {
    @Nullable BlockPos getCorePos();
    void setCorePos(@Nullable BlockPos pos);

    boolean isLinkedIndirectly();
    void setLinkedIndirectly(boolean indirect);

    @Nullable BlockPos getLinkedViaCasing();
    void setLinkedViaCasing(@Nullable BlockPos pos);

    int getCoreCount();
    void setCoreCount(int count);


    void reset();

    void unlinkFromCore();

    void checkForCoreBlocks();
    void forceNeighborsToCheckCore();
    void forceNeighborsToCheckCoreOnBroken();

    default boolean isLinkedToCore() {
        return getCorePos() != null;
    }

    default void readMultiblockDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("CoreX") && nbt.contains("CoreY") && nbt.contains("CoreZ")) {
            setCorePos(new BlockPos(nbt.getInt("CoreX"), nbt.getInt("CoreY"), nbt.getInt("CoreZ")));
        } else {
            setCorePos(null);
        }

        setCoreCount(nbt.getInt("CoreCount"));
        setLinkedIndirectly(nbt.getBoolean("LinkedIndirectly"));

        if (nbt.contains("LinkedX")) {
            setLinkedViaCasing(new BlockPos(nbt.getInt("LinkedX"), nbt.getInt("LinkedY"), nbt.getInt("LinkedZ")));
        } else {
            setLinkedViaCasing(null);
        }
    }

    default void writeMultiblockDataToNbt(NbtCompound nbt) {
        if (getCorePos() != null) {
            nbt.putInt("CoreX", getCorePos().getX());
            nbt.putInt("CoreY", getCorePos().getY());
            nbt.putInt("CoreZ", getCorePos().getZ());
        }

        nbt.putInt("CoreCount", getCoreCount());
        nbt.putBoolean("LinkedIndirectly", isLinkedIndirectly());

        if (getLinkedViaCasing() != null) {
            nbt.putInt("LinkedX", getLinkedViaCasing().getX());
            nbt.putInt("LinkedY", getLinkedViaCasing().getY());
            nbt.putInt("LinkedZ", getLinkedViaCasing().getZ());
        }
    }
}
