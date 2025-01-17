package xyz.faewulf.backpack.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import xyz.faewulf.backpack.Constants;
import net.minecraft.client.renderer.block.model.BlockModel;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ModelHelper {
    public static ModelBakery JessieLetMeCook = null;

//    public static BakedModel getBakedModel() {
//
//        // Get the ModelManager instance from the Minecraft client
//        ModelManager modelManager = Minecraft.getInstance().getModelManager();
//        BlockModel.fromStream().;
//
//
//        // If the model is not a JsonUnbakedModel, return null or handle appropriately
//        return modelManager.getModel(new ModelResourceLocation(ResourceLocation.tryBuild(Constants.MOD_ID, "block/backpack"), "inventory"));
//    }

    /**
     * Load a BlockModel from a ResourceLocation.
     *
     * @param modelLocation The location of the model file (e.g., "mymod:models/block/my_model.json").
     * @return The BlockModel instance.
     */
    public static BlockModel loadBlockModel(ResourceLocation modelLocation) {

        try {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

            // Ensure the resource exists
            Resource resource = resourceManager.getResource(modelLocation).orElseThrow(() ->
                    new IOException("Model file not found: " + modelLocation)
            );

            Reader reader = resource.openAsReader();
            return BlockModel.fromStream(reader);

        } catch (IOException ioException) {
            Constants.LOG.error("An error occurred while reading resource [{}]: {}", modelLocation.toString(), ioException.getMessage());
            return null;
        }
    }

    /**
     * Load a BlockModel from a ResourceLocation.
     *
     * @param modelLocation The location of the model file (e.g., "mymod:models/block/my_model.json").
     * @return The BlockModel instance.
     */
    public static Map<String, Object> loadJsonModel(ResourceLocation modelLocation) {

        try {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

            // Ensure the resource exists
            Resource resource = resourceManager.getResource(modelLocation).orElseThrow(() ->
                    new IOException("Model file not found: " + modelLocation)
            );

            Reader reader = resource.openAsReader();
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType(); // Define the type for Map<String, Object>
            return gson.fromJson(reader, type); // Parse and return the map

        } catch (IOException ioException) {
            Constants.LOG.error("An error occurred while reading resource [{}]: {}", modelLocation.toString(), ioException.getMessage());
            return null;
        }
    }

    /**
     * Converts a JSON block model into a LayerDefinition.
     *
     * @param jsonBlockModel The JSON block model represented as a parsed Map structure.
     * @return A LayerDefinition for the EntityModel.
     */
    public static LayerDefinition createBodyLayer(Map<String, Object> jsonBlockModel) {
        // Initialize a MeshDefinition and its root PartDefinition
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition rootPart = meshDefinition.getRoot();

        // Extract the "elements" tag from the JSON block model
        List<Map<String, Object>> elements = (List<Map<String, Object>>) jsonBlockModel.get("elements");
        List<Double> textureSizeArray = (List<Double>) jsonBlockModel.get("texture_size");

        // Extract the width and height from the array
        int textureWidth = textureSizeArray.get(0).intValue();
        int textureHeight = textureSizeArray.get(1).intValue();

        if (elements != null) {
            for (Map<String, Object> element : elements) {
                // Extract the 'from' and 'to' positions for the cube
                List<Double> from = (List<Double>) element.get("from");
                List<Double> to = (List<Double>) element.get("to");

                if (from != null && to != null) {
                    // Convert 'from' and 'to' into dimensions and offset
                    float xStart = from.get(0).floatValue();
                    float yStart = from.get(1).floatValue();
                    float zStart = from.get(2).floatValue();

                    float xEnd = to.get(0).floatValue();
                    float yEnd = to.get(1).floatValue();
                    float zEnd = to.get(2).floatValue();

                    float width = xEnd - xStart;
                    float height = yEnd - yStart;
                    float depth = zEnd - zStart;

                    // Extract rotation data (if present)
                    Map<String, Object> rotation = (Map<String, Object>) element.get("rotation");
                    PartPose partPose = PartPose.ZERO;

                    if (rotation != null) {
                        List<Double> origin = (List<Double>) rotation.get("origin");
                        String axis = (String) rotation.get("axis");
                        Double angle = (Double) rotation.get("angle");

                        // Create a PartPose if rotation is defined
                        if (origin != null && axis != null && angle != null) {
                            float originX = origin.get(0).floatValue();
                            float originY = origin.get(1).floatValue();
                            float originZ = origin.get(2).floatValue();

                            float angleRadians = (float) Math.toRadians(angle);

                            // Determine the rotation axis
                            switch (axis) {
                                case "x":
                                    partPose = PartPose.offsetAndRotation(originX, originY, originZ, angleRadians, 0.0F, 0.0F);
                                    break;
                                case "y":
                                    partPose = PartPose.offsetAndRotation(originX, originY, originZ, 0.0F, angleRadians, 0.0F);
                                    break;
                                case "z":
                                    partPose = PartPose.offsetAndRotation(originX, originY, originZ, 0.0F, 0.0F, angleRadians);
                                    break;
                            }
                        }
                    }

                    Map<String, Map<String, Object>> faces = (Map<String, Map<String, Object>>) element.get("faces");
                    float smallestX = Float.MAX_VALUE;
                    float smallestY = Float.MAX_VALUE;

                    if (faces != null) {
                        // Iterate through each face (north, south, etc.)
                        for (Map.Entry<String, Map<String, Object>> face : faces.entrySet()) {
                            Map<String, Object> faceData = face.getValue();

                            // Get the "uv" array for this face
                            List<Double> uv = (List<Double>) faceData.get("uv");
                            if (uv != null && uv.size() == 4) {
                                // Extract x1, y1, x2, y2 from the "uv" array
                                float x1 = uv.get(0).floatValue();
                                float y1 = uv.get(1).floatValue();
                                float x2 = uv.get(2).floatValue();
                                float y2 = uv.get(3).floatValue();

                                // Find the smaller x and y values for this face
                                smallestX = Math.min(smallestX, Math.min(x1, x2));
                                smallestY = Math.min(smallestY, Math.min(y1, y2));
                            }
                        }
                    }

                    // Add the cube to the rootPart using CubeListBuilder
                    Random random = new Random();
                    rootPart.addOrReplaceChild(
                            "cube_" + random.nextInt(), // Unique child name
                            CubeListBuilder.create()
                                    .texOffs((int) smallestX, (int) smallestY) // Default texture offset
                                    .addBox(xStart, -(yStart + height), zStart, width, height, depth, new CubeDeformation(0.0F)),
                            partPose // Apply the PartPose (offset and rotation)
                    );

                }
            }
        }

        // Return the constructed LayerDefinition
        return LayerDefinition.create(meshDefinition, textureWidth, textureHeight);
    }
}

