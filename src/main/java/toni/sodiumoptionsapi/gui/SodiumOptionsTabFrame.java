package toni.sodiumoptionsapi.gui;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import net.caffeinemc.mods.sodium.api.util.ColorARGB;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;

#if FORGE
import org.embeddedt.embeddium.gui.EmbeddiumVideoOptionsScreen;
import org.embeddedt.embeddium.gui.frame.AbstractFrame;
import org.embeddedt.embeddium.gui.frame.BasicFrame;
import org.embeddedt.embeddium.gui.frame.components.SearchTextFieldComponent;
import org.embeddedt.embeddium.gui.frame.tab.Tab;
import org.embeddedt.embeddium.render.ShaderModBridge;
import org.embeddedt.embeddium.gui.frame.tab.Tab;
#else
import me.flashyreese.mods.reeses_sodium_options.client.gui.FlatButtonWidgetExtended;
import me.flashyreese.mods.reeses_sodium_options.client.gui.Point2i;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.tab.Tab;
import me.flashyreese.mods.reeses_sodium_options.compat.IrisCompat;
import me.flashyreese.mods.reeses_sodium_options.client.gui.SodiumVideoOptionsScreen;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.BasicFrame;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.components.SearchTextFieldComponent;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.tab.Tab;
import me.flashyreese.mods.reeses_sodium_options.compat.IrisCompat;
#endif

#if AFTER_21_1

import net.caffeinemc.mods.sodium.client.gui.widgets.AbstractWidget;
import net.caffeinemc.mods.sodium.client.gui.widgets.FlatButtonWidget;
import net.caffeinemc.mods.sodium.client.util.Dim2i;
#else
import me.jellysquid.mods.sodium.client.gui.widgets.AbstractWidget;
import me.jellysquid.mods.sodium.client.gui.widgets.FlatButtonWidget;
import me.jellysquid.mods.sodium.client.util.Dim2i;
#endif

public class SodiumOptionsTabFrame extends AbstractFrame {
    private static final int TAB_OPTION_INDENT = 5;

    private Dim2i tabSection;
    private final Dim2i frameSection;
    private final Multimap<String, Tab<?>> tabs;
    private final Runnable onSetTab;
    private final AtomicReference<Component> tabSectionSelectedTab;
    private final AtomicReference<Integer> tabSectionScrollBarOffset;
    private Tab<?> selectedTab;
    private String selectedHeader;
    private #if FORGE AbstractFrame #else me.flashyreese.mods.reeses_sodium_options.client.gui.frame.AbstractFrame #endif selectedFrame;
    private Dim2i tabSectionInner;
    private ScrollableFrame sidebarFrame;

    private FlatButtonWidget.Style style = getStyle();

    public SodiumOptionsTabFrame(Dim2i dim, boolean renderOutline, Multimap<String, Tab<?>> tabs, Runnable onSetTab, AtomicReference<Component> tabSectionSelectedTab, AtomicReference<Integer> tabSectionScrollBarOffset) {
        super(dim, renderOutline);
        this.tabs = ImmutableListMultimap.copyOf(tabs);

        Optional<Integer> result = Stream.concat(
                tabs.keys().stream().map(id -> this.getStringWidth(TabHeaderWidget.getLabel(id, true)) + 10),
                tabs.values().stream().map(tab -> this.getStringWidth(tab.title()) + TAB_OPTION_INDENT)
        ).max(Integer::compareTo);

        this.tabSection = new Dim2i(this.dim.x(), this.dim.y(), result.map(integer -> integer + (24)).orElseGet(() -> (int) (this.dim.width() * 0.35D)), this.dim.height());
        this.frameSection = new Dim2i(this.tabSection.getLimitX(), this.dim.y(), this.dim.width() - this.tabSection.width(), this.dim.height());

        this.onSetTab = onSetTab;
        this.tabSectionSelectedTab = tabSectionSelectedTab;
        this.tabSectionScrollBarOffset = tabSectionScrollBarOffset;

        if (this.tabSectionSelectedTab.get() != null) {
            this.selectedTab = this.tabs.values().stream().filter(tab -> tab.title().getString().equals(this.tabSectionSelectedTab.get().getString())).findAny().orElse(null);
        }

        this.buildFrame();

        // Let's build each frame, future note for anyone: do not move this line.
        this.tabs.values().stream().filter(tab -> this.selectedTab != tab).forEach(tab -> tab.getFrameFunction().apply(this.frameSection));
    }

    public static Builder createBuilder() {
        return new Builder();
    }

