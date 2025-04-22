package faggot.testmod.datagen;

import faggot.testmod.block.ModBlocks;
import faggot.testmod.block.custom.OsmiumLoadingBar;
import faggot.testmod.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        BlockStateModelGenerator.BlockTexturePool OsmiumPool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.OSMIUM_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_OSMIUM_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_OSMIUM_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.OSMIUM_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MAGIC_BLOCK);

        OsmiumPool.stairs(ModBlocks.OSMIUM_STAIRS);
        OsmiumPool.slab(ModBlocks.OSMIUM_SLAB);
        OsmiumPool.fence(ModBlocks.OSMIUM_FENCE);
        OsmiumPool.fenceGate(ModBlocks.OSMIUM_FENCE_GATE);
        OsmiumPool.wall(ModBlocks.OSMIUM_WALL);
        OsmiumPool.button(ModBlocks.OSMIUM_BUTTON);
        OsmiumPool.pressurePlate(ModBlocks.OSMIUM_PRESSURE_PLATE);

        blockStateModelGenerator.registerDoor(ModBlocks.OSMIUM_DOOR);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.OSMIUM_TRAPDOOR);

        Identifier OsmiumLoadingBar1 = TexturedModel.CUBE_ALL.upload(ModBlocks.OSMIUM_LOADING_BAR, blockStateModelGenerator.modelCollector);
        Identifier OsmiumLoadingBar2 = blockStateModelGenerator.createSubModel(ModBlocks.OSMIUM_LOADING_BAR, "_2", Models.CUBE_ALL, TextureMap::all);
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(ModBlocks.OSMIUM_LOADING_BAR)
                .coordinate(BlockStateModelGenerator.createBooleanModelMap(OsmiumLoadingBar.CLICKED, OsmiumLoadingBar2, OsmiumLoadingBar1)));

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.GROWTH_CHAMBER);

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.RAW_OSMIUM, Models.GENERATED);
        itemModelGenerator.register(ModItems.OSMIUM_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRACK, Models.GENERATED);


        itemModelGenerator.register(ModItems.OSMIUM_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.OSMIUM_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.OSMIUM_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.OSMIUM_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.OSMIUM_HOE, Models.HANDHELD);
    }
}
