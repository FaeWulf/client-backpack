package xyz.faewulf.backpack.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

public class PoseHelper {

    public static float MODEL_OFFSET = 0.03125f;
    public static float UNIT_SIZE = 0.0625f;
    public static float Y_OFFSET_VALUE = 1f;

    // x, y, z from -8 to 8
    //Use blockbench for this
    public static void translate(PoseStack poseStack, float x, float y, float z) {
        poseStack.translate(UNIT_SIZE * -x, UNIT_SIZE * -y + Y_OFFSET_VALUE, UNIT_SIZE * (z) + MODEL_OFFSET);
    }

    public static void translateNoOffset(PoseStack poseStack, float x, float y, float z) {
        poseStack.translate(UNIT_SIZE * -x, UNIT_SIZE * -y, UNIT_SIZE * (z) + MODEL_OFFSET);
    }

    public static void scale(PoseStack poseStack, float value) {
        poseStack.scale(value, value, value);
    }

    public static void standardizePoseForBackpack(PoseStack poseStack) {
        poseStack.translate(0, Y_OFFSET_VALUE, UNIT_SIZE * 2);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180F));
    }
}
