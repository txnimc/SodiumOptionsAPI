package toni.sodiumoptionsapi.gui;

import com.mojang.blaze3d.platform.NativeImage;
import net.caffeinemc.mods.sodium.api.util.ColorARGB;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;

import toni.sodiumoptionsapi.SodiumOptionsAPI;
import toni.sodiumoptionsapi.mixin.sodium.FlatButtonWidgetAccessor;
import toni.sodiumoptionsapi.util.ILeftAlignOffsetAccessor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

#if AFTER_21_1
import net.minecraft.server.packs.PackLocationInfo;
import net.caffeinemc.mods.sodium.client.gui.widgets.FlatButtonWidget;
import net.caffeinemc.mods.sodium.client.util.Dim2i;
#else
import me.jellysquid.mods.sodium.client.gui.widgets.FlatButtonWidget;
import me.jellysquid.mods.sodium.client.util.Dim2i;
#endif

#if NEO
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.resource.ResourcePackLoader;
#endif

#if FORGE
import net.minecraftforge.resource.PathPackResources;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.resource.ResourcePackLoader;
#endif

#if FABRIC
import net.fabricmc.loader.api.FabricLoader;
#endif

public class TabHeaderWidget extends FlatButtonWidget {
    private static final ResourceLocation FALLBACK_LOCATION = #if AFTER_21_1 ResourceLocation.withDefaultNamespace #else new ResourceLocation #endif("textures/misc/unknown_pack.png");

    private static final Set<String> erroredLogos = new HashSet<>();
    private final ResourceLocation logoTexture;
    private final boolean isTitle;

    public static MutableComponent getLabel(String modId, boolean underline ) {
        return (switch(modId) {
            // TODO handle long mod names better, this is the only one we know of right now
            case "sspb" -> Component.literal("SSPB");
            default -> idComponent(modId);
        }).withStyle(s -> s.withUnderlined(underline));
    }

    static MutableComponent idComponent(String namespace) {
        return Component.literal(getModName(namespace));
    }

    public static String getModName(String modId) {
        #if FORGELIKE
        return ModList.get().getModContainerById(modId).map(container -> container.getModInfo().getDisplayName()).orElse(modId);
        #else
        return FabricLoader.getInstance().getModContainer(modId).map(container -> container.getMetadata().getName()).orElse(modId);
        #endif
    }

