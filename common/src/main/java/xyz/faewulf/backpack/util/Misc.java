package xyz.faewulf.backpack.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public class Misc {
    public static ItemStack wardenBanner() {
        ItemStack itemStack = new ItemStack(Items.LIGHT_BLUE_BANNER);
        ListTag bannerTag = new BannerPattern.Builder()
                .addPattern(BannerPatterns.CURLY_BORDER, DyeColor.BLACK)
                .addPattern(BannerPatterns.CREEPER, DyeColor.LIGHT_BLUE)
                .addPattern(BannerPatterns.RHOMBUS_MIDDLE, DyeColor.CYAN)
                .addPattern(BannerPatterns.FLOWER, DyeColor.BLACK)
                .addPattern(BannerPatterns.STRIPE_TOP, DyeColor.BLACK)
                .addPattern(BannerPatterns.PIGLIN, DyeColor.BLACK)
                .addPattern(BannerPatterns.STRIPE_BOTTOM, DyeColor.BLACK)
                .toListTag();

        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Patterns", bannerTag);
        BlockItem.setBlockEntityData(itemStack, BlockEntityType.BANNER, compoundTag);
        itemStack.hideTooltipPart(ItemStack.TooltipPart.ADDITIONAL);
        itemStack.setHoverName(Component.literal("Warden Banner").withStyle(ChatFormatting.GOLD));

        return itemStack;
    }

    public static void sendSystemToast(Component title, @Nullable Component message) {
        if (Minecraft.getInstance() != null)
            SystemToast.add(
                    Minecraft.getInstance().getToasts(),
                    SystemToast.SystemToastIds.PERIODIC_NOTIFICATION,
                    title, message
            );

    }
}
