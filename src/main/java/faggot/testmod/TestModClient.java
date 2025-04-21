package faggot.testmod;

import faggot.testmod.block.ModBlocks;
import faggot.testmod.block.entity.ModBlockEntities;
import faggot.testmod.block.entity.renderer.PedestalBlockEntityRenderer;
import faggot.testmod.screen.ModScreenHandlers;
import faggot.testmod.screen.custom.PedestalScreen;
import faggot.testmod.util.ModModelPredicates;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class TestModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OSMIUM_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OSMIUM_TRAPDOOR, RenderLayer.getCutout());

        ModModelPredicates.registerModelPredicates();

        BlockEntityRendererFactories.register(ModBlockEntities.PEDESTAL_BE, PedestalBlockEntityRenderer::new);
        HandledScreens.register(ModScreenHandlers.PEDESTAL_SCREEN_HANDLER, PedestalScreen::new);
    }
}