    public TabHeaderWidget(Dim2i dim, String modId, Runnable action) {
        super(dim, getLabel(modId, action == null), action == null ? () -> { } : action);

        isTitle = action == null;

        #if FORGELIKE
        Optional<String> logoFile = erroredLogos.contains(modId) ? Optional.empty() : ModList.get().getModContainerById(modId).flatMap(c -> c.getModInfo().getLogoFile());
        #elif FABRIC
        Optional<Path> logoFile = erroredLogos.contains(modId) ? Optional.empty() : FabricLoader.getInstance().getModContainer(modId).flatMap(c -> c.getMetadata().getIconPath(32).flatMap(c::findPath));
        #endif

        ResourceLocation texture = null;
        if(logoFile.isPresent()) {
            #if FABRIC
            try(InputStream is = Files.newInputStream(logoFile.get())) {
                if (is != null) {
                    NativeImage logo = NativeImage.read(is);
                    if(logo.getWidth() != logo.getHeight()) {
                        logo.close();
                        throw new IOException("Logo " + logoFile.get() + " for " + modId + " is not square");
                    }
                    texture = #if AFTER_21_1 ResourceLocation.fromNamespaceAndPath #else new ResourceLocation #endif("sodium", "logo/" + modId);
                    Minecraft.getInstance().getTextureManager().register(texture, new DynamicTexture(logo));
                }
            } catch(IOException e) {
                erroredLogos.add(modId);
                SodiumOptionsAPI.LOGGER.error("Exception reading logo for " + modId, e);
            }
            #elif FORGE
            final PathPackResources resourcePack = ResourcePackLoader.getPackFor(modId)
                    .orElse(ResourcePackLoader.getPackFor("forge").
                            orElseThrow(()->new RuntimeException("Can't find forge, WHAT!")));
            try {
                IoSupplier<InputStream> logoResource = resourcePack.getRootResource(logoFile.get());
                if (logoResource != null) {
                    NativeImage logo = NativeImage.read(logoResource.get());
                    if(logo.getWidth() != logo.getHeight()) {
                        logo.close();
                        throw new IOException("Logo " + logoFile.get() + " for " + modId + " is not square");
                    }
                    texture = new ResourceLocation(#if FORGE "embeddium" #else "sodium" #endif, "logo/" + modId);
                    Minecraft.getInstance().textureManager.register(texture, new DynamicTexture(logo));
                }
            } catch(IOException e) {
                erroredLogos.add(modId);
                SodiumOptionsAPI.LOGGER.error("Exception reading logo for " + modId, e);
            }
            #else
            final Pack.ResourcesSupplier supplier = ResourcePackLoader.getPackFor(modId).orElse(ResourcePackLoader.getPackFor("neoforge").orElseThrow(()->new RuntimeException("Can't find neoforge, WHAT!")));
            try(PackResources pack = supplier.openPrimary(new PackLocationInfo("mod:" + modId, Component.empty(), PackSource.BUILT_IN, Optional.empty()))) {
                IoSupplier<InputStream> logoResource = pack.getRootResource(logoFile.get().split("/"));
                if (logoResource != null) {
                    NativeImage logo = NativeImage.read(logoResource.get());
                    if(logo.getWidth() != logo.getHeight()) {
                        logo.close();
                        throw new IOException("Logo " + logoFile.get() + " for " + modId + " is not square");
                    }
                    texture = ResourceLocation.fromNamespaceAndPath("sodium", "logo/" + modId);
                    Minecraft.getInstance().getTextureManager().register(texture, new DynamicTexture(logo));
                }
            } catch(IOException e) {
                erroredLogos.add(modId);
                SodiumOptionsAPI.LOGGER.error("Exception reading logo for " + modId, e);
            }
            #endif
        }

        this.setStyle(getStyle());
        this.logoTexture = texture;
    }

    public FlatButtonWidget.Style getStyle() {
        FlatButtonWidget.Style style = new FlatButtonWidget.Style();
        style.bgHovered = isTitle ? ColorARGB.pack(0, 0, 0, 140) : -536870912;
        style.bgDefault = ColorARGB.pack(0, 0, 0, 140);
        style.bgDisabled = 1610612736;
        style.textDefault = -1;
        style.textDisabled = -1862270977;
        return style;
    }

//    @Override
//    protected int getLeftAlignedTextOffset() {
//        return super.getLeftAlignedTextOffset() + Minecraft.getInstance().font.lineHeight;
//    }
//
    protected boolean isHovered(int mouseX, int mouseY) {
        return false;
    }

    @Override
    public void render(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
        ((ILeftAlignOffsetAccessor) this).sodiumOptionsAPI$setLeftAlignOffset(20);
        super.render(drawContext, mouseX, mouseY, delta);

        var dim = ((FlatButtonWidgetAccessor)this).getDim();
        //var extraHeight = logoTexture.getPath().equals("logo/sodium") ? 0 : 4;
        //drawContext.fillGradient(dim.x(), dim.y() + 3, dim.x() + dim.width(), dim.y() + dim.height(), ColorARGB.pack(0, 0, 0, 0), ColorARGB.pack(0, 0, 0, 140));
        //drawContext.fill(dim.x(), dim.y() + extraHeight, dim.x() + dim.width(), dim.y() + dim.height(), ColorARGB.pack(0, 0, 0, 140));

        ResourceLocation icon = Objects.requireNonNullElse(this.logoTexture, FALLBACK_LOCATION);
        int fontHeight = Minecraft.getInstance().font.lineHeight;
        int imgY = ((FlatButtonWidgetAccessor)this).getDim().getCenterY() - (fontHeight / 2) ;
        drawContext.blit(icon, ((FlatButtonWidgetAccessor)this).getDim().x() + 5, imgY, 0.0f, 0.0f, fontHeight, fontHeight, fontHeight, fontHeight);
    }
}
