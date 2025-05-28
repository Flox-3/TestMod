package faggot.testmod.datagen;

import faggot.testmod.block.ModBlocks;
import faggot.testmod.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.OSMIUM_ORE)
                .add(ModBlocks.DEEPSLATE_OSMIUM_ORE)
                .add(ModBlocks.RAW_OSMIUM_BLOCK)
                .add(ModBlocks.OSMIUM_BLOCK)
                .add(ModBlocks.MAGIC_BLOCK)
                .add(ModBlocks.OSMIUM_STAIRS)
                .add(ModBlocks.OSMIUM_SLAB)
                .add(ModBlocks.OSMIUM_FENCE)
                .add(ModBlocks.OSMIUM_FENCE_GATE)
                .add(ModBlocks.OSMIUM_DOOR)
                .add(ModBlocks.OSMIUM_TRAPDOOR);        ;

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.OSMIUM_ORE)
                .add(ModBlocks.DEEPSLATE_OSMIUM_ORE)
                .add(ModBlocks.RAW_OSMIUM_BLOCK)
                .add(ModBlocks.OSMIUM_BLOCK)
                .add(ModBlocks.MAGIC_BLOCK)
                .add(ModBlocks.OSMIUM_STAIRS)
                .add(ModBlocks.OSMIUM_SLAB)
                .add(ModBlocks.OSMIUM_FENCE)
                .add(ModBlocks.OSMIUM_FENCE_GATE)
                .add(ModBlocks.OSMIUM_DOOR)
                .add(ModBlocks.OSMIUM_TRAPDOOR);

        getOrCreateTagBuilder(ModTags.Blocks.CORE)
                .add(ModBlocks.GROWTH_CHAMBER_CORE);

        getOrCreateTagBuilder(ModTags.Blocks.CASING)
                .add(ModBlocks.GROWTH_CHAMBER_CASING)
                .add(ModBlocks.GROWTH_CHAMBER_GLASS);

        getOrCreateTagBuilder(BlockTags.FENCES).add(ModBlocks.OSMIUM_FENCE);
        getOrCreateTagBuilder(BlockTags.FENCE_GATES).add(ModBlocks.OSMIUM_FENCE_GATE);
        getOrCreateTagBuilder(BlockTags.WALLS).add(ModBlocks.OSMIUM_WALL);

        getOrCreateTagBuilder(ModTags.Blocks.NEEDS_OSMIUM_TOOL)
                .addTag(BlockTags.NEEDS_IRON_TOOL);
        getOrCreateTagBuilder(ModTags.Blocks.INCORRECT_FOR_OSMIUM_TOOL);
    }
}
