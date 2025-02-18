package xyz.faewulf.backpack.util.config.ConfigScreen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.inter.IClientPlayerBackpackData;
import xyz.faewulf.backpack.inter.API.SyncUnavailable;
import xyz.faewulf.backpack.inter.API.SyncingTooFrequentlyException;
import xyz.faewulf.backpack.registry.BackpackModelRegistry;
import xyz.faewulf.backpack.util.DataSync;
import xyz.faewulf.backpack.util.config.Config;
import xyz.faewulf.backpack.util.config.ModConfigs;
import xyz.faewulf.backpack.util.config.util.DummyPlayer;
import xyz.faewulf.backpack.util.Misc;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventory;

public class CustomizeScreen extends Screen {
    public static boolean updateRequest = false;

    private static final String translatePath = "backpack.config.";

    //client
    private final Screen parent;
    private final Minecraft client;

    //layout vars
    private static final int RIGHT_TAB_PADDING = 4;
    private static final int QUIT_LAYOUT_EXTRA_HEIGHT = 20;
    private DummyPlayer dummyPlayer;

    //layouts
    private GridLayout settingLayout;
    private GridLayout previewLayout;
    private GridLayout quitSaveLayout;


    @Nullable
    private Button button_Model;
    @Nullable
    private Button button_Variant;
    @Nullable
    private Button button_Status;

    //vars
    private List<String> modelList;
    private List<String> variantList;
    private int model_index = -1;
    private int variant_index = -1;

    private boolean showTools = false;
    private boolean showBanner = false;
    private boolean showContainer = false;
    private boolean showLightSource = false;
    private boolean hidePlayer = false;

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
        updateModelVariantVariable();

        //layout
        //textWidget = this.addRenderableWidget(new FocusableTextWidget(300, this.title, this.font, 12));

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

