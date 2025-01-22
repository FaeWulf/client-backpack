package xyz.faewulf.backpack.util.config.ConfigScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.inter.BackpackStatus;
import xyz.faewulf.backpack.inter.IClientPlayerBackpackData;
import xyz.faewulf.backpack.registry.BackpackModelRegistry;
import xyz.faewulf.backpack.util.config.Config;
import xyz.faewulf.backpack.util.config.ModConfigs;
import xyz.faewulf.backpack.util.config.util.DummyPlayer;
import xyz.faewulf.backpack.util.converter;

import java.util.List;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventory;

public class CustomizeScreen extends Screen {
    private static final String translatePath = "backpack.config.";
    private Boolean isChanged = false;

    //client
    private final Screen parent;
    private final Minecraft client;

    //layout vars
    private static final int RIGHT_TAB_PADDING = 4;
    private DummyPlayer dummyPlayer;

    //layouts
    private GridLayout settingLayout;


    @Nullable
    private Button button_Model;
    @Nullable
    private Button ButtonReset_Cancel;
    @Nullable
    private Button ButtonDone_Save;

    //vars
    private List<String> modelList;
    private String model_type;
    private int model_index = -1;

    protected CustomizeScreen(Screen parent) {
        super(Component.translatable(translatePath + "title"));
        this.parent = parent;
        client = Minecraft.getInstance();
    }

    public static Screen getScreen(Screen parent) {
        return new CustomizeScreen(parent);
    }

    @Override
    protected void init() {

        // init vars
        // get model list from registry
        modelList = BackpackModelRegistry.getModelList();

        model_index = modelList.indexOf(ModConfigs.backpack); // retrieve index from registry Based on Modconfigs

        // fallback index value
        if (model_index == -1)
            model_index = 0;

        //layout

        this.settingLayout = new GridLayout();
        GridLayout.RowHelper rightTabRowHelper = this.settingLayout.createRowHelper(3);
        rightTabRowHelper.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle().padding(1);

        // change model type buttons
        rightTabRowHelper.addChild(
                Button.builder(
                                Component.literal("<"),
                                button -> {
                                    model_index--;

                                    if (model_index < 0)
                                        model_index = modelList.size() - 1;

                                    if (this.button_Model != null)
                                        this.button_Model.setMessage(Component.literal(modelList.get(model_index))); // update message

                                    this.updateDummyStatus();
                                })
                        .width(20)
                        .tooltip(Tooltip.create(Component.translatable("backpack.config.selectType.tooltip")))
                        .build(),
                1
        );

        button_Model = rightTabRowHelper.addChild(
                Button.builder(
                                Component.literal(modelList.get(model_index)),
                                button -> {
                                })
                        .width(80)
                        .tooltip(Tooltip.create(Component.translatable("backpack.config.selectType.tooltip")))
                        .build(),
                1
        );

        rightTabRowHelper.addChild(
                Button.builder(
                                Component.literal(">"),
                                button -> {
                                    model_index++;

                                    if (model_index >= modelList.size())
                                        model_index = 0;

                                    if (this.button_Model != null)
                                        this.button_Model.setMessage(Component.literal(modelList.get(model_index))); // update message

                                    this.updateDummyStatus();
                                })
                        .width(20)
                        .tooltip(Tooltip.create(Component.translatable("backpack.config.selectType.tooltip")))
                        .build(),
                1
        );

        //change variant buttons
        rightTabRowHelper.addChild(
                Button.builder(
                                Component.literal("<"),
                                button -> {
                                })
                        .width(20)
                        .tooltip(Tooltip.create(Component.translatable("backpack.config.selectType.tooltip")))
                        .build(),
                1
        );

        rightTabRowHelper.addChild(
                Button.builder(
                                Component.literal("variants"),
                                button -> {
                                })
                        .width(80)
                        .tooltip(Tooltip.create(Component.translatable("backpack.config.selectType.tooltip")))
                        .build(),
                1
        );

        rightTabRowHelper.addChild(
                Button.builder(
                                Component.literal(">"),
                                button -> {
                                })
                        .width(20)
                        .tooltip(Tooltip.create(Component.translatable("backpack.config.selectType.tooltip")))
                        .build(),
                1
        );

        //save button
        ButtonDone_Save = rightTabRowHelper.addChild(
                Button.builder(
                                Component.translatable("backpack.config.exit"),
                                button -> {
                                    if (isChanged) {
                                        Config.save();
                                        //this.updateConfig();
                                    } else {
                                        //undoConfig();
                                        this.saveConfig();
                                        this.onClose();
                                    }
                                })
                        .width(80)
                        .tooltip(Tooltip.create(Component.translatable("backpack.config.exit.tooltip")))
                        .build(),
                3
        );

        //register each widget in right tab (buttons)
        this.settingLayout.visitWidgets(abstractWidget -> {
            abstractWidget.setTabOrderGroup(1);
            this.addRenderableWidget(abstractWidget);
        });

        //init the reposition
        this.repositionElements();
    }

    // Method for update dummy status after its value changed
    private void updateDummyStatus() {
        // Update dummy player
        if (this.dummyPlayer instanceof IClientPlayerBackpackData clientPlayerBackpackDat) {
            clientPlayerBackpackDat.client_Backpack$setModel(modelList.get(model_index));
        }
    }

    private void saveConfig() {
        ModConfigs.backpack = this.modelList.get(model_index);
        Config.save();

        //update model for player
        if (Minecraft.getInstance().player != null) {
            // Create status for new player
            Constants.PLAYER_INV_STATUS.computeIfPresent(Minecraft.getInstance().player.getName().getString(), (k, v) -> {
                v.backpackType = ModConfigs.backpack;
                //backpackStatus.backpackVariant = client_Backpack$variantType;
                return v;
            });
        }
    }

    @Override
    protected void repositionElements() {
        this.settingLayout.arrangeElements();
        //reposition for right tab
        FrameLayout.centerInRectangle(this.settingLayout, this.width - this.settingLayout.getWidth() - RIGHT_TAB_PADDING, this.height - this.settingLayout.getHeight() - RIGHT_TAB_PADDING, this.settingLayout.getWidth() + RIGHT_TAB_PADDING, this.settingLayout.getHeight() + RIGHT_TAB_PADDING);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int $$1, int $$2, float $$3) {
        guiGraphics.fillGradient(
                0, 0,
                this.width,
                this.height,
                0x99000000, 0x99000000
        );

        super.render(guiGraphics, $$1, $$2, $$3);

        ClientLevel clientLevel = Minecraft.getInstance().level;

        // is selecting styling tab, then tweaking layout for the tab
        if (clientLevel != null) {

            DummyPlayer localPlayer = DummyPlayer.createInstance(clientLevel);
            this.dummyPlayer = localPlayer;

            // Set up the position, scale, and orientation
            float x = (float) this.width / 2; // Center of the screen
            float y = (float) this.height / 2 + 80; // Slightly lower than center
            float scale = 100; // Scale for rendering the entity (adjust as needed)

            // Render the entity
            renderEntityInInventory(
                    guiGraphics, // Graphics context
                    x,
                    y,           // Y position
                    scale,       // Scale
                    new Vector3f(0.0f, 0.0f, 0.0f),   // Translation vector
                    new Quaternionf().rotationYXZ((float) (Math.PI * 15 / 180), 0.0f, (float) Math.PI),        // Entity rotation
                    null, // Optional camera orientation
                    localPlayer       // The entity to render
            );
        }
    }

    @Override
    public void onClose() {
        if (this.client != null)
            this.client.setScreen(this.parent);
        else
            super.onClose();
    }
}
