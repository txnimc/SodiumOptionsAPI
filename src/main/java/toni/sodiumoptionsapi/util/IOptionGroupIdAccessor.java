package toni.sodiumoptionsapi.util;

import net.minecraft.resources.ResourceLocation;
import toni.sodiumoptionsapi.api.OptionIdentifier;

public interface IOptionGroupIdAccessor {
    public OptionIdentifier<Void> sodiumOptionsAPI$getId();
    public void sodiumOptionsAPI$setId(OptionIdentifier<Void> id);
    public void sodiumOptionsAPI$setId(ResourceLocation id);
}
