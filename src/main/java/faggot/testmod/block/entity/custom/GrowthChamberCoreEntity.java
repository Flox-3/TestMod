package faggot.testmod.block.entity.custom;

import faggot.testmod.block.entity.ImplementedInventory;
import faggot.testmod.block.entity.ModBlockEntities;
import faggot.testmod.recipe.GrowthChamberRecipe;
import faggot.testmod.recipe.GrowthChamberRecipeInput;
import faggot.testmod.recipe.ModRecipes;
import faggot.testmod.screen.custom.GrowthChamberScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class GrowthChamberCoreEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory, MultiblockMember {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    // MultiblockMember fields
    @Nullable
    private BlockPos corePos = null;
    private int coreCount = 0;
    private boolean linkedIndirectly = false;
    @Nullable
    private BlockPos linkedViaCasing = null;

    // Multiblock structure bounds & state
    private int connectedCasings = 0;
    private int xMin;
    private int yMin;
    private int zMin;
    private int xMax;
    private int yMax;
    private int zMax;
    private boolean isMultiblockValid = false;
    private boolean isInitialized = false;

    // Crafting fields
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    private int progress = 0;
    private int maxProgress = 72;

    protected final PropertyDelegate propertyDelegate;

    public GrowthChamberCoreEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GROWTH_CHAMBER_CORE_BE, pos, state);

        // Initialize multiblock bounds to the core position
        this.xMin = pos.getX();
        this.yMin = pos.getY();
        this.zMin = pos.getZ();
        this.xMax = pos.getX();
        this.yMax = pos.getY();
        this.zMax = pos.getZ();

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> GrowthChamberCoreEntity.this.progress;
                    case 1 -> GrowthChamberCoreEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> GrowthChamberCoreEntity.this.progress = value;
                    case 1 -> GrowthChamberCoreEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    // MultiblockMember interface implementation
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
        // For core block, this might be no-op or could implement scanning if needed
        // You can add your own logic here if needed.
    }

    // Additional multiblock management

    public void incrementConnectedCasings() {
        connectedCasings++;
        markDirty();
        System.out.println("Core at " + pos + " now has " + connectedCasings + " casings.");
    }

    public void decrementConnectedCasings() {
        if (connectedCasings > 0) {
            connectedCasings--;
            markDirty();
            System.out.println("Core at " + pos + " now has " + connectedCasings + " casings.");
        }
    }

    public void updateMultiblockSize(BlockPos casingPos) {
        int cx = casingPos.getX();
        int cy = casingPos.getY();
        int cz = casingPos.getZ();

        if (cx < xMin) xMin = cx;
        if (cx > xMax) xMax = cx;

        if (cy < yMin) yMin = cy;
        if (cy > yMax) yMax = cy;

        if (cz < zMin) zMin = cz;
        if (cz > zMax) zMax = cz;

        markDirty();
        sendMessage("Multiblock bounds updated by casing at " + casingPos +
                ": [" + xMin + "," + xMax + "] [" + yMin + "," + yMax + "] [" + zMin + "," + zMax + "]");
    }

    private void sendMessage(String msg) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                player.sendMessage(Text.literal(msg), false);
            }
        }
    }

    public int getConnectedCasings() {
        return connectedCasings;
    }

    public void forceNeighborsToCheckForCore() {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

            if (neighborEntity instanceof GrowthChamberCasingEntity neighborCasing) {
                neighborCasing.checkForCoreBlocks();
            }
        }
    }

    public void initializeMultiblock() {
        if (isMultiblockValid) {
            isInitialized = true;
        }
    }

    // Inventory & crafting

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.testmod.growth_chamber");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new GrowthChamberScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return this.pos;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("growth_chamber.progress", progress);
        nbt.putInt("growth_chamber.max_progress", maxProgress);

        nbt.putInt("growth_chamber.connected_casings", connectedCasings);

        nbt.putInt("lowest_x_coordination_of_multiblock", xMin);
        nbt.putInt("lowest_y_coordination_of_multiblock", yMin);
        nbt.putInt("lowest_z_coordination_of_multiblock", zMin);
        nbt.putInt("biggest_x_coordination_of_multiblock", xMax);
        nbt.putInt("biggest_y_coordination_of_multiblock", yMax);
        nbt.putInt("biggest_z_coordination_of_multiblock", zMax);

        // Write multiblock member data
        writeMultiblockDataToNbt(nbt);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, inventory, registryLookup);
        progress = nbt.getInt("growth_chamber.progress");
        maxProgress = nbt.getInt("growth_chamber.max_progress");

        connectedCasings = nbt.getInt("growth_chamber.connected_casings");

        xMin = nbt.getInt("lowest_x_coordination_of_multiblock");
        yMin = nbt.getInt("lowest_y_coordination_of_multiblock");
        zMin = nbt.getInt("lowest_z_coordination_of_multiblock");
        xMax = nbt.getInt("biggest_x_coordination_of_multiblock");
        yMax = nbt.getInt("biggest_y_coordination_of_multiblock");
        zMax = nbt.getInt("biggest_z_coordination_of_multiblock");

        // Read multiblock member data
        readMultiblockDataFromNbt(nbt);

        super.readNbt(nbt, registryLookup);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (hasRecipe()) {
            increaseCraftingProgress();
            markDirty(world, pos, state);

            if (hasCraftingFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = 72;
    }

    private void craftItem() {
        Optional<RecipeEntry<GrowthChamberRecipe>> recipe = getCurrentRecipe();

        if (recipe.isPresent()) {
            ItemStack output = recipe.get().value().output();

            this.removeStack(INPUT_SLOT, 1);
            this.setStack(OUTPUT_SLOT, new ItemStack(output.getItem(),
                    this.getStack(OUTPUT_SLOT).getCount() + output.getCount()));
        }
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        this.progress++;
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<GrowthChamberRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            return false;
        }
        ItemStack output = recipe.get().value().output();
        return canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemIntoOutputSlot(output);
    }

    private Optional<RecipeEntry<GrowthChamberRecipe>> getCurrentRecipe() {
        return this.getWorld().getRecipeManager().getFirstMatch(ModRecipes.GROWTH_CHAMBER_TYPE,
                new GrowthChamberRecipeInput(inventory.get(INPUT_SLOT)), this.getWorld());
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return this.getStack(OUTPUT_SLOT).isEmpty() || this.getStack(OUTPUT_SLOT).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = this.getStack(OUTPUT_SLOT).isEmpty() ? 64 : this.getStack(OUTPUT_SLOT).getMaxCount();
        int currentCount = this.getStack(OUTPUT_SLOT).getCount();

        return maxCount >= currentCount + count;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
