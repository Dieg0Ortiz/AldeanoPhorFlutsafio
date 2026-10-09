package aldeanoforaflut.aldeanoforaflut.sound;

import aldeanoforaflut.aldeanoforaflut.Aldeanoforaflut;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Sonidos del Phora (estilo computadora de ciencia ficción).
 * Los audios se definen en assets/aldeanoforaflut/sounds.json: para usar .ogg propios
 * basta con cambiar ese JSON, sin tocar el código.
 */
public class ModSounds {
    /** Volumen global de todos los sonidos del Phora (1.0 = volumen original del audio). */
    public static final float PHORA_VOLUME = 0.35f;

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Aldeanoforaflut.MODID);

    public static final RegistryObject<SoundEvent> PHORA_POWER_ON = register("phora.power_on");
    public static final RegistryObject<SoundEvent> PHORA_BOOT_READY = register("phora.boot_ready");
    public static final RegistryObject<SoundEvent> PHORA_POWER_OFF = register("phora.power_off");
    public static final RegistryObject<SoundEvent> PHORA_AMBIENT = register("phora.ambient");
    public static final RegistryObject<SoundEvent> PHORA_PROCESSING = register("phora.processing");
    public static final RegistryObject<SoundEvent> PHORA_ACCEPT = register("phora.accept");
    public static final RegistryObject<SoundEvent> PHORA_DENY = register("phora.deny");
    public static final RegistryObject<SoundEvent> PHORA_TRADE = register("phora.trade");
    public static final RegistryObject<SoundEvent> PHORA_RESTOCK = register("phora.restock");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name,
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Aldeanoforaflut.MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
