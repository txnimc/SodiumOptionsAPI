package toni.sodiumoptionsapi.util;

#if FABRIC
import net.fabricmc.loader.api.FabricLoader;
#elif FORGE
import net.minecraftforge.fml.loading.FMLLoader;
#else
import net.neoforged.fml.loading.FMLLoader;
#endif


public class PlatformUtil {
    public static boolean modPresent(String modid) {
        #if FABRIC
        return FabricLoader.getInstance().isModLoaded(modid);
        #else
        return FMLLoader.getLoadingModList().getModFileById(modid) != null;
        #endif
    }
}
