package xyz.faewulf.backpack.util.config.ConfigScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import xyz.faewulf.backpack.util.config.ConfigLoaderFromAnnotation;
import xyz.faewulf.backpack.util.config.ConfigScreen.Components.DefaultButton;
import xyz.faewulf.backpack.util.config.ConfigScreen.Components.GroupButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScrollableListWidget extends ContainerObjectSelectionList<ScrollableListWidget.ListEntry> {

    private static final int SCROLLBAR_OFFSET = 7;

    public ScrollableListWidget(Minecraft $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        super($$0, $$1, $$2, $$4, $$5);

        //this.setRenderBackground(false);
    }

    public void setSize(int x, int y, int width, int height) {
        this.setX(x);
        this.setY(y);
        this.setWidth(width);
        this.setHeight(height);
    }

    public void clear() {
        this.clearEntries();
    }

    public void addRow(ConfigLoaderFromAnnotation.EntryInfo entryInfo, AbstractWidget... widget) {
        ListEntry e = new ListEntry(entryInfo, widget);
        addEntry(e);
    }

    @Override
    public int getRowWidth() {
        return (int) Math.max(220, width * 0.85);
    }

    public static class ListEntry extends Entry<ListEntry> {

        private final int DEFAULT_BUTTON_SIZE = 20;

        private final ArrayList<NarratableEntry> selectables = new ArrayList<>();
        private final ArrayList<AbstractWidget> elements = new ArrayList<>();
        private final ConfigLoaderFromAnnotation.EntryInfo entryInfo;
        private final DefaultButton defaultButton;

        public ListEntry(ConfigLoaderFromAnnotation.EntryInfo entryInfo, AbstractWidget... e) {
            this.entryInfo = entryInfo;
            elements.addAll(Arrays.asList(e));

            if (entryInfo.pseudoEntry) {
                this.defaultButton = null;
                return;
            }

            this.defaultButton = new DefaultButton(
                    20,
                    20,
                    20,
                    20,
                    Component.literal("↻"),
                    button -> {
                        //reset to default value
                        Object defaultValue = ConfigLoaderFromAnnotation.getDefaultValue(this.entryInfo.name);
                        if (defaultValue != null) {
                            try {
                                this.entryInfo.targetField.set(null, defaultValue);
                            } catch (IllegalAccessException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
            );
            this.defaultButton.active = false;
            elements.add(this.defaultButton);
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return selectables;
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return elements;
        }

        @Override
        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

            // Render all buttons except default button
            for (int i = 0; i < elements.size() - 1; i++) {
                AbstractWidget abstractWidget = elements.get(i);

                int width = (entryWidth - 2 - DEFAULT_BUTTON_SIZE) / (elements.size() - 1);

                abstractWidget.setWidth(width - 2);
                abstractWidget.setX(x + i * width + 2 - SCROLLBAR_OFFSET / 2);
                abstractWidget.setY(y);
                abstractWidget.render(context, mouseX, mouseY, tickDelta);
            }

            // Basically, when elements only contain GroupButton, the for loop above simple skip the loop
            // (Because int the init<>, when entryInfo == null (GroupButton doesn't have entryInfo), it doesn't put default button to the elements list
            // So below is a special render handler for GroupButton
            if (entryInfo.pseudoEntry) {
                if (elements.getFirst() instanceof GroupButton groupButton) {
                    groupButton.setWidth(entryWidth);
                    groupButton.setHeight(20);
                    groupButton.setX(x);
                    groupButton.setY(y);
                    groupButton.render(context, mouseX, mouseY, tickDelta);
                }
                return;
            }

            // Render default button
            this.defaultButton.setWidth(20);
            this.defaultButton.setX(x + entryWidth - DEFAULT_BUTTON_SIZE - 2 - SCROLLBAR_OFFSET / 2);
            this.defaultButton.setY(y);
            this.defaultButton.render(context, mouseX, mouseY, tickDelta);

            //check value
            Object value;

            try {
                value = this.entryInfo.targetField.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            Object defaultValue = ConfigLoaderFromAnnotation.getDefaultValue(this.entryInfo.name);

            this.defaultButton.active = !value.equals(defaultValue);
        }


    }

}