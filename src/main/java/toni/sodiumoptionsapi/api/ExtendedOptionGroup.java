package toni.sodiumoptionsapi.api;

#if AFTER_21_1
import net.caffeinemc.mods.sodium.client.gui.options.Option;
import net.caffeinemc.mods.sodium.client.gui.options.OptionGroup;
#else
import me.jellysquid.mods.sodium.client.gui.options.Option;
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
#endif

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Unique;

public interface ExtendedOptionGroup {


    public OptionGroup.Builder sodiumOptionsAPI$setId(ResourceLocation id);

    public OptionGroup.Builder sodiumOptionsAPI$setId(OptionIdentifier<Void> id);


    public static OptionGroup.Builder createBuilder(OptionIdentifier<Void> id) {

        var builder = OptionGroup.createBuilder();
        ((ExtendedOptionGroup) builder).sodiumOptionsAPI$setId(id);
        return builder;
    }

    public static OptionGroup.Builder createBuilder(ResourceLocation id) {

        var builder = OptionGroup.createBuilder();
        ((ExtendedOptionGroup) builder).sodiumOptionsAPI$setId(id);
        return builder;
    }
}
