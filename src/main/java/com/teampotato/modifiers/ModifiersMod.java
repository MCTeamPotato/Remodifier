package com.teampotato.modifiers;

import com.teampotato.modifiers.client.events.ClientEvents;
import com.teampotato.modifiers.common.config.toml.*;
import com.teampotato.modifiers.common.curios.ICurioProxy;
import com.teampotato.modifiers.common.events.CommonEvents;
import com.teampotato.modifiers.common.item.ItemModifierBook;
import com.teampotato.modifiers.common.modifier.Modifiers;
import com.teampotato.modifiers.common.network.NetworkHandler;
import com.teampotato.modifiers.common.network.NetworkHandlerForge;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Mod(ModifiersMod.MOD_ID)
public class ModifiersMod {
    public static final String MOD_ID = "modifiers";
    public static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<Item> MODIFIER_BOOK;
    public static final Logger LOGGER = LogManager.getLogger();
    public static ICurioProxy CURIO_PROXY;
    public static ItemGroup GROUP_BOOKS;

    public ModifiersMod() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        final IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        final ModLoadingContext ctx = ModLoadingContext.get();
        final ModConfig.Type common = ModConfig.Type.COMMON;
        NetworkHandler.register();
        ITEM_DEFERRED_REGISTER.register(modEventBus);
        modEventBus.addListener(this::setup);
        forgeBus.register(CommonEvents.class);
        if (FMLLoader.getDist().isClient()) forgeBus.register(ClientEvents.class);
        ctx.registerConfig(common, ReforgeConfig.CONFIG, "remodifier/reforge.toml");
        ctx.registerConfig(common, ArmorConfig.CONFIG, "remodifier/armor-modifiers.toml");
        ctx.registerConfig(common, ToolConfig.CONFIG, "remodifier/tool-modifiers.toml");
        ctx.registerConfig(common, BowConfig.CONFIG, "remodifier/bow-modifiers.toml");
        ctx.registerConfig(common, ShieldConfig.CONFIG, "remodifier/shield-modifiers.toml");
        ctx.registerConfig(common, CuriosConfig.CONFIG, "remodifier/curios-modifiers.toml");
    }

    static ItemStack icon;
    static {
        NetworkHandler.setProxy(new NetworkHandlerForge());

        GROUP_BOOKS = new ItemGroup(-1, MOD_ID +"_books") {
            @Override
            public ItemStack createIcon() {
                if (icon == null) icon = MODIFIER_BOOK.get().getDefaultStack();
                return icon;
            }
        };
    }

    private static Boolean isCuriosLoaded = null;

    public static boolean isCuriosLoaded() {
        if (isCuriosLoaded == null) isCuriosLoaded = ModList.get().isLoaded("curios");
        return isCuriosLoaded;
    }

    private void setup(final FMLCommonSetupEvent event) {
        Modifiers.initialize();
        event.enqueueWork(() -> {
            if (isCuriosLoaded()) {
                try {
                    CURIO_PROXY = (ICurioProxy) Class.forName("com.teampotato.modifiers.common.curios.CurioCompat").getDeclaredConstructor().newInstance();
                    MinecraftForge.EVENT_BUS.register(CURIO_PROXY);
                } catch (Exception e) {
                    LOGGER.error("Remodifier failed to load Curios integration.", e);
                }
            }
            if (CURIO_PROXY == null) CURIO_PROXY = new ICurioProxy() {};
        });
    }

    static {
        MODIFIER_BOOK = ITEM_DEFERRED_REGISTER.register("modifier_book", ItemModifierBook::new);
    }
}
