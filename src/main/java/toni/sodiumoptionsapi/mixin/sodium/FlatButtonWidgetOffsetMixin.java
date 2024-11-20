package toni.sodiumoptionsapi.mixin.sodium;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import toni.sodiumoptionsapi.util.ILeftAlignOffsetAccessor;

#if AFTER_21_1
import net.caffeinemc.mods.sodium.client.gui.widgets.FlatButtonWidget;
import net.caffeinemc.mods.sodium.client.util.Dim2i;

#else
import me.jellysquid.mods.sodium.client.gui.widgets.FlatButtonWidget;
import me.jellysquid.mods.sodium.client.util.Dim2i;
#endif

#if !FORGE
import com.bawnorton.mixinsquared.TargetHandler;
import me.flashyreese.mods.reeses_sodium_options.client.gui.FlatButtonWidgetExtended;
#endif

@Mixin(value = FlatButtonWidget.class, priority = 10000)
public class FlatButtonWidgetOffsetMixin implements ILeftAlignOffsetAccessor {

    @Shadow @Final private Dim2i dim;

    @Unique
    private int sodiumOptionsAPI$leftAlignedTextOffset = 10;

    #if AFTER_21_1
    @TargetHandler(
        mixin = "me.flashyreese.mods.reeses_sodium_options.mixin.sodium.MixinFlatButtonWidget",
        name = "redirectDrawString"
    )
    @Inject(
        method = "@MixinSquared:Handler",
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    public void redirectDrawString(Args args, CallbackInfo ci) {
        int textX = this.dim.x() + sodiumOptionsAPI$leftAlignedTextOffset;
        if (((FlatButtonWidgetExtended) this).isLeftAligned()) {
            args.set(2, textX);
            //guiGraphics.drawString(Minecraft.getInstance().font, text, textX, y, color);
            ci.cancel();
        }
    }

    #elif !FORGE
    @TargetHandler(
            mixin = "me.flashyreese.mods.reeses_sodium_options.mixin.sodium.MixinFlatButtonWidget",
            name = "redirectDrawString"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void redirectDrawString(FlatButtonWidget instance, GuiGraphics guiGraphics, Component text, int x, int y, int color, CallbackInfo ci) {
        int textX = this.dim.x() + sodiumOptionsAPI$leftAlignedTextOffset;
        if (((FlatButtonWidgetExtended) instance).isLeftAligned()) {
            guiGraphics.drawString(Minecraft.getInstance().font, text, textX, y, color);
            ci.cancel();
        }
    }
    #else
    @Inject(method = "getLeftAlignedTextOffset", at = @At("HEAD"), cancellable = true, remap = false)
    public void getLeftOffset(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(sodiumOptionsAPI$leftAlignedTextOffset);
    }
    #endif

    @Override
    public void sodiumOptionsAPI$setLeftAlignOffset(int leftAlignOffset) {
        sodiumOptionsAPI$leftAlignedTextOffset = leftAlignOffset;
    }
}
