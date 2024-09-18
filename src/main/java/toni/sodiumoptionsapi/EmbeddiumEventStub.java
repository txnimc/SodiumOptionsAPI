package toni.sodiumoptionsapi;

#if FORGE
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.embeddedt.embeddium.api.OptionPageConstructionEvent;
import toni.sodiumoptionsapi.api.OptionIdentifier;
import toni.sodiumoptionsapi.api.OptionPageConstruction;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = SodiumOptionsAPI.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EmbeddiumEventStub {
    @SubscribeEvent
    public static void onEmbeddiumPagesRegister(OptionPageConstructionEvent e) {
        List<OptionGroup> extraGroups = new ArrayList<>();
        OptionPageConstruction.EVENT.invoker().onPageConstruction(OptionIdentifier.create(e.getId().getModId(), e.getId().getPath()), e.getName(), extraGroups);

        for (var group : extraGroups) {
            e.addGroup(group);
        }
    }
}
#endif