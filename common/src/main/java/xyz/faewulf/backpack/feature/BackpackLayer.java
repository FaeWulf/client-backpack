package xyz.faewulf.backpack.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import commonnetwork.api.Dispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.inter.BackpackStatus;
import xyz.faewulf.backpack.inter.IBackpackModel;
import xyz.faewulf.backpack.networking.Packet_Handle_BackpackData;
import xyz.faewulf.backpack.platform.Services;
import xyz.faewulf.backpack.registry.BackpackModelRegistry;
import xyz.faewulf.backpack.util.compare;
import xyz.faewulf.backpack.util.config.ModConfigs;
import xyz.faewulf.backpack.util.converter;

import java.util.ArrayList;
import java.util.List;

public class BackpackLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
    private EntityModel<EntityRenderState> model;
    private final EntityRendererProvider.Context context;

    public BackpackLayer(RenderLayerParent<PlayerRenderState, PlayerModel> parent, EntityRendererProvider.Context context) {
        super(parent);
        this.context = context;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int i, @NotNull PlayerRenderState playerRenderState, float v, float v1) {

        // Toggle mod
        if (!ModConfigs._enable_mod)
            return;

        // Stop rendering if invisible
        if (ModConfigs.hide_if_invisible && playerRenderState.isInvisible) {
            return;
        }

        boolean isLocalPLayer = Minecraft.getInstance().player != null && Minecraft.getInstance().player.getName().getString().equals(playerRenderState.name);
        BackpackStatus backpackStatus = null;

        // Local player check
        // Outsider players will handle differently
        if (isLocalPLayer) {
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

        // Handle backpack model based on model type and variant
        if (BackpackModelRegistry.isValidModel(backpackStatus.backpackType))
            this.model = BackpackModelRegistry.createBackpackModel(backpackStatus.backpackType, this.context);

        ResourceLocation variant = null;
        // handle backpack variant
        if (BackpackModelRegistry.isValidVariant(backpackStatus.backpackType, backpackStatus.backpackVariant))
            variant = BackpackModelRegistry.getVariant(backpackStatus.backpackType, backpackStatus.backpackVariant);

        //if no model then don't render
        if (this.model == null)
            return;

        // Calculate backpack contents based on inv only if inv changed
        // Only for Local player
        if (backpackStatus.invChanged && isLocalPLayer) {
            converter.updateBackpackStatus(backpackStatus, playerRenderState.name, false);
            Constants.PLAYER_INV_STATUS.put(playerRenderState.name, backpackStatus);
        }

        //System.out.println(ModelHelper..getQuads(null, Direction.DOWN, RandomSource.create()).size());
        //BlockModel blockModel = ModelHelper.loadBlockModel(ResourceLocation.tryBuild(Constants.MOD_ID, "models/block/backpack.json"));
        //System.out.println(ModelHelper.loadJsonModel(ResourceLocation.tryBuild(Constants.MOD_ID, "models/item/backpack.json")));

        //Render backpack
        if (this.model instanceof IBackpackModel backpackModel) {
            backpackModel.render(poseStack, multiBufferSource, i, playerRenderState, backpackStatus, this.model, variant);
        }


    }
}
