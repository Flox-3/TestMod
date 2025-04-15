package faggot.testmod.item;

import faggot.testmod.TestMod;
import faggot.testmod.item.custom.ChiselItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModItems {
    public static final Item RAW_OSMIUM = registerItem("raw_osmium", new Item(new Item.Settings()));
    public static final Item OSMIUM_INGOT = registerItem("osmium_ingot", new Item(new Item.Settings()));

    public static final Item CHISEL = registerItem("chisel", new ChiselItem(new Item.Settings().maxDamage(32)));

    public static final Item CRACK = registerItem("crack", new Item(new Item.Settings().food(ModFoodComponents.CRACK)) {
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("tooltip.testmod.crack.tooltip"));
            super.appendTooltip(stack, context, tooltip, type);
        }
    });



    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(TestMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        TestMod.LOGGER.info("Registering Mod Items for " + TestMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(RAW_OSMIUM);
            fabricItemGroupEntries.add(OSMIUM_INGOT);
        });
    }
}
