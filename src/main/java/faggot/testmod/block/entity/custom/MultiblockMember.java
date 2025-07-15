package faggot.testmod.block.entity.custom;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface MultiblockMember {

    // Core position
    @Nullable BlockPos getCorePos();
    void setCorePos(@Nullable BlockPos pos);

    // Indirect linking
    boolean isLinkedIndirectly();
    void setLinkedIndirectly(boolean indirect);

    // Position of the casing this was linked through
    @Nullable BlockPos getLinkedViaCasing();
    void setLinkedViaCasing(@Nullable BlockPos pos);

    // Core count
    int getCoreCount();
    void setCoreCount(int count);

    // Force check for core (recursive search typically)
    void checkForCoreBlocks();

    // Convenience methods
    default boolean isLinkedToCore() {
        return getCorePos() != null;
    }

    default void linkToCore(BlockPos corePos) {
        setCorePos(corePos);
    }

    default void unlinkFromCore() {
        setCorePos(null);
        setCoreCount(0);
        setLinkedIndirectly(false);
        setLinkedViaCasing(null);
    }

    // NBT serialization
    default void readMultiblockDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("CoreX") && nbt.contains("CoreY") && nbt.contains("CoreZ")) {
            setCorePos(new BlockPos(nbt.getInt("CoreX"), nbt.getInt("CoreY"), nbt.getInt("CoreZ")));
        } else {
            setCorePos(null);
        }

        setCoreCount(nbt.getInt("CoreCount"));
        setLinkedIndirectly(nbt.getBoolean("LinkedIndirectly"));

        if (nbt.contains("LinkedX") && nbt.contains("LinkedY") && nbt.contains("LinkedZ")) {
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
