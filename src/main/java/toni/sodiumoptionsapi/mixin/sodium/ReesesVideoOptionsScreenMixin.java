package toni.sodiumoptionsapi.mixin.sodium;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.TextureFormat;
import net.caffeinemc.mods.sodium.api.util.ColorARGB;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toni.sodiumoptionsapi.SodiumOptionsAPI;
import toni.sodiumoptionsapi.gui.SodiumOptionsTabFrame;
import toni.sodiumoptionsapi.util.IOptionGroupIdAccessor;
import toni.sodiumoptionsapi.util.PlatformUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Stream;

#if FORGE
import org.embeddedt.embeddium.client.gui.options.OptionIdentifier;
import org.embeddedt.embeddium.gui.EmbeddiumVideoOptionsScreen;
import org.embeddedt.embeddium.gui.frame.AbstractFrame;
import org.embeddedt.embeddium.gui.frame.BasicFrame;
import org.embeddedt.embeddium.gui.frame.components.SearchTextFieldComponent;
import org.embeddedt.embeddium.gui.frame.tab.Tab;
import org.embeddedt.embeddium.render.ShaderModBridge;
#else
import me.flashyreese.mods.reeses_sodium_options.client.gui.SodiumVideoOptionsScreen;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.AbstractFrame;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.BasicFrame;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.components.SearchTextFieldComponent;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.tab.Tab;
import me.flashyreese.mods.reeses_sodium_options.compat.IrisCompat;
#endif

#if AFTER_21_1
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
import net.caffeinemc.mods.sodium.client.gui.widgets.AbstractWidget;
import net.caffeinemc.mods.sodium.client.util.Dim2i;
#else
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import me.jellysquid.mods.sodium.client.gui.widgets.AbstractWidget;
import me.jellysquid.mods.sodium.client.util.Dim2i;
#endif

