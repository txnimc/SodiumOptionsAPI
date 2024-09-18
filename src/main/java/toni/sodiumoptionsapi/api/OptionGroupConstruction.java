package toni.sodiumoptionsapi.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.List;

#if AFTER_21_1
import net.caffeinemc.mods.sodium.client.gui.options.Option;
#else
import me.jellysquid.mods.sodium.client.gui.options.Option;
#endif

public interface OptionGroupConstruction {
    Event<OptionGroupConstruction> EVENT = EventFactory.createArrayBacked(OptionGroupConstruction.class, (listeners) -> (id, options) -> {
        for (OptionGroupConstruction event : listeners) {
            event.onGroupConstruction(id, options);
        }
    });

    void onGroupConstruction(OptionIdentifier<Void> id, List<Option<?>> options);
}
