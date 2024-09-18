package toni.sodiumoptionsapi.mixin.sodium;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toni.sodiumoptionsapi.SodiumOptionsAPI;
import toni.sodiumoptionsapi.api.OptionIdentifier;
import toni.sodiumoptionsapi.api.OptionPageConstruction;
import toni.sodiumoptionsapi.util.IOptionGroupIdAccessor;
import toni.sodiumoptionsapi.util.OptionIdGenerator;

import java.util.ArrayList;
import java.util.List;

#if AFTER_21_1
import net.caffeinemc.mods.sodium.client.gui.options.OptionGroup;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
#else
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
#endif

@Mixin(OptionPage.class)
public class OptionPageMixin implements IOptionGroupIdAccessor {
    @Unique
    private static final OptionIdentifier<Void> sodiumOptionsAPI$DEFAULT_ID = OptionIdentifier.create(#if FORGE "embeddium" #else "sodium" #endif, "empty");

    @Unique
    private OptionIdentifier<Void> sodiumOptionsAPI$id;

    @Mutable
    @Shadow @Final private ImmutableList<OptionGroup> groups;
    @Shadow @Final private Component name;

    #if FORGE
    @Inject(method = "<init>(Lorg/embeddedt/embeddium/client/gui/options/OptionIdentifier;Lnet/minecraft/network/chat/Component;Lcom/google/common/collect/ImmutableList;)V", at = @At(value = "RETURN"))
    public void onInit(org.embeddedt.embeddium.client.gui.options.OptionIdentifier id, Component name, ImmutableList groups, CallbackInfo ci) {
        sodiumOptionsAPI$id = sodiumOptionsAPI$tryMakeId(name);
    }
    #else
    @Inject(method = "<init>", at = @At(unsafe = true, value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;builder()Lcom/google/common/collect/ImmutableList$Builder;"))
    public void onInit(Component name, ImmutableList<OptionGroup> groups, CallbackInfo ci) {
        this.groups = sodiumOptionsAPI$collectExtraGroups(groups);
        sodiumOptionsAPI$id = sodiumOptionsAPI$tryMakeId(name);
    }
    #endif


    @Unique
    private ImmutableList<OptionGroup> sodiumOptionsAPI$collectExtraGroups(ImmutableList<OptionGroup> groups) {
        List<OptionGroup> extraGroups = new ArrayList<>();
        OptionPageConstruction.EVENT.invoker().onPageConstruction(this.sodiumOptionsAPI$id, this.name, extraGroups);

        return extraGroups.isEmpty() ? groups : ImmutableList.<OptionGroup>builder().addAll(groups).addAll(extraGroups).build();
    }

    @Override
    public OptionIdentifier<Void> sodiumOptionsAPI$getId() {
        if (sodiumOptionsAPI$id == null)
            return sodiumOptionsAPI$DEFAULT_ID;

        return sodiumOptionsAPI$id;
    }

    @Unique
    private static OptionIdentifier<Void> sodiumOptionsAPI$tryMakeId(Component name) {
        OptionIdentifier<Void> id;
        if(name.getContents() instanceof TranslatableContents translatableContents) {
            String key = translatableContents.getKey();
            if(name.getSiblings().isEmpty()) {
                // Detect our own tabs
                id = OptionIdGenerator.generateId(key);
            } else {
                id = OptionIdGenerator.generateId(key);
            }
        } else {
            id = OptionIdGenerator.generateId(name.getString());
        }
        if(id != null) {
            SodiumOptionsAPI.LOGGER.debug("Guessed ID for legacy OptionPage '{}': {}", name.getString(), id);
            return id;
        } else {
            SodiumOptionsAPI.LOGGER.warn("Id must be specified in OptionPage '{}'", name.getString());
            return sodiumOptionsAPI$DEFAULT_ID;
        }
    }
}
