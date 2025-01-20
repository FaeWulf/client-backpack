package xyz.faewulf.backpack.feature.backpacks.defaultBackPack;

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
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.feature.backpacks.basketBackpack.BasketBackpackModel;
import xyz.faewulf.backpack.inter.BackpackStatus;
import xyz.faewulf.backpack.inter.IBackpackModel;
import xyz.faewulf.backpack.inter.ItemDisplayTransform;
import xyz.faewulf.backpack.platform.Services;
import xyz.faewulf.backpack.util.ModelHelper;
import xyz.faewulf.backpack.util.PoseHelper;
import xyz.faewulf.backpack.util.compare;

import java.util.Map;

public class DefaultBackpackModel extends EntityModel<EntityRenderState> implements IBackpackModel {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.tryBuild(Constants.MOD_ID, "default_backpack"), "main");
    public final ResourceLocation TEXTURE_LOCATION = ResourceLocation.tryBuild(Constants.MOD_ID, "textures/block/default.png");

    private final ItemDisplayTransform toolDisplay = itemPosition.getToolPosition();
    private final ItemDisplayTransform containerDisplay = containerPosition.getPosition();

    public DefaultBackpackModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        //System.out.println(ModelHelper.loadJsonModel(ResourceLocation.tryBuild(Constants.MOD_ID, "models/item/backpack.json")));
//        Map<String, Object> json = ModelHelper.loadJsonModel(ResourceLocation.tryBuild(Constants.MOD_ID, "models/item/backpack.json"));
//        if (json != null)
//            return ModelHelper.createBodyLayer(json);
//        return null;

        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, 0.0F, 8.0F, 10.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(4, 22).addBox(-3.0F, -7.0F, 5.0F, 6.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition log = partdefinition.addOrReplaceChild("log", CubeListBuilder.create().texOffs(4, 18).addBox(-4.0F, -12.0F, 2.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition stick = partdefinition.addOrReplaceChild("stick", CubeListBuilder.create().texOffs(26, 7).addBox(0.0F, -19.0F, -1.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 15).addBox(0.0F, -18.0F, -1.0F, 1.0F, 23.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 24.0F, 4.0F));

        PartDefinition hook = partdefinition.addOrReplaceChild("hook", CubeListBuilder.create().texOffs(4, 29).addBox(-4.0F, -2.0F, 6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 15).addBox(3.0F, -2.0F, 6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(26, 12).addBox(3.0F, -1.0F, 5.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 18).addBox(-4.0F, -1.0F, 5.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(4, 15).addBox(-5.0F, -11.0F, 0.0F, 10.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(20, 22).addBox(4.0F, -7.0F, 0.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(26, 0).addBox(-6.0F, -5.0F, 1.0F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, PlayerRenderState playerRenderState, BackpackStatus backpackStatus, EntityModel<EntityRenderState> model) {
        VertexConsumer vertexconsumer = multiBufferSource.getBuffer(RenderType.entityTranslucent(this.TEXTURE_LOCATION));

        poseStack.pushPose();

        // global transform for crouching
        if (playerRenderState.isCrouching) {
            poseStack.rotateAround(
                    Axis.XP.rotationDegrees(25.0F), // Rotation of 20 degrees (adjust for effect)
                    0.0f, 0.0f, 0.0f       //Pivot point: adjust to center of backpack
            );
            poseStack.translate(0f, -0.63f, 0.1f);
        } else {
            poseStack.translate(0f, -0.8f, 0.16f);
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
            toolDisplay.reset();

            for (int index = 0; index < backpackStatus.toolsList.size(); index++) {

                //max tool can be shown is 5
                if (index == 5)
                    break;

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
                if (toolDisplay.hasNextTransform()) {
                    toolDisplay.getNextTransform().accept(poseStack);
                    Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, null, 0);
                }

                poseStack.popPose();
            }


        }

        //banner
        if (backpackStatus != null && backpackStatus.banner != null) {
            poseStack.pushPose();
            PoseHelper.scale(poseStack, 0.4f);
            PoseHelper.translate(poseStack, -1f, 18, 16);
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            Minecraft.getInstance().getItemRenderer().renderStatic(backpackStatus.banner, ItemDisplayContext.HEAD, packedLight, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, null, 0);
            poseStack.popPose();
        }

        // container render
        if (backpackStatus != null && !backpackStatus.containerList.isEmpty()) {
            containerDisplay.reset();
            for (int index = 0; index < backpackStatus.containerList.size(); index++) {
                ItemStack itemStack = backpackStatus.containerList.get(index);
                poseStack.pushPose();
                if (containerDisplay.hasNextTransform()) {
                    containerDisplay.getNextTransform().accept(poseStack);
                    Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, null, 0);
                }
                poseStack.popPose();
            }
        }

        // end global transform
        poseStack.popPose();
    }

    @Override
    public ModelLayerLocation getLayerLocation() {
        return LAYER_LOCATION;
    }
}
