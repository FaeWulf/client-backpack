package xyz.faewulf.backpack.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import commonnetwork.api.Dispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.inter.BackpackModelRecord.DetailBackpack;
import xyz.faewulf.backpack.inter.BackpackStatus;
import xyz.faewulf.backpack.networking.Packet_Handle_BackpackData;
import xyz.faewulf.backpack.platform.Services;
import xyz.faewulf.backpack.registry.BackpackModelRegistry;
import xyz.faewulf.backpack.util.PoseHelper;
import xyz.faewulf.backpack.util.Compare;
import xyz.faewulf.backpack.util.config.ModConfigs;
import xyz.faewulf.backpack.util.Converter;

public class BackpackLayer extends RenderLayer<PlayerRenderState, PlayerModel> {

    public BackpackLayer(RenderLayerParent<PlayerRenderState, PlayerModel> parent) {
        super(parent);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int i, @NotNull PlayerRenderState playerRenderState, float v, float v1) {

        // Toggle mod
        if (!ModConfigs.__enable_mod)
            return;

        // Stop rendering if invisible
        // Exception:
        // DummyPlayer have to bypass this setting,
        // then when toggle HidePlayer when previewing backpack won't affect by this setting.
        if (ModConfigs.hide_if_invisible && playerRenderState.isInvisible && !playerRenderState.name.equals(Constants.DUMMY_PLAYER_NAME)) {
            return;
        }

        boolean isLocalPLayer = Minecraft.getInstance().player != null && Minecraft.getInstance().player.getName().getString().equals(playerRenderState.name);
        BackpackStatus backpackStatus = null;

        // Local player check
        // Outsider players will handle differently
        if (isLocalPLayer || playerRenderState.name.equals(Constants.DUMMY_PLAYER_NAME)) {
            // Local player
            backpackStatus = Constants.PLAYER_INV_STATUS.get(playerRenderState.name);
        } else {
            // Outsider will send request to server
            Dispatcher.sendToServer(new Packet_Handle_BackpackData(playerRenderState.name, new BackpackStatus()));
            backpackStatus = Constants.PLAYER_INV_STATUS.get(playerRenderState.name);
        }

        // Get BackpackStatus
        if (backpackStatus == null)
            return;

        if (!backpackStatus.isWearingBackpack())
            return;

        // Handle backpack model based on model type and variant
        // if no model then don't render
        if (!BackpackModelRegistry.isValidBackpack(backpackStatus.getBackpackType(), backpackStatus.getBackpackVariant()))
            return;

        // Calculate backpack contents based on inv only if inv changed
        // Only for Local player
        if (backpackStatus.isInvChanged() && isLocalPLayer) {
            Converter.updateBackpackStatus(backpackStatus, playerRenderState.name, false);
            Constants.PLAYER_INV_STATUS.put(playerRenderState.name, backpackStatus);
        }

        // Transforms the pose to player's body, match with all transforms (ex: crouching)
        this.getParentModel().body.translateAndRotate(poseStack);

        //Render backpack
        renderBackpack(poseStack, multiBufferSource, i, playerRenderState, backpackStatus, backpackStatus.getBackpackType(), backpackStatus.getBackpackVariant(), v, v1);

    }

