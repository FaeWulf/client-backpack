package xyz.faewulf.backpack.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Range;
import xyz.faewulf.backpack.util.config.ModConfigs;

public record PlayerLuminance(boolean invert) implements EntityLuminance {
    // The Codec of this entity luminance provider,
    // this describes how to parse the JSON file.
    public static final MapCodec<PlayerLuminance> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.BOOL.fieldOf("invert").forGetter(PlayerLuminance::invert)
            ).apply(instance, PlayerLuminance::new)
    );

    @Override
    public Type type() {
        // This is the registered type of this entity luminance provider.
        // We will modify the initializer to reflect this.
        return DynamicLightInitializer.CUSTOM_ENTITY_LUMINANCE;
    }

    @Override
    public @Range(from = 0, to = 15) int getLuminance(
            ItemLightSourceManager itemLightSourceManager,
            Entity entity
    ) {
        // Here we compute the luminance the given entity should emit.
        // We also have access to the item light source manager,
        // in case our luminance depends on the luminance of an item.
        //boolean isNight = this.invert ? entity.level().isDay() : entity.level().isNight();

        int maxLight = 0;

        if (!ModConfigs.enable_dynamiclight_compat)
            return maxLight;

        if (entity instanceof AbstractClientPlayer abstractClientPlayer) {
            for (ItemStack item : abstractClientPlayer.getInventory().items) {
                int lightValue = itemLightSourceManager.getLuminance(item);
                if (lightValue > maxLight)
                    maxLight = lightValue;
            }
        }

        //return isNight ? 10 : 0;
        return maxLight;
    }
}
