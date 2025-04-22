package faggot.testmod.block.entity;

import faggot.testmod.TestMod;
import faggot.testmod.block.ModBlocks;
import faggot.testmod.block.entity.custom.GrowthChamberBlockEntity;
import faggot.testmod.block.entity.custom.PedestalBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<PedestalBlockEntity> PEDESTAL_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(TestMod.MOD_ID, "pedestal_be"),
                    BlockEntityType.Builder.create(PedestalBlockEntity::new, ModBlocks.PEDESTAL).build(null));

    public static final BlockEntityType<GrowthChamberBlockEntity> GROWTH_CHAMBER_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(TestMod.MOD_ID, "growth_chamber_be"),
                    BlockEntityType.Builder.create(GrowthChamberBlockEntity::new, ModBlocks.GROWTH_CHAMBER).build(null));

    public static void registerBlockEntities() {
        TestMod.LOGGER.info("Registering Block Entities for " + TestMod.MOD_ID);
    }
}