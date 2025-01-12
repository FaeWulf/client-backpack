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
import xyz.faewulf.backpack.inter.BackpackStatus;
import xyz.faewulf.backpack.registry.CustomModelLayers;
import xyz.faewulf.backpack.util.compare;

import java.util.ArrayList;
import java.util.List;

public class BackpackLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
    private final EntityModel<EntityRenderState> model;

    public BackpackLayer(RenderLayerParent<PlayerRenderState, PlayerModel> parent, EntityModelSet modelSet) {
        super(parent);
        this.model = new BackpackModel(modelSet.bakeLayer(CustomModelLayers.BACKPACK));
    }


    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, PlayerRenderState playerRenderState, float v, float v1) {
        VertexConsumer vertexconsumer = multiBufferSource.getBuffer(RenderType.entityTranslucent(ResourceLocation.tryBuild(Constants.MOD_ID, "textures/block/normal.png")));

        // Check for inv change, if change then compute the status again
        BackpackStatus backpackStatus = Constants.PLAYER_INV_STATUS.get(playerRenderState.name);
        List<ItemStack> playerInv = Constants.PLAYER_INV.get(playerRenderState.name);

        if (backpackStatus != null && backpackStatus.invChanged) {
            List<ItemStack> tools = new ArrayList<>();
            List<ItemStack> pockets = new ArrayList<>();
            List<ItemStack> containers = new ArrayList<>();
            List<ItemStack> liquids = new ArrayList<>();
            backpackStatus.hasLightSource = false;

            for (ItemStack stack : playerInv) {
                if (stack.isEmpty())
                    continue;

                // if light source
                if (stack.getItem() == Items.LANTERN) {
                    backpackStatus.hasLightSource = true;
                }

                // if weapon or tool
                if (compare.isHasTag(stack.getItem(), "client_backpack:tool_and_weapon")) {
                    tools.add(stack);
                }

                // if pocket item (arrow for example)
                if (compare.isHasTag(stack.getItem(), "client_backpack:pocket_item")) {
                    pockets.add(stack);
                }

                // if containers (shulker, bundle for example)
                if (compare.isHasTag(stack.getItem(), "client_backpack:container")) {
                    containers.add(stack);
                }

                // if liquid (lava, water for example)
                if (compare.isHasTag(stack.getItem(), "client_backpack:liquid")) {
                    liquids.add(stack);
                }
            }

            backpackStatus.invChanged = false;
            backpackStatus.toolsList = tools;
            backpackStatus.pocketList = pockets;
            backpackStatus.containerList = containers;
            backpackStatus.liquidList = liquids;

            Constants.PLAYER_INV_STATUS.put(playerRenderState.name, backpackStatus);
        }

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
        this.model.setupAnim(playerRenderState);
        this.model.renderToBuffer(poseStack, vertexconsumer, i, OverlayTexture.NO_OVERLAY);

        // holding emit light source
        if (backpackStatus != null && backpackStatus.hasLightSource) {
            poseStack.pushPose();
            //poseStack.translate(0f, 2f, 0f);
            poseStack.rotateAround(Axis.XP.rotationDegrees(180.0F), 0.0f, 0.0f, 0.0f);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.translate(0.05f, -1.45f, -1.3f);

            BlockState lantern = Blocks.LANTERN.defaultBlockState();
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(lantern, poseStack, multiBufferSource, i, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }

        // end global transform
        poseStack.popPose();
    }
}
