package faggot.testmod.datagen;

import faggot.testmod.block.ModBlocks;
import faggot.testmod.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.OSMIUM_BLOCK);
        addDrop(ModBlocks.RAW_OSMIUM_BLOCK);
        addDrop(ModBlocks.MAGIC_BLOCK);


        addDrop(ModBlocks.OSMIUM_SLAB, slabDrops(ModBlocks.OSMIUM_SLAB));
        addDrop(ModBlocks.OSMIUM_STAIRS);
        addDrop(ModBlocks.OSMIUM_FENCE);
        addDrop(ModBlocks.OSMIUM_FENCE_GATE);
        addDrop(ModBlocks.OSMIUM_WALL);
        addDrop(ModBlocks.OSMIUM_DOOR, doorDrops(ModBlocks.OSMIUM_DOOR));
        addDrop(ModBlocks.OSMIUM_TRAPDOOR);
        addDrop(ModBlocks.OSMIUM_BUTTON);
        addDrop(ModBlocks.OSMIUM_PRESSURE_PLATE);

        addDrop(ModBlocks.OSMIUM_ORE, oreDrops(ModBlocks.OSMIUM_ORE, ModItems.RAW_OSMIUM));
        addDrop(ModBlocks.DEEPSLATE_OSMIUM_ORE, oreDrops(ModBlocks.DEEPSLATE_OSMIUM_ORE, ModItems.RAW_OSMIUM));

    }
}
