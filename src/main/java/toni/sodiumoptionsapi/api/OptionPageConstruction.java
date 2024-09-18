package toni.sodiumoptionsapi.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.chat.Component;

import java.util.List;

#if AFTER_21_1
import net.caffeinemc.mods.sodium.client.gui.options.OptionGroup;
#else
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
#endif

public interface OptionPageConstruction {
    Event<OptionPageConstruction> EVENT = EventFactory.createArrayBacked(OptionPageConstruction.class, (listeners) -> (id, name, additionalGroups) -> {
        for (OptionPageConstruction event : listeners) {
            event.onPageConstruction(id, name, additionalGroups);
        }
    });

    void onPageConstruction(OptionIdentifier<Void> id, Component name, List<OptionGroup> additionalGroups);
}