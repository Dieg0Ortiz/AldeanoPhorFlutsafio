package aldeanoforaflut.aldeanoforaflut.entity;

import aldeanoforaflut.aldeanoforaflut.Aldeanoforaflut;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.goal.UseItemGoal;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Ajusta la IA heredada de WanderingTrader sin modificar la entidad:
 * el comerciante no toma la poción de invisibilidad de noche ni la leche al amanecer.
 */
@Mod.EventBusSubscriber(modid = Aldeanoforaflut.MODID)
public class MerchantAiHandler {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()
                || !(event.getEntity() instanceof TransformableMerchantEntity merchant)) {
            return;
        }

        merchant.goalSelector.removeAllGoals(goal -> goal instanceof UseItemGoal<?>);
        // Aldeanos de mundos guardados que ya se habían tomado la poción
        merchant.removeEffect(MobEffects.INVISIBILITY);
    }
}