@Mixin(#if FORGE EmbeddiumVideoOptionsScreen.class #else SodiumVideoOptionsScreen.class #endif )
public class ReesesVideoOptionsScreenMixin  {

    #if FORGE
    @Shadow @Final private AtomicReference<Integer> tabFrameScrollBarOffset;
    @Shadow @Final private AtomicReference<Integer> optionPageScrollBarOffset;
    #else
    @Shadow @Final private static AtomicReference<Integer> tabFrameScrollBarOffset;
    @Shadow @Final private static AtomicReference<Integer> optionPageScrollBarOffset;
    #endif

    @Shadow @Final private List<OptionPage> pages;
    @Shadow private SearchTextFieldComponent searchTextField;
    @Shadow @Final private static AtomicReference<Component> tabFrameSelectedTab;

    @Unique
    private static final ResourceLocation LOGO_LOCATION = #if AFTER_21_1 ResourceLocation.fromNamespaceAndPath #else new ResourceLocation #endif ("sodiumoptionsapi", "textures/sodiumoptionsapi/gui/logo_transparent.png");

    @Unique
    private Dim2i sodiumOptionsAPI$logoDim;


    @Inject(method = "<init>", at = @At("RETURN"))
    private void inject$registerTextures(Screen prev, List pages, CallbackInfo ci) {
        #if mc < 215
        Minecraft.getInstance().getTextureManager().register(LOGO_LOCATION, new SimpleTexture(SodiumOptionsAPI.LOGO_LOCATION));
        #endif
    }

    @Inject(method = "parentFrameBuilder", at = @At(value = "RETURN"), remap = false)
    private void setLogoDim(CallbackInfoReturnable<BasicFrame.Builder> cir, @Local(ordinal = 1) Dim2i tabFrameDim) {
        int logoSizeOnScreen = 20;
        this.sodiumOptionsAPI$logoDim = new Dim2i(tabFrameDim.x(), tabFrameDim.getLimitY() + 25 - logoSizeOnScreen, logoSizeOnScreen, logoSizeOnScreen);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void onRender(GuiGraphics gfx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        #if !FORGE
        //System.out.println("onreounder");
        var ths = (#if FORGE EmbeddiumVideoOptionsScreen #else SodiumVideoOptionsScreen #endif) (Object) this;

        var color = ColorARGB.pack(115,197,95);
        //ths.renderBackground(gfx, mouseX, mouseY, delta);
        #if mc < 214
        gfx.setColor((float) ColorARGB.unpackRed(color) / 255.0F, (float)ColorARGB.unpackGreen(color) / 255.0F, (float)ColorARGB.unpackBlue(color) / 255.0F, 0.8F);
        #endif

        #if mc < 215
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 1);

        #if mc >= 214
        gfx.blit(RenderType::guiTextured, LOGO_LOCATION, this.sodiumOptionsAPI$logoDim.x(), this.sodiumOptionsAPI$logoDim.y(), this.sodiumOptionsAPI$logoDim.width(), this.sodiumOptionsAPI$logoDim.height(), 0, 0, 256, 256, 256, 256, ColorARGB.pack(115,197,95, 204));
        #else
        gfx.blit(LOGO_LOCATION, this.sodiumOptionsAPI$logoDim.x(), this.sodiumOptionsAPI$logoDim.y(), this.sodiumOptionsAPI$logoDim.width(), this.sodiumOptionsAPI$logoDim.height(), 0.0F, 0.0F, 256, 256, 256, 256);
        #endif

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        #endif

        #if mc < 214
        gfx.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        #endif

        #endif
    }

    #if FORGE
    @Redirect(method = "parentBasicFrameBuilder", at = @At(value = "INVOKE", ordinal = 2, target = "Lorg/embeddedt/embeddium/gui/frame/BasicFrame$Builder;addChild(Ljava/util/function/Function;)Lorg/embeddedt/embeddium/gui/frame/BasicFrame$Builder;"), remap = false)
    #else
    @Redirect(method = "parentBasicFrameBuilder", at = @At(value = "INVOKE", ordinal = #if mc >= 211 0 #else 2 #endif, target = "Lme/flashyreese/mods/reeses_sodium_options/client/gui/frame/BasicFrame$Builder;addChild(Ljava/util/function/Function;)Lme/flashyreese/mods/reeses_sodium_options/client/gui/frame/BasicFrame$Builder;"), remap = false)
    #endif
    private BasicFrame.Builder tabFrameBuilder(BasicFrame.Builder instance, Function<Dim2i, AbstractWidget> function, @Local(ordinal = 1, argsOnly = true) Dim2i tabFrameDim) {
        instance.addChild((parentDim) -> SodiumOptionsTabFrame.createBuilder()
                .setDimension(tabFrameDim)
                .shouldRenderOutline(false)
                .setTabSectionScrollBarOffset(tabFrameScrollBarOffset)
                .setTabSectionSelectedTab(tabFrameSelectedTab)
                .addTabs(tabs -> this.pages
                        .stream()
                        .filter(this::sodiumOptionsAPI$isSodiumTab)
                        .forEach(page -> tabs.put(((IOptionGroupIdAccessor)page).sodiumOptionsAPI$getId().getModId(), #if AFTER_21_1 Tab.builder() #else Tab.createBuilder() #endif.from(page, #if FORGE (option) -> true, #endif optionPageScrollBarOffset)))                )
                .addTabs(this::sodiumOptionsAPI$createShaderPackButton)
                .addTabs(tabs -> this.pages
                        .stream()
                        .filter((tab) -> !sodiumOptionsAPI$isSodiumTab(tab))
                        .forEach(page -> tabs.put(((IOptionGroupIdAccessor)page).sodiumOptionsAPI$getId().getModId(), #if AFTER_21_1 Tab.builder() #else Tab.createBuilder() #endif.from(page, #if FORGE (option) -> true, #endif optionPageScrollBarOffset)))                )
                .onSetTab(() -> {
                    optionPageScrollBarOffset.set(0);
                })
                .build());

        return instance;
    }

    @Unique
    private boolean sodiumOptionsAPI$isSodiumTab(OptionPage optionPage) {
        if (optionPage.getName().getString().equals("Shader Packs...") || optionPage.getName().getString().equals("Oculus"))
            return false;

        return Objects.equals(((IOptionGroupIdAccessor) optionPage).sodiumOptionsAPI$getId().getModId(), "sodium") || Objects.equals(((IOptionGroupIdAccessor) optionPage).sodiumOptionsAPI$getId().getModId(), "embeddium");
    }

    @Unique
    private void sodiumOptionsAPI$createShaderPackButton(Multimap<String, Tab<?>> tabs) {
        #if FORGE
        var iris = ShaderModBridge.isShaderModPresent();
        #else
        var iris = IrisCompat.isIrisPresent();
        #endif

        if (iris) {
            String shaderModId = (String) Stream.of("oculus", "iris").filter(PlatformUtil::modPresent).findFirst().orElse("iris");

            #if FORGE
                var builder = Tab.createBuilder()
                        .setTitle(Component.translatable("options.iris.shaderPackSelection"))
                        .setFrameFunction(this::sodiumOptionsAPI$getFrame)
                        .setId(OptionIdentifier.create("iris", "shader_packs"))
                        .setOnSelectFunction(() -> {
                            if(ShaderModBridge.openShaderScreen((Screen) (Object) this) instanceof Screen screen) {
                                ((Screen) (Object) this).getMinecraft().setScreen(screen);
                            }
                            return false;
                        });
            #else
                #if AFTER_21_1
                var builder = Tab.builder()
                    .withTitle(Component.translatable("options.iris.shaderPackSelection"))
                    .withFrameFunction(this::sodiumOptionsAPI$getFrame);
                #else
                var builder = Tab.createBuilder()
                        .setTitle(Component.translatable("options.iris.shaderPackSelection"))
                        .setFrameFunction(this::sodiumOptionsAPI$getFrame);
                #endif
            #endif

            tabs.put(shaderModId, builder.build());
        }

    }

    @Unique
    private <T extends AbstractFrame> T sodiumOptionsAPI$getFrame(Dim2i dim) {
        return (T) new BasicFrame(dim, false, new ArrayList());
    }
//
//    private boolean canShowPage(OptionPage page) {
//        if(page.getGroups().isEmpty()) {
//            return false;
//        }
//
//        // Check if any options on this page are visible
//        var predicate = searchTextField.isActive();
//
//        for(OptionGroup group : page.getGroups()) {
//            for(Option<?> option : group.getOptions()) {
//                if(predicate.test(option)) {
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }
}
