package toni.sodiumoptionsapi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

#if FABRIC
    import net.fabricmc.api.ClientModInitializer;
    import net.fabricmc.api.ModInitializer;
#endif

#if FORGE
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
#endif


#if NEO
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
#endif


#if FORGELIKE
@Mod("sodiumoptionsapi")
#endif
public class SodiumOptionsAPI #if FABRIC implements ModInitializer, ClientModInitializer #endif
{
	public static final String MODNAME = "Sodium Options API";
	public static final String ID = "sodiumoptionsapi";
	public static final Logger LOGGER = LogManager.getLogger(MODNAME);

	public SodiumOptionsAPI(#if NEO IEventBus modEventBus, ModContainer modContainer #endif) {
        #if FORGE
        var context = FMLJavaModLoadingContext.get();
        var modEventBus = context.getModEventBus();
        #endif

        #if FORGELIKE
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        #endif
	}


	#if FABRIC @Override #endif
	public void onInitialize() {

	}

	#if FABRIC @Override #endif
	public void onInitializeClient() {

	}

	// Forg event stubs to call the Fabric initialize methods, and set up cloth config screen
    #if FORGELIKE
    public void commonSetup(FMLCommonSetupEvent event) { onInitialize(); }
    public void clientSetup(FMLClientSetupEvent event) { onInitializeClient(); }
    #endif
}
