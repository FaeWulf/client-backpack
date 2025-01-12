package xyz.faewulf.backpack.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.kinds.Const;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.registry.CustomModelLayers;

public class BackpackLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
    private final EntityModel<EntityRenderState> model;

    public BackpackLayer(RenderLayerParent<PlayerRenderState, PlayerModel> parent, EntityModelSet modelSet) {
        super(parent);
        this.model = new BackpackModel(modelSet.bakeLayer(CustomModelLayers.BACKPACK));
    }


    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, PlayerRenderState playerRenderState, float v, float v1) {
        VertexConsumer vertexconsumer = multiBufferSource.getBuffer(RenderType.entityTranslucent(ResourceLocation.tryBuild(Constants.MOD_ID, "textures/block/normal.png")));

        poseStack.pushPose();
        if (playerRenderState.isCrouching) {
            poseStack.rotateAround(
                    Axis.XP.rotationDegrees(25.0F), // Rotation of 20 degrees (adjust for effect)
                    0.0f, 0.0f, 0.0f       //Pivot point: adjust to center of backpack
            );
            poseStack.translate(0f, -0.63f, 0.1f);
        } else {
            poseStack.translate(0f, -0.8f, 0.14f);
        }
        this.model.setupAnim(playerRenderState);
        this.model.renderToBuffer(poseStack, vertexconsumer, i, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();

        // holding emit light source
        Inventory playerInv = Constants.PLAYER_INV.get(playerRenderState.name);

        if (playerInv == null)
            return;

        boolean haveTorch = false;
        for (ItemStack stack : playerInv.items) {
            if (!stack.isEmpty() && stack.getItem() == Items.LANTERN) {
                haveTorch = true;
                break;
            }
        }

        if (haveTorch) {
            poseStack.pushPose();
            //poseStack.translate(0f, 2f, 0f);
            poseStack.rotateAround(Axis.XP.rotationDegrees(180.0F), 0.0f, 0.0f, 0.0f);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.translate(0.05f, 0.2f, -1.65f);

            BlockState lantern = Blocks.LANTERN.defaultBlockState();
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(lantern, poseStack, multiBufferSource, i, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }
}
