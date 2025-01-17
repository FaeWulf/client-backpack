package xyz.faewulf.backpack.util;

import com.mojang.blaze3d.vertex.PoseStack;

public class PoseHelper {

    public static float MODEL_OFFSET = 0.03125f;
    public static float UNIT_SIZE = 0.0625f;
    public static float Y_OFFSET_VALUE = 1f;

    // x, y, z from -8 to 8
    //Use blockbench for this
    public static void translate(PoseStack poseStack, float x, float y, float z) {
        poseStack.translate(UNIT_SIZE * -(x + 1), UNIT_SIZE * y + Y_OFFSET_VALUE, UNIT_SIZE * (z) + MODEL_OFFSET);
    }

    public static void scale(PoseStack poseStack, float value) {
        poseStack.scale(value, value, value);
    }

    public static void normalizePose(PoseStack poseStack) {

    }
}
