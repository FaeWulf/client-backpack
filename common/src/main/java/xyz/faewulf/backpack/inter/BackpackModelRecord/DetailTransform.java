package xyz.faewulf.backpack.inter.BackpackModelRecord;

import com.google.gson.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import xyz.faewulf.backpack.util.PoseHelper;

import java.lang.reflect.Type;
import java.util.Arrays;

public record DetailTransform(float[] translation, float[] scale, float[] rotation) {

    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, ItemStack itemStack, int packedLight) {
        poseStack.pushPose();

        // Basic transform
        if (translation != null)
            poseStack.translate(translation()[0], translation()[1], translation()[2]);

        if (scale != null)
            poseStack.scale(scale()[0], scale()[1], scale()[2]);

        if (rotation != null) {
            poseStack.mulPose(Axis.XP.rotationDegrees(rotation()[0]));
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation()[1]));
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotation()[2]));
        }

        // Render item
        Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, null, 0);

        poseStack.popPose();
    }

    public void applyTransform(PoseStack poseStack, boolean noOffset) {
        // Make all transform follow same rule

        // Basic transform
        if (translation != null) {
            if (noOffset) {
                PoseHelper.translateNoOffset(poseStack, translation()[0], translation()[1], translation()[2]);
            } else
                PoseHelper.translate(poseStack, translation()[0], translation()[1], translation()[2]);
        }

        if (scale != null)
            poseStack.scale(scale()[0], scale()[1], scale()[2]);

        if (rotation != null) {
            poseStack.mulPose(Axis.XP.rotationDegrees(rotation()[0]));
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation()[1]));
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotation()[2]));
        }
    }

    @Override
    public String toString() {
        return "DetailTransform{" +
                "translation=" + Arrays.toString(translation) +
                ", scale=" + Arrays.toString(scale) +
                ", rotation=" + Arrays.toString(rotation) +
                '}';
    }

    // Custom deserializer to treat empty JSON objects `{}` as null
    public static class Deserializer implements JsonDeserializer<DetailTransform> {
        @Override
        public DetailTransform deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) {
                return null; // If it's not an object, return null
            }

            JsonObject obj = json.getAsJsonObject();

            float[] translation = getFloatArray(obj, "translation");
            float[] scale = getFloatArray(obj, "scale");
            float[] rotation = getFloatArray(obj, "rotation");


            // If any array is invalid, return null
            if (translation == null || scale == null || rotation == null) {
                return null;
            }

            if (translation.length != 3) translation = null;
            if (scale.length != 3) scale = null;
            if (rotation.length != 3) rotation = null;

            return new DetailTransform(translation, scale, rotation);
        }

        private float[] getFloatArray(JsonObject obj, String key) {
            if (!obj.has(key) || !obj.get(key).isJsonArray()) {
                return null;
            }

            JsonArray array = obj.getAsJsonArray(key);
            if (array.size() != 3) {
                return null; // Ensure exactly 3 elements
            }

            float[] values = new float[3];
            for (int i = 0; i < 3; i++) {
                values[i] = array.get(i).getAsFloat();
            }

            return values;
        }
    }
}