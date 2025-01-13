package xyz.faewulf.backpack.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.feature.backpacks.DefaultBackpackModel;
import xyz.faewulf.backpack.inter.BackpackStatus;
import xyz.faewulf.backpack.inter.IBackpackModel;
import xyz.faewulf.backpack.util.compare;

import java.util.ArrayList;
import java.util.List;

public class BackpackLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
    private final EntityModel<EntityRenderState> model;

    public BackpackLayer(RenderLayerParent<PlayerRenderState, PlayerModel> parent, EntityModelSet modelSet) {
        super(parent);
        this.model = new DefaultBackpackModel(modelSet.bakeLayer(DefaultBackpackModel.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, PlayerRenderState playerRenderState, float v, float v1) {

        // Check for inv change, if change then compute the status again
        BackpackStatus backpackStatus = Constants.PLAYER_INV_STATUS.get(playerRenderState.name);
        List<ItemStack> playerInv = Constants.PLAYER_INV.get(playerRenderState.name);

        if (backpackStatus != null && backpackStatus.invChanged) {
            List<ItemStack> tools = new ArrayList<>();
            List<ItemStack> pockets = new ArrayList<>();
            List<ItemStack> containers = new ArrayList<>();
            List<ItemStack> liquids = new ArrayList<>();
            backpackStatus.hasLightSource = false;

            for (int index = 0; index < playerInv.size(); index++) {
                ItemStack stack = playerInv.get(index);

                if (stack.isEmpty())
                    continue;

                // if light source
                if (stack.getItem() == Items.LANTERN) {
                    backpackStatus.hasLightSource = true;
                }

                // if weapon or tool, and not holding it (main hand and offhand = 40)
                if (backpackStatus.holdingSlot != index && index != 40 && compare.isHasTag(stack.getItem(), "client_backpack:tool_and_weapon")) {
                    tools.add(stack);
                }

                // if pocket item (arrow for example)
                if (compare.isHasTag(stack.getItem(), "client_backpack:pocket_item")) {
                    pockets.add(stack);
                }

                // if containers (shulker, bundle for example)
                if (backpackStatus.holdingSlot != index && index != 40 && compare.isHasTag(stack.getItem(), "client_backpack:container")) {
                    containers.add(stack);
                }

                // if liquid (lava, water for example)
                if (compare.isHasTag(stack.getItem(), "client_backpack:liquid")) {
                    liquids.add(stack);
                }

                // Todo: banner
            }

            backpackStatus.invChanged = false;
            backpackStatus.toolsList = tools;
            backpackStatus.pocketList = pockets;
            backpackStatus.containerList = containers;
            backpackStatus.liquidList = liquids;

            Constants.PLAYER_INV_STATUS.put(playerRenderState.name, backpackStatus);
        }

        // Render backpack
        if (this.model instanceof IBackpackModel backpackModel) {
            backpackModel.render(poseStack, multiBufferSource, i, playerRenderState, backpackStatus, this.model);
        }


    }
}
