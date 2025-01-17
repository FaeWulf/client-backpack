package xyz.faewulf.backpack.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.feature.backpacks.defaultBackPack.DefaultBackpackModel;
import xyz.faewulf.backpack.inter.BackpackStatus;
import xyz.faewulf.backpack.inter.IBackpackModel;
import xyz.faewulf.backpack.platform.Services;
import xyz.faewulf.backpack.util.compare;

import java.util.ArrayList;
import java.util.List;

public class BackpackLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
    private final EntityModel<EntityRenderState> model;

    public BackpackLayer(RenderLayerParent<PlayerRenderState, PlayerModel> parent, EntityRendererProvider.Context context) {
        super(parent);
        this.model = new DefaultBackpackModel(context.getModelSet().bakeLayer(DefaultBackpackModel.LAYER_LOCATION));
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
            ItemStack banner = null;
            backpackStatus.hasLightSource = false;

            for (int index = 0; index < playerInv.size(); index++) {
                ItemStack stack = playerInv.get(index);

                if (stack.isEmpty())
                    continue;

                // if light source
                if (!backpackStatus.hasLightSource && Services.DYNAMIC_LIGHT_HELPER.getLuminance(stack) > 0) {
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

                // Banner
                if (compare.isHasTag(stack.getItem(), "client_backpack:banner")) {
                    banner = stack;
                }

                // if containers (shulker, bundle for example)
                if (backpackStatus.holdingSlot != index && index != 40 && compare.isHasTag(stack.getItem(), "client_backpack:container")) {
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
            backpackStatus.banner = banner;

            Constants.PLAYER_INV_STATUS.put(playerRenderState.name, backpackStatus);
        }

        //System.out.println(ModelHelper..getQuads(null, Direction.DOWN, RandomSource.create()).size());
        //BlockModel blockModel = ModelHelper.loadBlockModel(ResourceLocation.tryBuild(Constants.MOD_ID, "models/block/backpack.json"));
        //System.out.println(ModelHelper.loadJsonModel(ResourceLocation.tryBuild(Constants.MOD_ID, "models/item/backpack.json")));

        //Render backpack

        if (this.model instanceof IBackpackModel backpackModel) {
            backpackModel.render(poseStack, multiBufferSource, i, playerRenderState, backpackStatus, this.model);
        }


    }
}
