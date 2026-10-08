package aldeanoforaflut.aldeanoforaflut.item;

import aldeanoforaflut.aldeanoforaflut.Aldeanoforaflut;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Aldeanoforaflut.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Aldeanoforaflut.MODID);

    // Moneda de los aldeanos transformables
    public static final RegistryObject<Item> DILITIO =
            ITEMS.register("dilitio", () -> new Item(new Item.Properties().stacksTo(64)));

    // Tablet de Admin
    public static final RegistryObject<Item> PHORA_TABLET =
            ITEMS.register("phora_tablet", () -> new PhoraTabletItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == Aldeanoforaflut.EXAMPLE_TAB.getKey()) {
            event.accept(DILITIO);
            event.accept(PHORA_TABLET);
        }
    }
}
