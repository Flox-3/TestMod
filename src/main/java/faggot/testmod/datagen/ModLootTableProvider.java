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

        addDrop(ModBlocks.OSMIUM_ORE, oreDrops(ModBlocks.OSMIUM_ORE, ModItems.RAW_OSMIUM));
        addDrop(ModBlocks.DEEPSLATE_OSMIUM_ORE, oreDrops(ModBlocks.DEEPSLATE_OSMIUM_ORE, ModItems.RAW_OSMIUM));

    }
}
