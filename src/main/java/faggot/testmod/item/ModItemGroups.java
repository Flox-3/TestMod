package faggot.testmod.item;

import faggot.testmod.TestMod;
import faggot.testmod.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup TEST_MOD_BLOCKS_AND_ITEMS = Registry.register(Registries.ITEM_GROUP,
        Identifier.of(TestMod.MOD_ID, "test_mod_items_and_blocks"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.OSMIUM_INGOT))
                    .displayName(Text.translatable("itemgroup.testmod.test_mod_items_and_blocks"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.RAW_OSMIUM);
                        entries.add(ModItems.OSMIUM_INGOT);
                        entries.add(ModBlocks.OSMIUM_BLOCK);
                        entries.add(ModBlocks.RAW_OSMIUM_BLOCK);
                        entries.add(ModBlocks.OSMIUM_ORE);
                        entries.add(ModBlocks.DEEPSLATE_OSMIUM_ORE);
                        entries.add(ModBlocks.MAGIC_BLOCK);
                        entries.add(ModBlocks.OSMIUM_STAIRS);
                        entries.add(ModBlocks.OSMIUM_SLAB);
                        entries.add(ModBlocks.OSMIUM_FENCE);
                        entries.add(ModBlocks.OSMIUM_FENCE_GATE);
                        entries.add(ModBlocks.OSMIUM_WALL);
                        entries.add(ModBlocks.OSMIUM_DOOR);
                        entries.add(ModBlocks.OSMIUM_TRAPDOOR);
                        entries.add(ModBlocks.OSMIUM_BUTTON);
                        entries.add(ModBlocks.OSMIUM_PRESSURE_PLATE);
                        entries.add(ModBlocks.OSMIUM_LOADING_BAR);

                        entries.add(ModItems.OSMIUM_SWORD);
                        entries.add(ModItems.OSMIUM_SHOVEL);
                        entries.add(ModItems.OSMIUM_PICKAXE);
                        entries.add(ModItems.OSMIUM_AXE);
                        entries.add(ModItems.OSMIUM_HOE);

                        entries.add(ModItems.STAFF);

                        entries.add(ModItems.CHISEL);
                        entries.add(ModItems.CRACK);
                    })

                    .build());


    public static void registerItemGroups() {
        TestMod.LOGGER.info("Registering Item Groups for " + TestMod.MOD_ID);
    }
}
