package xyz.faewulf.backpack.mixinClient;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.util.config.ConfigScreen.CustomizeScreen;
import xyz.faewulf.lib.util.config.infoScreen.ModInfoScreen;

import java.util.Objects;

@Mixin(ModInfoScreen.class)
public abstract class ModInfoScreenMixin extends Screen {
    @Shadow
    @Final
    private Minecraft client;

    @Shadow
    @Final
    private String MOD_ID;

    protected ModInfoScreenMixin(Component title) {
        super(title);
    }

    @Unique
    private Button client_Backpack$customizeButton;

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/LayoutSettings;alignHorizontallyCenter()Lnet/minecraft/client/gui/layouts/LayoutSettings;", ordinal = 0))
    private void initInject(CallbackInfo ci, @Local(ordinal = 0) GridLayout.RowHelper rowHelper) {

        if (!Objects.equals(this.MOD_ID, Constants.MOD_ID))
            return;

        client_Backpack$customizeButton = rowHelper.addChild(
                Button.builder(
                        Component.translatable("client_backpack.infoScreen.customize.available"),
                        button -> this.client.setScreen(CustomizeScreen.getScreen(this))).build()
        );
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickInject(CallbackInfo ci) {

        if (!Objects.equals(this.MOD_ID, Constants.MOD_ID))
            return;

        // Toggle customize button based on client available or not
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel == null) {
            client_Backpack$customizeButton.active = false;
            client_Backpack$customizeButton.setTooltip(Tooltip.create(Component.translatable("client_backpack.infoScreen.customize.unavailable.tooltip")));
            client_Backpack$customizeButton.setMessage(Component.translatable("client_backpack.infoScreen.customize.unavailable"));
        } else {
            client_Backpack$customizeButton.active = true;
            client_Backpack$customizeButton.setMessage(Component.translatable("client_backpack.infoScreen.customize.available"));
            client_Backpack$customizeButton.setTooltip(null);
        }
    }
}
