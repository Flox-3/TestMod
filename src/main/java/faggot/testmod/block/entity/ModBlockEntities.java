package faggot.testmod.block.entity;

import faggot.testmod.TestMod;
import faggot.testmod.block.ModBlocks;
import faggot.testmod.block.custom.GrowthChamberCasing;
import faggot.testmod.block.custom.GrowthChamberGlass;
import faggot.testmod.block.entity.custom.GrowthChamberCasingEntity;
import faggot.testmod.block.entity.custom.GrowthChamberCoreEntity;
import faggot.testmod.block.entity.custom.GrowthChamberGlassEntity;
import faggot.testmod.block.entity.custom.PedestalBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<PedestalBlockEntity> PEDESTAL_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(TestMod.MOD_ID, "pedestal_be"),
                    BlockEntityType.Builder.create(PedestalBlockEntity::new, ModBlocks.PEDESTAL).build(null));

    public static final BlockEntityType<GrowthChamberCoreEntity> GROWTH_CHAMBER_CORE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(TestMod.MOD_ID, "growth_chamber_core_be"),
                    BlockEntityType.Builder.create(GrowthChamberCoreEntity::new, ModBlocks.GROWTH_CHAMBER_CORE).build(null));

    public static final BlockEntityType<GrowthChamberCasingEntity> GROWTH_CHAMBER_CASING_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(TestMod.MOD_ID, "growth_chamber_casing_be"),
                    BlockEntityType.Builder.create(GrowthChamberCasingEntity::new, ModBlocks.GROWTH_CHAMBER_CASING).build(null));

    public static final BlockEntityType<GrowthChamberGlassEntity> GROWTH_CHAMBER_GLASS_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(TestMod.MOD_ID, "growth_chamber_glass_be"),
                    BlockEntityType.Builder.create(GrowthChamberGlassEntity::new, ModBlocks.GROWTH_CHAMBER_GLASS).build(null));

    public static void registerBlockEntities() {
        TestMod.LOGGER.info("Registering Block Entities for " + TestMod.MOD_ID);
    }
}