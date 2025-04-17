package faggot.testmod.item;

import faggot.testmod.TestMod;
import faggot.testmod.item.custom.ChiselItem;
import faggot.testmod.item.custom.StaffItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
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

    //Osmium Tools
    public static final Item OSMIUM_SWORD = registerItem("osmium_sword",
            new SwordItem(ModToolMaterials.OSMIUM, new Item.Settings()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.OSMIUM, 3, -1f))));
    public static final Item OSMIUM_SHOVEL = registerItem("osmium_shovel",
            new ShovelItem(ModToolMaterials.OSMIUM, new Item.Settings()
                    .attributeModifiers(ShovelItem.createAttributeModifiers(ModToolMaterials.OSMIUM, 1.5f, -2.8F))));
    public static final Item OSMIUM_PICKAXE = registerItem("osmium_pickaxe",
            new PickaxeItem(ModToolMaterials.OSMIUM, new Item.Settings()
                    .attributeModifiers(PickaxeItem.createAttributeModifiers(ModToolMaterials.OSMIUM, 1.0F, -2.6F))));
    public static final Item OSMIUM_AXE = registerItem("osmium_axe",
            new AxeItem(ModToolMaterials.OSMIUM, new Item.Settings()
                    .attributeModifiers(AxeItem.createAttributeModifiers(ModToolMaterials.OSMIUM, 6.2F, -2.8F))));
    public static final Item OSMIUM_HOE = registerItem("osmium_hoe",
            new HoeItem(ModToolMaterials.OSMIUM, new Item.Settings()
                    .attributeModifiers(HoeItem.createAttributeModifiers(ModToolMaterials.OSMIUM, 0.2F, -3.0F))));

    public static final Item STAFF = registerItem("staff",
            new StaffItem(new Item.Settings().maxDamage(500)));



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