    private void renderBackpack(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, PlayerRenderState playerRenderState, BackpackStatus backpackStatus, String id, String variant, float v, float v1) {
        // Get model from model manager (registered via platform's ModelLoadingPlugin or ModelEvent.RegisterAdditional
        BakedModel model = Services.CLIENT_HELPER.getCustomBakedModel(BackpackModelRegistry.getBackpackModel(id, variant));

        DetailBackpack detailBackpack = BackpackModelRegistry.getBackpackDetail(id, variant);

        // Cancel if no model in game model manager
        if (model == Minecraft.getInstance().getModelManager().getMissingModel()) {
            return;
        }

        // render strap
        renderStrap(poseStack, multiBufferSource, packedLight, detailBackpack);

        // Global transform
        poseStack.pushPose();

        if (detailBackpack != null && detailBackpack.global != null) {
            detailBackpack.global.applyTransform(poseStack, true);
        }

        // Backpack transform to correct the location
        poseStack.pushPose();

        if (detailBackpack != null && detailBackpack.base != null) {
            PoseHelper.standardizePoseForBackpack(poseStack);
            detailBackpack.base.applyTransform(poseStack, false);
        }

        // Render backpack
        VertexConsumer vertexconsumer2 = multiBufferSource.getBuffer(Sheets.translucentItemSheet());
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), vertexconsumer2, null, model, 1.0F, 1.0F, 1.0F, packedLight, OverlayTexture.NO_OVERLAY);

        // End backpack transform
        poseStack.popPose();

        // If no detail data, then return
        if (detailBackpack == null)
            return;

        // Processing details

        // tools and weapons
        if (backpackStatus != null && !backpackStatus.getToolsList().isEmpty()) {

            int index_backTool = 0;
            int index_tool = 0;
            int totalSize = 0;
            int totalToolShowed = 0;

            if (detailBackpack.tool != null) totalSize += detailBackpack.tool.size();
            if (detailBackpack.back_tool != null) totalSize += detailBackpack.back_tool.size();


            // Render all
            for (int index = 0; index < backpackStatus.getToolsList().size(); index++) {

                //max tool can be shown
                if (totalToolShowed >= totalSize)
                    break;


                ItemStack itemStack = backpackStatus.getToolsList().get(index);

                //first weapon will hold in the back till out of weapon slot
                if (Compare.isHasTagClient(itemStack.getItem(), "weapon")
                        && (detailBackpack.back_tool != null && index_backTool < detailBackpack.back_tool.size())
                ) {
                    //render
                    poseStack.pushPose();
                    detailBackpack.back_tool.get(index_backTool).applyTransform(poseStack, false);
                    Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, null, 0);

                    // Count up related vars
                    poseStack.popPose();
                    index_backTool++;
                    totalToolShowed++;
                } else if (detailBackpack.tool != null && index_tool < detailBackpack.tool.size()) { // If has valid tool then render
                    // render
                    poseStack.pushPose();
                    detailBackpack.tool.get(index_tool).applyTransform(poseStack, false);
                    Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, null, 0);
                    poseStack.popPose();

                    // Count up related vars
                    index_tool++;
                    totalToolShowed++;
                }

                // if not
                // render nothing
            }
        }


        // holding emit light source
        if (backpackStatus != null && backpackStatus.isHasLightSource()
                && detailBackpack.light_source != null
                && detailBackpack.light_source.transform != null
                && detailBackpack.light_source.item != null
        ) {
            ResourceLocation item = ResourceLocation.tryParse(detailBackpack.light_source.item);

            if (item != null) {

                var blockReference = BuiltInRegistries.BLOCK.get(item);
                // If can get a block model
                if (blockReference.isPresent()) {
                    poseStack.pushPose();
                    detailBackpack.light_source.transform.applyTransform(poseStack, false);
                    BlockState light = blockReference.get().value().defaultBlockState();
                    Minecraft.getInstance().getBlockRenderer().renderSingleBlock(light, poseStack, multiBufferSource, packedLight, OverlayTexture.NO_OVERLAY);
                    poseStack.popPose();
                } else {
                    // If not, then try item
                    BuiltInRegistries.ITEM.get(item).ifPresent(itemReference -> {
                        poseStack.pushPose();

                        detailBackpack.light_source.transform.applyTransform(poseStack, false);

                        ItemStack itemStack = new ItemStack(itemReference.value());
                        Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.HEAD, packedLight, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, null, 0);
                        poseStack.popPose();
                    });
                }
            }
        }

        // container render
        if (backpackStatus != null && !backpackStatus.getContainerList().isEmpty() && detailBackpack.container != null) {
            for (int index = 0; index < backpackStatus.getContainerList().size(); index++) {
                ItemStack itemStack = backpackStatus.getContainerList().get(index);
                poseStack.pushPose();

                if (index < detailBackpack.container.size()) {
                    detailBackpack.container.get(index).applyTransform(poseStack, false);
                    Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, null, 0);
                }
                poseStack.popPose();
            }
        }

        //banner
        if (backpackStatus != null && backpackStatus.getBanner() != null && detailBackpack.banner != null) {
            poseStack.pushPose();
            detailBackpack.banner.applyTransform(poseStack, false);
            Minecraft.getInstance().getItemRenderer().renderStatic(backpackStatus.getBanner(), ItemDisplayContext.HEAD, packedLight, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, null, 0);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private void renderStrap(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, @Nullable DetailBackpack detailBackpack) {

        if (detailBackpack == null || detailBackpack.strap == null || !detailBackpack.strap.visible())
            return;

        // Strap render
        BakedModel strapModel = Services.CLIENT_HELPER.getCustomBakedModel(BackpackModelRegistry.getBackpackModel("strap", "default"));
        DetailBackpack strapDetail = BackpackModelRegistry.getBackpackDetail("strap", detailBackpack.strap.id());

        poseStack.pushPose();

        if (strapDetail != null && strapDetail.base != null && strapDetail.global != null) {
            strapDetail.global.applyTransform(poseStack, true);
            PoseHelper.standardizePoseForBackpack(poseStack);
            strapDetail.base.applyTransform(poseStack, false);
        }

        // Render strap
        VertexConsumer vertexconsumer = multiBufferSource.getBuffer(Sheets.translucentItemSheet());
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), vertexconsumer, null, strapModel, 1.0f, 1.0f, 1.00f, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
        // End strap render
    }
}
