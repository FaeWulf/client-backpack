package xyz.faewulf.backpack.feature.backpacks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.inter.BackpackStatus;
import xyz.faewulf.backpack.inter.IBackpackModel;
import xyz.faewulf.backpack.util.compare;

public class DefaultBackpackModel extends EntityModel<EntityRenderState> implements IBackpackModel {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.tryBuild(Constants.MOD_ID, "default_backpack"), "main");
    public final ResourceLocation TEXTURE_LOCATION = ResourceLocation.tryBuild(Constants.MOD_ID, "textures/block/normal.png");

    public DefaultBackpackModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, 0.0F, 8.0F, 10.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(4, 22).addBox(-3.0F, -7.0F, 5.0F, 6.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition log = partdefinition.addOrReplaceChild("log", CubeListBuilder.create().texOffs(4, 18).addBox(-4.0F, -12.0F, 2.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition stick = partdefinition.addOrReplaceChild("stick", CubeListBuilder.create().texOffs(26, 4).addBox(0.0F, -19.0F, -1.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 15).addBox(0.0F, -18.0F, -1.0F, 1.0F, 23.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 24.0F, 4.0F));

        PartDefinition hook = partdefinition.addOrReplaceChild("hook", CubeListBuilder.create().texOffs(16, 29).addBox(-4.0F, -2.0F, 6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 16).addBox(3.0F, -2.0F, 6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 25).addBox(3.0F, -1.0F, 5.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(30, 22).addBox(-4.0F, -1.0F, 5.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition tank = partdefinition.addOrReplaceChild("tank", CubeListBuilder.create().texOffs(24, 18).addBox(-7.0F, -1.0F, 1.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(26, 0).addBox(-7.0F, -8.0F, 1.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(28, 9).addBox(-5.0F, -7.0F, 1.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(4, 29).addBox(-5.0F, -7.0F, 3.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(8, 29).addBox(-7.0F, -7.0F, 1.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(12, 29).addBox(-7.0F, -7.0F, 3.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(4, 15).addBox(-5.0F, -11.0F, 0.0F, 10.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(20, 22).addBox(4.0F, -7.0F, 0.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, PlayerRenderState playerRenderState, BackpackStatus backpackStatus, EntityModel<EntityRenderState> model) {
        VertexConsumer vertexconsumer = multiBufferSource.getBuffer(RenderType.entityTranslucent(this.TEXTURE_LOCATION));

        poseStack.pushPose();

        // global transform
        if (playerRenderState.isCrouching) {
            poseStack.rotateAround(
                    Axis.XP.rotationDegrees(25.0F), // Rotation of 20 degrees (adjust for effect)
                    0.0f, 0.0f, 0.0f       //Pivot point: adjust to center of backpack
            );
            poseStack.translate(0f, -0.63f, 0.1f);
        } else {
            poseStack.translate(0f, -0.8f, 0.14f);
        }

        // Backpack model
        model.setupAnim(playerRenderState);
        model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);

        // holding emit light source
        if (backpackStatus != null && backpackStatus.hasLightSource) {
            poseStack.pushPose();
            //poseStack.translate(0f, 2f, 0f);
            poseStack.rotateAround(Axis.XP.rotationDegrees(180.0F), 0.0f, 0.0f, 0.0f);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.translate(0.05f, -1.45f, -1.3f);

            BlockState lantern = Blocks.LANTERN.defaultBlockState();
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(lantern, poseStack, multiBufferSource, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }

        // tools and weapons
        if (backpackStatus != null && !backpackStatus.toolsList.isEmpty()) {

            boolean alreadyHasWeaponInTheBack = false;

            for (int index = 0; index < backpackStatus.toolsList.size(); index++) {
                ItemStack itemStack = backpackStatus.toolsList.get(index);

                //first weapon will hold in the back
                if (compare.isHasTag(itemStack.getItem(), Constants.MOD_ID + ":weapon") && !alreadyHasWeaponInTheBack) {
                    alreadyHasWeaponInTheBack = true;

                    poseStack.pushPose();

                    poseStack.translate(-0.2f, 0.8f, 0.15f);
                    poseStack.scale(0.8f, 0.8f, 0.8f);
                    poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));

                    Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, null, 0);
                    poseStack.popPose();

                    continue;
                }

                //normal tools go to rack
                poseStack.pushPose();
                //poseStack.translate(0f, 2f, 0f);
                poseStack.translate(index, 0f, 0f);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-45.0F));

                Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, null, 0);
                poseStack.popPose();
            }


        }

        // end global transform
        poseStack.popPose();
    }
}
