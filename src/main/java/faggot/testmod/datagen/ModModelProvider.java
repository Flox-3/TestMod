package faggot.testmod.datagen;

import faggot.testmod.block.ModBlocks;
import faggot.testmod.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.OSMIUM_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_OSMIUM_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_OSMIUM_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.OSMIUM_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MAGIC_BLOCK);

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.RAW_OSMIUM, Models.GENERATED);
        itemModelGenerator.register(ModItems.OSMIUM_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRACK, Models.GENERATED);
        itemModelGenerator.register(ModItems.CHISEL, Models.GENERATED);
    }
}
