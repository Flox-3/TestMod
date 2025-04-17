package faggot.testmod.block;


import faggot.testmod.TestMod;
import faggot.testmod.block.custom.MagicBlock;
import faggot.testmod.block.custom.OsmiumLoadingBar;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
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

    public static final Block MAGIC_BLOCK = registerBlock("magic_block",
            new MagicBlock(AbstractBlock.Settings.create().strength(3f).requiresTool()));

    public static final Block OSMIUM_LOADING_BAR = registerBlock("osmium_loading_bar",
            new OsmiumLoadingBar(AbstractBlock.Settings.create().strength(3f).requiresTool()
                    .luminance(state -> state.get(OsmiumLoadingBar.CLICKED)? 15 : 0)));

    //Non-Block Blocks
    public static final Block OSMIUM_STAIRS = registerBlock("osmium_stairs",
            new StairsBlock(ModBlocks.OSMIUM_BLOCK.getDefaultState(),
                    AbstractBlock.Settings.create().strength(3f).requiresTool().sounds(BlockSoundGroup.METAL)));
    public static final Block OSMIUM_SLAB = registerBlock("osmium_slab",
            new SlabBlock(AbstractBlock.Settings.create().strength(3f).requiresTool().sounds(BlockSoundGroup.METAL)));

    public static final Block OSMIUM_BUTTON = registerBlock("osmium_button",
            new ButtonBlock(BlockSetType.IRON,2,
                    AbstractBlock.Settings.create().strength(2f).sounds(BlockSoundGroup.METAL).noCollision()));
    public static final Block OSMIUM_PRESSURE_PLATE = registerBlock("osmium_pressure_plate",
            new PressurePlateBlock(BlockSetType.IRON,
                    AbstractBlock.Settings.create().strength(2f).sounds(BlockSoundGroup.METAL).noCollision()));

    public static final Block OSMIUM_FENCE = registerBlock("osmium_fence",
            new FenceBlock(AbstractBlock.Settings.create().strength(3f).requiresTool().sounds(BlockSoundGroup.METAL)));
    public static final Block OSMIUM_FENCE_GATE = registerBlock("osmium_fence_gate",
            new FenceGateBlock(WoodType.ACACIA,AbstractBlock.Settings.create().strength(3f).requiresTool().sounds(BlockSoundGroup.METAL)));
    public static final Block OSMIUM_WALL = registerBlock("osmium_wall",
            new WallBlock(AbstractBlock.Settings.create().strength(3f).requiresTool().sounds(BlockSoundGroup.METAL)));

    public static final Block OSMIUM_DOOR = registerBlock("osmium_door",
            new DoorBlock(BlockSetType.IRON,
                    AbstractBlock.Settings.create().strength(3f).requiresTool().sounds(BlockSoundGroup.METAL).nonOpaque()));
    public static final Block OSMIUM_TRAPDOOR = registerBlock("osmium_trapdoor",
            new TrapdoorBlock(BlockSetType.IRON,
                    AbstractBlock.Settings.create().strength(3f).requiresTool().sounds(BlockSoundGroup.METAL).nonOpaque()));




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