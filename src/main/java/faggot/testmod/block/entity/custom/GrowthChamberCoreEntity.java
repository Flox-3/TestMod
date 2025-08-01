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

public class GrowthChamberCoreEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);


    //Multiblock hoffentlich
    public int connectedCasings = 0;
    public int xMin = pos.getX();
    public int yMin = pos.getY();
    public int zMin = pos.getZ();
    public int xMax = pos.getX();
    public int yMax = pos.getY();
    public int zMax = pos.getZ();
    public boolean isMultiblockValid = true;
    public boolean isInitialized = false;

    public void incrementConnectedCasings() {
        connectedCasings++;
        markDirty();
        System.out.println("Core at " + pos + " now has " + connectedCasings + " casings.");
    }

    public void setMultiblockValid(boolean valid) {
        this.isMultiblockValid = valid;
        markDirty();
    }


    public void decrementConnectedCasings() {
        if (connectedCasings > 0) {
            connectedCasings--;
            markDirty();
            System.out.println("Core at " + pos + " now has " + connectedCasings + " casings.");
        }
    }

    public void setInitializedfalse() {
        isInitialized = false;
    }

    public void updateMultiblockSize(BlockPos casingPos) {
        int cx = casingPos.getX();
        int cy = casingPos.getY();
        int cz = casingPos.getZ();

        if (cx < xMin) { xMin = cx;}
        if (cx > xMax) { xMax = cx;}

        if (cy < yMin) { yMin = cy;}
        if (cy > yMax) { yMax = cy;}

        if (cz < zMin) { zMin = cz;}
        if (cz > zMax) { zMax = cz;}

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

            if (neighborEntity instanceof MultiblockMember member) {
                member.checkForCoreBlocks();
            }
        }
    }

    public void reset() {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);

            if (neighborEntity instanceof MultiblockMember neighborMember && this.pos.equals(neighborMember.getCorePos())) {
                neighborMember.reset();
            }
        }
    }


    public void initializeMultiblock() {
        if (isMultiblockValid) {

            int wallthickness = 1;

            int width = xMax - xMin + wallthickness;
            int height = yMax - yMin + wallthickness;
            int depth = zMax - zMin + wallthickness;
            int volume = width * height * depth;
            int freevolume = (width - wallthickness * 2) * (height - wallthickness * 2) * (depth - wallthickness * 2);
            int expectedcasings = volume - freevolume - 1;

            sendMessage("Multiblock initialized: Size = " + width + "×" + height + "×" + depth +
                    ", Volume = " + volume + " blocks");
            if (expectedcasings == connectedCasings)
                isInitialized = true;
        } else {
            sendMessage("Tried to initialize multiblock, but it's invalid.");
        }
    }




    //Inventory and shit

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 72;

    public GrowthChamberCoreEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GROWTH_CHAMBER_CORE_BE, pos, state);
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
                    case 0: GrowthChamberCoreEntity.this.progress = value;
                    case 1: GrowthChamberCoreEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return this.pos;
    }

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

        super.readNbt(nbt, registryLookup);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(hasRecipe()) {
            increaseCraftingProgress();
            markDirty(world, pos, state);

            if(hasCraftingFinished()) {
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

        ItemStack output = recipe.get().value().output();

        this.removeStack(INPUT_SLOT, 1);
        this.setStack(OUTPUT_SLOT, new ItemStack(output.getItem(),
                this.getStack(OUTPUT_SLOT).getCount() + output.getCount()));
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