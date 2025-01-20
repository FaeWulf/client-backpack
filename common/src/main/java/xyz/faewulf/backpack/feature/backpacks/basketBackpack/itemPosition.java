package xyz.faewulf.backpack.feature.backpacks.basketBackpack;

import com.mojang.math.Axis;
import xyz.faewulf.backpack.inter.ItemDisplayTransform;

public class itemPosition {
    public static ItemDisplayTransform getToolPosition() {
        ItemDisplayTransform toolDisplay = new ItemDisplayTransform();

        toolDisplay.registerTransform(poseStack -> {
            poseStack.translate(-0.12f, 1.42f, 0.35f);
            poseStack.scale(0.8f, 0.8f, 0.8f);
            poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
        });

        toolDisplay.registerTransform(poseStack -> {
            poseStack.translate(0.25f, 1.57f, 0.07f);
            poseStack.scale(0.3f, 0.3f, 0.3f);
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-45.0F));
        });

        toolDisplay.registerTransform(poseStack -> {
            poseStack.translate(-0.25f, 1.57f, 0.07f);
            poseStack.scale(0.3f, 0.3f, 0.3f);
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-45.0F));
        });

        toolDisplay.registerTransform(poseStack -> {
            poseStack.translate(-0.25f, 1.57f, 0.27f);
            poseStack.scale(0.3f, 0.3f, 0.3f);
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-45.0F));
        });

        return toolDisplay;
    }
}
