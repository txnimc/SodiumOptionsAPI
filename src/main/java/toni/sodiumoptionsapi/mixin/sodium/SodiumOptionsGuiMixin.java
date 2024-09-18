package toni.sodiumoptionsapi.mixin.sodium;

import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import toni.sodiumoptionsapi.api.OptionGUIConstruction;

#if AFTER_21_1
import net.caffeinemc.mods.sodium.client.gui.SodiumOptionsGUI;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
#else
import me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
#endif

@Mixin(value = SodiumOptionsGUI.class, remap = false, priority = 100)
public class SodiumOptionsGuiMixin {

    @Shadow
    @Final
    private List<OptionPage> pages;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void inject$dynLightsPage(Screen prevScreen, CallbackInfo ci) {
        OptionGUIConstruction.EVENT.invoker().onGroupConstruction(this.pages);
    }

}