                                    this.refreshVariantButton();
                                    this.updateDummyStatus();
                                })
                        .width(20)
                        .build(),
                1
        );

        button_Model = rightTabRowHelper.addChild(
                Button.builder(
                                Component.literal(modelList.get(model_index)),
                                button -> {
                                })
                        .width(80)
                        .tooltip(Tooltip.create(Component.translatable("backpack.customize.model.tooltip.label")))
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

                                    this.refreshVariantButton();

                                    this.updateDummyStatus();
                                })
                        .width(20)
                        .build(),
                1
        );

        //change variant buttons
        rightTabRowHelper.addChild(
                Button.builder(
                                Component.literal("<"),
                                button -> {
                                    variant_index--;

                                    if (variant_index < 0)
                                        variant_index = variantList.size() - 1;

                                    if (this.button_Variant != null)
                                        this.button_Variant.setMessage(Component.literal(variantList.get(variant_index)));

                                    this.updateDummyStatus();
                                })
                        .width(20)
                        .build(),
                1
        );

        button_Variant = rightTabRowHelper.addChild(
                Button.builder(
                                Component.literal(variantList.get(variant_index)),
                                button -> {
                                })
                        .width(80)
                        .tooltip(Tooltip.create(Component.translatable("backpack.customize.variant.tooltip.label")))
                        .build(),
                1
        );

        rightTabRowHelper.addChild(
                Button.builder(
                                Component.literal(">"),
                                button -> {
                                    variant_index++;

                                    if (variant_index >= variantList.size())
                                        variant_index = 0;

                                    if (this.button_Variant != null)
                                        this.button_Variant.setMessage(Component.literal(variantList.get(variant_index))); // update message

                                    this.updateDummyStatus();
                                })
                        .width(20)
                        .build(),
                1
        );

        // Preview buttons
        this.previewLayout = new GridLayout();
        GridLayout.RowHelper rowHelper = this.previewLayout.createRowHelper(1);
        rowHelper.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle().padding(1);

        rowHelper.addChild(
                Button.builder(
                                Component.translatable("backpack.customize.preview.tools"),
                                button -> {
                                    showTools = !showTools;

                                    if (this.dummyPlayer == null)
                                        return;

                                    if (showTools) {
                                        Constants.PLAYER_INV_STATUS.computeIfPresent(Constants.DUMMY_PLAYER_NAME, (k, v) -> {
                                            List<ItemStack> toolList = v.getToolsList();
                                            toolList.clear();
                                            toolList.add(new ItemStack(Items.DIAMOND_SWORD));
                                            toolList.add(new ItemStack(Items.GOLDEN_PICKAXE));
                                            toolList.add(new ItemStack(Items.NETHERITE_HOE));
                                            toolList.add(new ItemStack(Items.STONE_AXE));
                                            toolList.add(new ItemStack(Items.BOW));
                                            return v;
                                        });
                                    } else {
                                        Constants.PLAYER_INV_STATUS.computeIfPresent(Constants.DUMMY_PLAYER_NAME, (k, v) -> {
                                            v.getToolsList().clear();
                                            return v;
                                        });
                                    }
                                })
                        .width(80)
                        .tooltip(Tooltip.create(Component.translatable("backpack.customize.preview.tools.tooltip")))
                        .build(),
                1
        );

        rowHelper.addChild(
                Button.builder(
                                Component.translatable("backpack.customize.preview.lamb"),
                                button -> {
                                    showLightSource = !showLightSource;

                                    if (this.dummyPlayer == null)
                                        return;

                                    if (showLightSource) {
                                        Constants.PLAYER_INV_STATUS.computeIfPresent(Constants.DUMMY_PLAYER_NAME, (k, v) -> {
                                            v.setHasLightSource(true);
                                            return v;
                                        });
                                    } else {
                                        Constants.PLAYER_INV_STATUS.computeIfPresent(Constants.DUMMY_PLAYER_NAME, (k, v) -> {
                                            v.setHasLightSource(false);
                                            return v;
                                        });
                                    }
                                })
                        .width(80)
                        .tooltip(Tooltip.create(Component.translatable("backpack.customize.preview.lamb.tooltip")))
                        .build(),
                1
        );

        rowHelper.addChild(
                Button.builder(
                                Component.translatable("backpack.customize.preview.container"),
                                button -> {
                                    showContainer = !showContainer;

                                    if (this.dummyPlayer == null)
                                        return;

                                    if (showContainer) {
                                        Constants.PLAYER_INV_STATUS.computeIfPresent(Constants.DUMMY_PLAYER_NAME, (k, v) -> {
                                            List<ItemStack> containerList = v.getContainerList();
                                            containerList.clear();
                                            containerList.add(new ItemStack(Items.SHULKER_BOX));
                                            containerList.add(new ItemStack(Items.RED_SHULKER_BOX));
                                            containerList.add(new ItemStack(Items.BUNDLE));
                                            return v;
                                        });
                                    } else {
                                        Constants.PLAYER_INV_STATUS.computeIfPresent(Constants.DUMMY_PLAYER_NAME, (k, v) -> {
                                            v.getContainerList().clear();
                                            return v;
                                        });
                                    }
                                })
                        .width(80)
                        .tooltip(Tooltip.create(Component.translatable("backpack.customize.preview.container.tooltip")))
                        .build(),
                1
        );

        rowHelper.addChild(
                Button.builder(
                                Component.translatable("backpack.customize.preview.banner"),
                                button -> {
                                    showBanner = !showBanner;

                                    if (this.dummyPlayer == null)
                                        return;

                                    if (showBanner) {
                                        Constants.PLAYER_INV_STATUS.computeIfPresent(Constants.DUMMY_PLAYER_NAME, (k, v) -> {
                                            v.setBanner(Misc.wardenBanner());
                                            return v;
                                        });
                                    } else {
                                        Constants.PLAYER_INV_STATUS.computeIfPresent(Constants.DUMMY_PLAYER_NAME, (k, v) -> {
                                            v.setBanner(null);
                                            return v;
                                        });
                                    }
                                })
                        .width(80)
                        .tooltip(Tooltip.create(Component.translatable("backpack.customize.preview.banner.tooltip")))
                        .build(),
                1
        );

        rowHelper.addChild(
                Button.builder(
                                Component.translatable("backpack.customize.preview.hideplayer"),
                                button -> {
                                    hidePlayer = !hidePlayer;

                                    if (this.dummyPlayer == null)
                                        return;

                                    this.dummyPlayer.setInvisible(hidePlayer);
                                })
                        .width(80)
                        .tooltip(Tooltip.create(Component.translatable("backpack.customize.preview.hideplayer.tooltip")))
                        .build(),
                1
        );

        // Quit and Save layout buttons

        this.quitSaveLayout = new GridLayout();
        GridLayout.RowHelper rowHelper_QuitSaveLayout = this.quitSaveLayout.createRowHelper(3);
        rowHelper_QuitSaveLayout.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle().padding(1);

        rowHelper_QuitSaveLayout.addChild(
                Button.builder(
                                Component.translatable("backpack.customize.exit"),
                                button -> {
                                    this.onClose();
                                })
                        .width(80)
                        .tooltip(Tooltip.create(Component.translatable("backpack.customize.exit.tooltip")))
                        .build(),
                1
        );

        rowHelper_QuitSaveLayout.addChild(
                Button.builder(
                                Component.translatable("backpack.customize.save"),
                                button -> {
                                    this.saveConfig();

                                    if (Minecraft.getInstance().player != null)
                                        CompletableFuture.runAsync(() -> {
                                            try {
                                                DataSync.sync(Minecraft.getInstance().player.getUUID().toString(), Constants.PLAYER_INV_STATUS.get(";-;"));
                                            } catch (Exception e) {
                                                var actualException = e instanceof CompletionException ce ? ce.getCause() : e;
                                                if (actualException instanceof SyncingTooFrequentlyException) {
                                                    Constants.LOG.warn("Failed to sync settings as we've already synced too recently");
                                                } else {
                                                    Constants.LOG.error("Failed to sync settings", actualException);
                                                }
                                                Constants.LOG.error("Failed to sync settings", actualException);
                                            }
                                        });

                                    this.onClose();
                                })
                        .width(80)
                        .tooltip(Tooltip.create(Component.translatable("backpack.customize.save.tooltip")))
                        .build(),
                1
        );

        button_Status = rowHelper_QuitSaveLayout.addChild(
                Button.builder(
                                Component.literal("..."),
                                button -> {
                                    updateOnlineStatus();
                                })
                        .width(20)
                        .build(),
                1
        );

        //register each widget in right tab (buttons)

        this.settingLayout.visitWidgets(abstractWidget -> {
            abstractWidget.setTabOrderGroup(1);
            this.addRenderableWidget(abstractWidget);
        });
        this.previewLayout.visitWidgets(abstractWidget -> {
            abstractWidget.setTabOrderGroup(1);
            this.addRenderableWidget(abstractWidget);
        });
        this.quitSaveLayout.visitWidgets(abstractWidget -> {
            abstractWidget.setTabOrderGroup(1);
            this.addRenderableWidget(abstractWidget);
        });

        //init the reposition
        this.repositionElements();
        this.updateOnlineStatus();
    }

    private void updateModelVariantVariable() {
        modelList = BackpackModelRegistry.getModelList();

        model_index = modelList.indexOf(ModConfigs.backpack); // retrieve index from registry Based on Modconfigs

        // fallback index value
        if (model_index == -1)
            model_index = 0;

        variantList = BackpackModelRegistry.getVariantList(modelList.get(model_index));
        variant_index = variantList.indexOf(ModConfigs.variant);

        // fallback index value
        if (variant_index == -1)
            variant_index = 0;

        if (this.button_Variant != null)
            this.button_Variant.setMessage(Component.literal(variantList.get(variant_index)));

        if (this.button_Model != null)
            this.button_Model.setMessage(Component.literal(modelList.get(model_index))); // update message

        updateDummyStatus();
    }

    private void updateOnlineStatus() {
        if (button_Status == null)
            return;

        SyncUnavailable result = DataSync.unavailableReason();

        if (result == null) {
            button_Status.setMessage(Component.literal("✔").withStyle(ChatFormatting.GREEN));
            button_Status.setTooltip(Tooltip.create(Component.translatable("backpack.customize.online_status.normal")));
            syncCloudToLocal();
        } else {
            button_Status.setMessage(Component.literal("❌").withStyle(ChatFormatting.RED));
            button_Status.setTooltip(Tooltip.create(Component.translatable("backpack.customize.online_status.error")));
        }
    }

    private void syncCloudToLocal() {
        if (Minecraft.getInstance().player != null) {
            DataSync.UPDATE_QUEUE.put(Minecraft.getInstance().player.getName().getString(), Minecraft.getInstance().player.getStringUUID());
            DataSync.requestUpdateData();
            Misc.sendSystemToast(Component.translatable("backpack.system.upload.syncLocal"), null);
        }
    }

    // Method for update dummy status after its value changed
    private void updateDummyStatus() {

        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel != null) {
            this.dummyPlayer = DummyPlayer.createInstance(clientLevel);
        }

        // Update dummy player
        if (this.dummyPlayer instanceof IClientPlayerBackpackData clientPlayerBackpackDat) {
            clientPlayerBackpackDat.client_Backpack$setModel(modelList.get(model_index));
            clientPlayerBackpackDat.client_Backpack$setVariant(variantList.get(variant_index));
        }
    }

    private void refreshVariantButton() {
        variantList = BackpackModelRegistry.getVariantList(modelList.get(model_index));
        variant_index = 0;

        if (button_Variant != null)
            button_Variant.setMessage(Component.literal(variantList.get(variant_index)));
    }

    private void saveConfig() {
        ModConfigs.backpack = this.modelList.get(model_index);
        ModConfigs.variant = this.variantList.get(variant_index);
        Config.save();

        //update model for player
        if (Minecraft.getInstance().player != null) {
            // Create status for new player
            Constants.PLAYER_INV_STATUS.computeIfPresent(Minecraft.getInstance().player.getName().getString(), (k, v) -> {
                v.updateModelData(ModConfigs.backpack, ModConfigs.variant);
                //backpackStatus.backpackVariant = client_Backpack$variantType;
                return v;
            });
        }
    }

    @Override
    protected void repositionElements() {
        this.settingLayout.arrangeElements();
        this.previewLayout.arrangeElements();
        this.quitSaveLayout.arrangeElements();

//        if (this.textWidget != null) {
//            this.textWidget.containWithin(this.width);
//            this.textWidget.setPosition(this.width - this.textWidget.getWidth() - 20, this.height - this.textWidget.getHeight() - 20);
//        }

        //each vertical layout get 1/3 max width
        int layoutWidth = this.width / 3;

        //reposition for right tab
        //FrameLayout.centerInRectangle(this.settingLayout, this.width - this.settingLayout.getWidth() - RIGHT_TAB_PADDING, this.height - this.settingLayout.getHeight() - RIGHT_TAB_PADDING, this.settingLayout.getWidth() + RIGHT_TAB_PADDING, this.settingLayout.getHeight() + RIGHT_TAB_PADDING);
        FrameLayout.centerInRectangle(this.quitSaveLayout, 0, this.height - this.quitSaveLayout.getHeight() - QUIT_LAYOUT_EXTRA_HEIGHT, this.width, this.quitSaveLayout.getHeight() + QUIT_LAYOUT_EXTRA_HEIGHT);
        FrameLayout.alignInRectangle(this.settingLayout, 0, 0, layoutWidth + RIGHT_TAB_PADDING, this.height - this.quitSaveLayout.getHeight() - QUIT_LAYOUT_EXTRA_HEIGHT, 1f, 0.5f);
        FrameLayout.alignInRectangle(this.previewLayout, this.width - layoutWidth - RIGHT_TAB_PADDING, 0, layoutWidth + RIGHT_TAB_PADDING, this.height - this.quitSaveLayout.getHeight() - QUIT_LAYOUT_EXTRA_HEIGHT, 0f, 0.5f);
    }

    @Override
    public void tick() {

        // Request update outside Class
        if (updateRequest) {
            updateRequest = false;
            updateModelVariantVariable();
        }

        super.tick();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        // renderBackground()
        if (this.minecraft.level != null) {
            guiGraphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            this.renderDirtBackground(guiGraphics);
        }
        // end of renderBackground()

        // Render dummy player if possible

        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel != null) {
            this.dummyPlayer = DummyPlayer.createInstance(clientLevel);
            // Render the entity
            renderEntityInInventoryFollowsMouse(
                    guiGraphics,
                    this.width / 3, 0,
                    this.width * 2 / 3, this.height - this.quitSaveLayout.getHeight() - QUIT_LAYOUT_EXTRA_HEIGHT,
                    100,
                    0,
                    mouseX, mouseY,
                    this.dummyPlayer
            );
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    // Prevent renderEntityInInventoryFollowsMouse draw before background
    // In original code (super.render()) it calls renderBackground() but I have to render renderEntityInInventoryFollowsMouse() before the super.render()
    // to prevent model render on top of buttons and tooltips
    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
        super.renderBackground(guiGraphics);
    }

    @Override
    public void onClose() {
        if (this.client != null)
            this.client.setScreen(this.parent);
        else
            super.onClose();
    }

    // From InventoryScreen.renderEntityInInventoryFollowsMouse
    // Layering guiGraphics.pose() to translate forward a little bit, to resolve clipping with back ground issue.
    // Reverse facing to show back instead of front (quaternionf)
    // Increase yBodyRot (before render) from 20f to 40f for better backpack preview.
    public static void renderEntityInInventoryFollowsMouse(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int scale, float yOffset, float mouseX, float mouseY, LivingEntity entity) {
        float x = (float) (x1 + x2) / 2.0F;
        float y = (float) (y1 + y2) / 2.0F;

        //float f = (float) Math.atan((double) (mouseX / 40.0F));
        //float g = (float) Math.atan((double) (mouseY / 40.0F));
        float f = (float) Math.atan((double) ((x - mouseX) / 40.0F));
        float g = -(float) Math.atan((double) ((y - mouseY) / 40.0F));
        //Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf quaternionf = (new Quaternionf()).rotateXYZ((float) Math.PI, 0.0f, (float) Math.PI * 2);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(g * 20.0F * (float) (Math.PI / 180.0));
        quaternionf.mul(quaternionf2);
        float h = entity.yBodyRot;
        float i = entity.getYRot();
        float j = entity.getXRot();
        float k = entity.yHeadRotO;
        float l = entity.yHeadRot;
        entity.yBodyRot = 180.0F + f * 40.0F;
        entity.setYRot(180.0F + f * 40.0F);
        entity.setXRot(-g * 20.0F);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 50f);
        renderEntityInInventory(guiGraphics, (int) x, (int) y2, scale, quaternionf, quaternionf2, entity);
        guiGraphics.pose().popPose();
        entity.yBodyRot = h;
        entity.setYRot(i);
        entity.setXRot(j);
        entity.yHeadRotO = k;
        entity.yHeadRot = l;
    }
}
