package aldeanoforaflut.aldeanoforaflut.entity;

import aldeanoforaflut.aldeanoforaflut.Aldeanoforaflut;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Aldeanoforaflut.MODID);

    public static final RegistryObject<EntityType<TransformableMerchantEntity>> TRANSFORMABLE_MERCHANT =
            ENTITY_TYPES.register("transformable_merchant",
                    () -> EntityType.Builder.of(TransformableMerchantEntity::new, MobCategory.MISC)
                            .sized(1.0f, 1.0f) // Tamaño similar a un bloque cuando está inactivo
                            .build("transformable_merchant"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
