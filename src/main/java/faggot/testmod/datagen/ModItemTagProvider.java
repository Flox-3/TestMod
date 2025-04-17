package faggot.testmod.datagen;

import faggot.testmod.item.ModItems;
import faggot.testmod.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ModTags.Items.TRANSFORMABLE_ITEMS)
                .add(ModItems.OSMIUM_INGOT)
                .add(Items.WHEAT);

        getOrCreateTagBuilder(ItemTags.SWORDS)
                .add(ModItems.OSMIUM_SWORD);
        getOrCreateTagBuilder(ItemTags.SHOVELS)
                .add(ModItems.OSMIUM_SHOVEL);
        getOrCreateTagBuilder(ItemTags.PICKAXES)
                .add(ModItems.OSMIUM_PICKAXE);
        getOrCreateTagBuilder(ItemTags.AXES)
                .add(ModItems.OSMIUM_AXE);
        getOrCreateTagBuilder(ItemTags.HOES)
                .add(ModItems.OSMIUM_HOE);
    }
}
