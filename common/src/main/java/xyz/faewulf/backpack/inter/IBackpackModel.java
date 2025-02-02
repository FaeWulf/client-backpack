package xyz.faewulf.backpack.inter;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.resources.ResourceLocation;

public interface IBackpackModel {
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, PlayerRenderState playerRenderState, BackpackStatus backpackStatus, EntityModel<EntityRenderState> model, ResourceLocation texture);

    ModelLayerLocation getLayerLocation();
}
