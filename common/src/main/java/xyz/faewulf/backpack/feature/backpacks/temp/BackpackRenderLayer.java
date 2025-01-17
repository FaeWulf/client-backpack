package xyz.faewulf.backpack.feature.backpacks.temp;

import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import xyz.faewulf.backpack.feature.pseudoItem.CItem;

public class BackpackRenderLayer extends GeoRenderLayer<CItem> {
    public BackpackRenderLayer(GeoRenderer<CItem> entityRendererIn) {
        super(entityRendererIn);
    }
}