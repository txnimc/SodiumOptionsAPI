package toni.sodiumoptionsapi.mixin.sodium;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toni.sodiumoptionsapi.api.ExtendedOptionGroup;
import toni.sodiumoptionsapi.api.OptionGroupConstruction;
import toni.sodiumoptionsapi.api.OptionIdentifier;

import java.util.List;

#if AFTER_21_1
import net.caffeinemc.mods.sodium.client.gui.options.Option;
import net.caffeinemc.mods.sodium.client.gui.options.OptionGroup;
#else
import me.jellysquid.mods.sodium.client.gui.options.Option;
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
#endif

@Mixin(OptionGroup.Builder.class)
public class OptionGroupBuilderMixin implements ExtendedOptionGroup {

    @Shadow @Final private List<Option<?>> options;

    @Unique
    private static final OptionIdentifier<Void> sodiumOptionsAPI$DEFAULT_ID = OptionIdentifier.create(#if FORGE "embeddium" #else "sodium" #endif, "empty");

    @Unique
    private OptionIdentifier<Void> sodiumOptionsAPI$id;

    @Unique
    public OptionGroup.Builder sodiumOptionsAPI$setId(ResourceLocation id) {
        this.sodiumOptionsAPI$id = OptionIdentifier.create(id);
        return (OptionGroup.Builder) (Object) this;
    }

    @Unique
    public OptionGroup.Builder sodiumOptionsAPI$setId(OptionIdentifier<Void> id) {
        this.sodiumOptionsAPI$id = id;
        return (OptionGroup.Builder) (Object) this;
    }

    @Inject(method = "build", at = @At("HEAD"), remap = false)
    public void onBuild(CallbackInfoReturnable<OptionGroup> cir) {
        if (this.sodiumOptionsAPI$id == null) {
            this.sodiumOptionsAPI$id = sodiumOptionsAPI$DEFAULT_ID;
        }

        OptionGroupConstruction.EVENT.invoker().onGroupConstruction(this.sodiumOptionsAPI$id, this.options);
    }
}
