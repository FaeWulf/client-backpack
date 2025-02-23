package xyz.faewulf.backpack.mixin.compat.travelerbackpack;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

@Pseudo
@Mixin(targets = "com.tiviacz.travelersbackpack.component.ComponentUtils")
public interface ComponentUtilsInvoker {
    @Invoker("isWearingBackpack")
    public static boolean isWearingBackpackInvoked(Player player) {
        throw new AssertionError();
    }

    @Invoker("getWearingBackpack")
    public static ItemStack getWearingBackpackInvoked(Player player) {
        throw new AssertionError();
    }

    @Invoker("getBackpackWrapper")
    public static @Nullable BackpackWrapper getBackpackWrapper(Player player) {
        throw new AssertionError();
    }

    @Invoker("getBackpackWrapper")
    public static @Nullable BackpackWrapper getBackpackWrapper(Player player, ItemStack itemStack) {
        throw new AssertionError();
    }
}