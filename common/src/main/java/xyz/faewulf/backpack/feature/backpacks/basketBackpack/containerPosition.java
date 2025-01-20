package xyz.faewulf.backpack.feature.backpacks.basketBackpack;

import com.mojang.math.Axis;
import xyz.faewulf.backpack.inter.ItemDisplayTransform;
import xyz.faewulf.backpack.util.PoseHelper;

public class containerPosition {
    public static ItemDisplayTransform getPosition() {
        ItemDisplayTransform toolDisplay = new ItemDisplayTransform();

        toolDisplay.registerTransform(poseStack -> {
            poseStack.scale(0.3f, 0.3f, 0.3f);
            PoseHelper.translate(poseStack, 16, 50, 8);
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        });

        toolDisplay.registerTransform(poseStack -> {
            poseStack.scale(0.3f, 0.3f, 0.3f);
            PoseHelper.translate(poseStack, 16, 42, 8);
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        });

        toolDisplay.registerTransform(poseStack -> {
            poseStack.scale(0.3f, 0.3f, 0.3f);
            PoseHelper.translate(poseStack, 16, 34, 8);
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        });

        return toolDisplay;
    }
}
