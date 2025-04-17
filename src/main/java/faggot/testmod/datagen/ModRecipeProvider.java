package faggot.testmod.datagen;

import faggot.testmod.TestMod;
import faggot.testmod.block.ModBlocks;
import faggot.testmod.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {
        List<ItemConvertible> OSMIUM_SMELTABLES = List.of(ModItems.RAW_OSMIUM, ModBlocks.OSMIUM_ORE,
                ModBlocks.DEEPSLATE_OSMIUM_ORE);

        offerSmelting(recipeExporter, OSMIUM_SMELTABLES, RecipeCategory.MISC, ModItems.OSMIUM_INGOT, 0.25f, 200, "osmium_ingot");
        offerBlasting(recipeExporter, OSMIUM_SMELTABLES, RecipeCategory.MISC, ModItems.OSMIUM_INGOT, 0.25f, 100, "osmium_ingot");

        offerReversibleCompactingRecipes(recipeExporter, RecipeCategory.BUILDING_BLOCKS, ModItems.OSMIUM_INGOT, RecipeCategory.DECORATIONS, ModBlocks.OSMIUM_BLOCK);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.RAW_OSMIUM_BLOCK)
                .pattern("RRR")
                .pattern("RRR")
                .pattern("RRR")
                .input('R', ModItems.RAW_OSMIUM)
                .criterion(hasItem(ModItems.RAW_OSMIUM), conditionsFromItem(ModItems.RAW_OSMIUM))
                .offerTo(recipeExporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RAW_OSMIUM, 9)
                .input(ModBlocks.RAW_OSMIUM_BLOCK)
                .criterion(hasItem(ModBlocks.RAW_OSMIUM_BLOCK), conditionsFromItem(ModBlocks.RAW_OSMIUM_BLOCK))
                .offerTo(recipeExporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.RAW_OSMIUM, 32)
                .input(ModBlocks.MAGIC_BLOCK)
                .criterion(hasItem(ModBlocks.MAGIC_BLOCK), conditionsFromItem(ModBlocks.MAGIC_BLOCK))
                .offerTo(recipeExporter, Identifier.of(TestMod.MOD_ID, "raw_osmium_from_magic_block"));


        //Tools
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.OSMIUM_SWORD)
                .pattern(" R ")
                .pattern(" R ")
                .pattern(" # ")
                .input('R', ModItems.OSMIUM_INGOT)
                .input('#', Items.STICK)
                .criterion(hasItem(ModItems.OSMIUM_INGOT), conditionsFromItem(ModItems.OSMIUM_INGOT))
                .offerTo(recipeExporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.OSMIUM_SHOVEL)
                .pattern(" R ")
                .pattern(" # ")
                .pattern(" # ")
                .input('R', ModItems.OSMIUM_INGOT)
                .input('#', Items.STICK)
                .criterion(hasItem(ModItems.OSMIUM_INGOT), conditionsFromItem(ModItems.OSMIUM_INGOT))
                .offerTo(recipeExporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.OSMIUM_PICKAXE)
                .pattern("RRR")
                .pattern(" # ")
                .pattern(" # ")
                .input('R', ModItems.OSMIUM_INGOT)
                .input('#', Items.STICK)
                .criterion(hasItem(ModItems.OSMIUM_INGOT), conditionsFromItem(ModItems.OSMIUM_INGOT))
                .offerTo(recipeExporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.OSMIUM_AXE)
                .pattern("RR ")
                .pattern("R# ")
                .pattern(" # ")
                .input('R', ModItems.OSMIUM_INGOT)
                .input('#', Items.STICK)
                .criterion(hasItem(ModItems.OSMIUM_INGOT), conditionsFromItem(ModItems.OSMIUM_INGOT))
                .offerTo(recipeExporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.OSMIUM_HOE)
                .pattern("RR ")
                .pattern(" # ")
                .pattern(" # ")
                .input('R', ModItems.OSMIUM_INGOT)
                .input('#', Items.STICK)
                .criterion(hasItem(ModItems.OSMIUM_INGOT), conditionsFromItem(ModItems.OSMIUM_INGOT))
                .offerTo(recipeExporter);

    }
}
