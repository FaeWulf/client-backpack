package xyz.faewulf.backpack.inter;

import com.mojang.blaze3d.vertex.PoseStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class ItemDisplayTransform {
    private final List<Consumer<PoseStack>> transforms = new ArrayList<>();
    private int index = 0;

    public ItemDisplayTransform() {

    }

    public void registerTransform(Consumer<PoseStack> transform) {
        transforms.add(transform);
    }

    public void reset() {
        index = 0;
    }

    public boolean hasNextTransform() {
        return index < transforms.size();
    }

    public Consumer<PoseStack> getNextTransform() {
        if (index >= transforms.size() || index < 0)
            return poseStack -> {
            };

        return transforms.get(index++);
    }
}