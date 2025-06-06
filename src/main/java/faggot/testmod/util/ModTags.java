package faggot.testmod.util;

import faggot.testmod.TestMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;



public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> NEEDS_OSMIUM_TOOL = createTag("needs_osmium_tool");
        public static final TagKey<Block> INCORRECT_FOR_OSMIUM_TOOL = createTag("incorrect_for_osmium_tool");


        public static final TagKey<Block> CASING = createTag("casing");
        public static final TagKey<Block> CORE = createTag("core");
        public static final TagKey<Block> CHAMBER_GLASS = createTag("chamber_glass");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(TestMod.MOD_ID, name));

        }
    }

    public static class Items {
        public static final TagKey<Item> TRANSFORMABLE_ITEMS = createTag("transformable_items");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(TestMod.MOD_ID, name));
        }
    }
}
