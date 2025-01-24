package xyz.faewulf.backpack.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BannerPatterns;

public class misc {
    public static ItemStack wardenBanner(HolderGetter<BannerPattern> bannerPatternLookup) {
        ItemStack itemStack = new ItemStack(Items.LIGHT_BLUE_BANNER);
        BannerPatternLayers bannerPatternsComponent = new BannerPatternLayers.Builder()
                .addIfRegistered(bannerPatternLookup, BannerPatterns.CURLY_BORDER, DyeColor.BLACK)
                .addIfRegistered(bannerPatternLookup, BannerPatterns.CREEPER, DyeColor.LIGHT_BLUE)
                .addIfRegistered(bannerPatternLookup, BannerPatterns.RHOMBUS_MIDDLE, DyeColor.CYAN)
                .addIfRegistered(bannerPatternLookup, BannerPatterns.FLOWER, DyeColor.BLACK)
                .addIfRegistered(bannerPatternLookup, BannerPatterns.STRIPE_TOP, DyeColor.BLACK)
                .addIfRegistered(bannerPatternLookup, BannerPatterns.PIGLIN, DyeColor.BLACK)
                .addIfRegistered(bannerPatternLookup, BannerPatterns.STRIPE_BOTTOM, DyeColor.BLACK)
                .build();
        itemStack.set(DataComponents.BANNER_PATTERNS, bannerPatternsComponent);
        itemStack.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
        itemStack.set(DataComponents.ITEM_NAME, Component.literal("Warden Banner").withStyle(ChatFormatting.GOLD));
        return itemStack;
    }
}
