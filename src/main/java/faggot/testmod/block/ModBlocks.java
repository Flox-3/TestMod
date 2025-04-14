package faggot.testmod.block;


import faggot.testmod.TestMod;
import faggot.testmod.block.custom.MagicBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class ModBlocks {

    //ORES
    public static final Block OSMIUM_ORE = registerBlock("osmium_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(2, 5),
                    AbstractBlock.Settings.create().strength(3f).requiresTool()));
    public static final Block DEEPSLATE_OSMIUM_ORE = registerBlock("deepslate_osmium_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(3, 6),
                    AbstractBlock.Settings.create().strength(3f).requiresTool().sounds(BlockSoundGroup.DEEPSLATE)));


    //BLOCKS
    public static final Block RAW_OSMIUM_BLOCK = registerBlock("raw_osmium_block",
            new net.minecraft.block.Block(AbstractBlock.Settings.create( ).strength(3f)
                    .requiresTool().sounds(BlockSoundGroup.METAL)));
    public static final Block OSMIUM_BLOCK = registerBlock("osmium_block",
            new net.minecraft.block.Block(AbstractBlock.Settings.create( ).strength(3f)
                    .requiresTool().sounds(BlockSoundGroup.METAL)));

    public static final Block MAGIC_BLOCK = registerBlock("magic_blcok",
            new MagicBlock(AbstractBlock.Settings.create().strength(3f).requiresTool()));



    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(TestMod.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(TestMod.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
       TestMod.LOGGER.info("Registering Mod Blocks for " + TestMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(fabricItemGroupEntries ->
                fabricItemGroupEntries.add(ModBlocks.OSMIUM_ORE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(fabricItemGroupEntries ->
                fabricItemGroupEntries.add(ModBlocks.DEEPSLATE_OSMIUM_ORE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(fabricItemGroupEntries ->
                fabricItemGroupEntries.add(ModBlocks.RAW_OSMIUM_BLOCK));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(fabricItemGroupEntries ->
                fabricItemGroupEntries.add(ModBlocks.OSMIUM_BLOCK));
   }
}