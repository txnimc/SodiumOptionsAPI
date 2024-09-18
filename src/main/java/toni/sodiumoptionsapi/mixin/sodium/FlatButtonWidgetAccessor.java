package toni.sodiumoptionsapi.mixin.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

#if AFTER_21_1
import net.caffeinemc.mods.sodium.client.gui.widgets.FlatButtonWidget;
import net.caffeinemc.mods.sodium.client.util.Dim2i;
#else
import me.jellysquid.mods.sodium.client.gui.widgets.FlatButtonWidget;
import me.jellysquid.mods.sodium.client.util.Dim2i;
#endif

@Mixin(FlatButtonWidget.class)
public interface FlatButtonWidgetAccessor {
    @Accessor("dim")
    public Dim2i getDim();
}