    public void setHeader(String header, Tab<?> tab) {
        this.selectedHeader = header;
        //tabSectionScrollBarOffset.set(0);
        this.setTab(tab);
    }

    public void setTab(Tab<?> tab) {

        if (selectedHeader != null) {
            var map = tabs.asMap();
            var value = map.getOrDefault(selectedHeader, null);
            if (value == null || !value.contains(tab)) {
                selectedHeader = null;
            }
        }

        this.selectedTab = tab;
        this.tabSectionSelectedTab.set(this.selectedTab.title());
        if (this.onSetTab != null) {
            this.onSetTab.run();
        }
        this.buildFrame();
    }

    class TabSidebarFrame extends toni.sodiumoptionsapi.gui.AbstractFrame {
        TabSidebarFrame(Dim2i dim) {
            super(dim, false);
        }

        @Override
        public void buildFrame() {
            this.children.clear();
            this.drawable.clear();
            this.controlElements.clear();

            rebuildTabs();

            super.buildFrame();
        }

        private void rebuildTabs() {
            int offsetY = 0;
            int width = tabSection.width() - 4;
            int height = 18;

            for (var modEntry : tabs.asMap().entrySet()) {
                // Add a "button" as the header
                var extraHeight = modEntry.getKey().equals("sodium") || modEntry.getKey().equals("embeddium") ? 0 : 4;
                Dim2i modHeaderDim = withParentOffset(new Dim2i(0, offsetY + extraHeight, width, height), #if !FORGE (Point2i) (Object) #endif tabSection);
                offsetY += height + extraHeight;

                var firstTab = modEntry.getValue().stream().findFirst().orElse(null);
                TabHeaderWidget headerButton = new TabHeaderWidget(modHeaderDim, modEntry.getKey(), () -> {
                    if (modEntry.getKey().equals("iris") || modEntry.getKey().equals("oculus")) {
                        #if FORGE
                        if(ShaderModBridge.openShaderScreen(Minecraft.getInstance().screen) instanceof Screen screen) {
                            Minecraft.getInstance().setScreen(screen);
                        }
                        #else
                        Object patt0$temp = IrisCompat.getIrisShaderPacksScreen(Minecraft.getInstance().screen);
                        if (patt0$temp instanceof Screen screen) {
                            Minecraft.getInstance().setScreen(screen);
                        }
                        #endif
                        return;
                    }
                    SodiumOptionsTabFrame.this.setHeader(modEntry.getKey(), firstTab);
                });


                (#if !FORGE (FlatButtonWidgetExtended) #endif headerButton).setLeftAligned(true);

                this.children.add(headerButton);

                if (!(modEntry.getKey().equals("sodium") || modEntry.getKey().equals("embeddium")) && (selectedHeader == null || !selectedHeader.equals(modEntry.getKey())))
                    continue;

                if (modEntry.getValue().size() == 1) {
                    headerButton.setSelected(SodiumOptionsTabFrame.this.selectedTab == firstTab);
                    continue;
                }

                for (Tab<?> tab : modEntry.getValue()) {

                    if ((modEntry.getKey().equals("sodium") || modEntry.getKey().equals("embeddium")) && Objects.equals(tab.title().getString(), "Shader Packs..."))
                        continue;

                    // Add the button for the mod itself
                    Dim2i tabDim = withParentOffset(new Dim2i(0, offsetY, width, height), #if !FORGE (Point2i) (Object) #endif tabSection);

                    FlatButtonWidget button = new FlatButtonWidget(tabDim, tab.title(), () -> {
                        SodiumOptionsTabFrame.this.setTab(tab);
                    });

                    button.setStyle(style);
                    button.setSelected(SodiumOptionsTabFrame.this.selectedTab == tab);
                    (#if !FORGE (FlatButtonWidgetExtended) #endif button).setLeftAligned(true);
                    this.children.add(button);

                    offsetY += height;
                }
            }
        }
    }

    public static FlatButtonWidget.Style getStyle() {
        FlatButtonWidget.Style style = new FlatButtonWidget.Style();
        style.bgHovered = -536870912;
        style.bgDefault = ColorARGB.pack(0, 0, 0, 85);
        style.bgDisabled = 1610612736;
        style.textDefault = -1;
        style.textDisabled = -1862270977;
        return style;
    }

    #if FORGE
    public Dim2i withParentOffset(Dim2i ths, Dim2i parent) {
        return new Dim2i(parent.x() + ths.x(), parent.y() + ths.y(), ths.width(), ths.height());
    }
    #else
    public Dim2i withParentOffset(Dim2i ths, Point2i parent) {
        return new Dim2i(parent.getX() + ths.x(), parent.getY() + ths.y(), ths.width(), ths.height());
    }
    #endif


    @Override
    public void buildFrame() {
        this.children.clear();
        this.drawable.clear();
        this.controlElements.clear();

        if (this.selectedTab == null) {
            if (!this.tabs.isEmpty()) {
                // Just use the first tab for now
                this.selectedTab = this.tabs.values().iterator().next();
            }
        }

        int tabSectionY = 0;//(this.tabs.size() + this.tabs.keySet().size()) * 18 + (4 * this.tabs.keySet().size());
        for (var modEntry : tabs.asMap().entrySet()) {
            if (!(modEntry.getKey().equals("sodium") || modEntry.getKey().equals("embeddium")) && (selectedHeader == null || !selectedHeader.equals(modEntry.getKey()))) {
                tabSectionY += 22;
                continue;
            }

            if (selectedHeader != null && selectedHeader.equals(modEntry.getKey()) && modEntry.getValue().size() == 1)
                continue;

            var size = modEntry.getValue().size();
            if ((modEntry.getKey().equals("sodium") || modEntry.getKey().equals("embeddium")) && modEntry.getValue().stream().anyMatch(tab -> Objects.equals(tab.title().getString(), "Shader Packs...")))
                size -= 1;

            tabSectionY += 22 + size * 18;
        }
        this.tabSectionInner = tabSectionY > this.dim.height() ? new Dim2i(this.tabSection.x(), this.tabSection.y(), this.tabSection.width(), tabSectionY) : this.tabSection;

        this.sidebarFrame = ScrollableFrame.createBuilder()
                .setDimension(this.tabSection)
                .setFrame(new TabSidebarFrame(this.tabSectionInner))
                .setVerticalScrollBarOffset(this.tabSectionScrollBarOffset)
                .build();

        this.children.add(this.sidebarFrame);

        this.rebuildTabFrame();

        super.buildFrame();
    }

    private void rebuildTabFrame() {
        if (this.selectedTab == null)
            return;

        #if FORGE AbstractFrame #else me.flashyreese.mods.reeses_sodium_options.client.gui.frame.AbstractFrame #endif frame = this.selectedTab.getFrameFunction().apply(this.frameSection);
        if (frame != null) {
            this.selectedFrame = frame;
            frame.buildFrame();
            this.children.add(frame);
        }
    }

    @Override
    public void render(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
        for (AbstractWidget widget : this.children) {
            if (widget != this.selectedFrame) {
                widget.render(drawContext, mouseX, mouseY, delta);
            }
        }
        if(this.selectedFrame != null) {
            this.selectedFrame.render(drawContext, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return (this.dim.containsCursor(mouseX, mouseY) && super.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    #if AFTER_21_1
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
    #else
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX) {
        return super.mouseScrolled(mouseX, mouseY, scrollX);
    }
    #endif

    public static class Builder {
        private final Multimap<String, Tab<?>> functions = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        private Dim2i dim;
        private boolean renderOutline;
        private Runnable onSetTab;
        private AtomicReference<Component> tabSectionSelectedTab = new AtomicReference<>(null);
        private AtomicReference<Integer> tabSectionScrollBarOffset = new AtomicReference<>(0);

        public Builder setDimension(Dim2i dim) {
            this.dim = dim;
            return this;
        }

        public Builder shouldRenderOutline(boolean renderOutline) {
            this.renderOutline = renderOutline;
            return this;
        }

        public Builder addTabs(Consumer<Multimap<String, Tab<?>>> tabs) {
            tabs.accept(this.functions);
            return this;
        }

        public Builder onSetTab(Runnable onSetTab) {
            this.onSetTab = onSetTab;
            return this;
        }

        public Builder setTabSectionSelectedTab(AtomicReference<Component> tabSectionSelectedTab) {
            this.tabSectionSelectedTab = tabSectionSelectedTab;
            return this;
        }

        public Builder setTabSectionScrollBarOffset(AtomicReference<Integer> tabSectionScrollBarOffset) {
            this.tabSectionScrollBarOffset = tabSectionScrollBarOffset;
            return this;
        }

        public SodiumOptionsTabFrame build() {
            Validate.notNull(this.dim, "Dimension must be specified");

            return new SodiumOptionsTabFrame(this.dim, this.renderOutline, this.functions, this.onSetTab, this.tabSectionSelectedTab, this.tabSectionScrollBarOffset);
        }
    }
}