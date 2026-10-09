package aldeanoforaflut.aldeanoforaflut.trade;

import aldeanoforaflut.aldeanoforaflut.Aldeanoforaflut;
import aldeanoforaflut.aldeanoforaflut.entity.TransformableMerchantEntity;
import net.minecraft.nbt.CompoundTag;
import aldeanoforaflut.aldeanoforaflut.sound.ModSounds;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Reabastece las ofertas de los aldeanos transformables una vez por día de Minecraft.
 * El último día de reabastecimiento se guarda en los datos persistentes de la entidad.
 */
@Mod.EventBusSubscriber(modid = Aldeanoforaflut.MODID)
public class DilitioRestockHandler {

    public static final long TICKS_PER_DAY = 24000L;
    // Cada cuántos ticks se revisa si cambió el día
    public static final int CHECK_INTERVAL_TICKS = 20;

    private static final String LAST_RESTOCK_DAY_KEY = Aldeanoforaflut.MODID + ":last_restock_day";

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof TransformableMerchantEntity merchant)
                || merchant.level().isClientSide
                || merchant.tickCount % CHECK_INTERVAL_TICKS != 0) {
            return;
        }

        long currentDay = merchant.level().getDayTime() / TICKS_PER_DAY;
        CompoundTag data = merchant.getPersistentData();
        if (!data.contains(LAST_RESTOCK_DAY_KEY)) {
            data.putLong(LAST_RESTOCK_DAY_KEY, currentDay);
            return;
        }

        // Se compara con != para que /time set hacia atrás también cuente como día nuevo
        if (data.getLong(LAST_RESTOCK_DAY_KEY) == currentDay || merchant.getTradingPlayer() != null) {
            return;
        }

        data.putLong(LAST_RESTOCK_DAY_KEY, currentDay);
        if (DilitioTrades.restock(merchant.getOffers(), merchant::isOfferUnlocked)) {
            merchant.playSound(ModSounds.PHORA_RESTOCK.get(), ModSounds.PHORA_VOLUME, 1.0f);
        }
    }
}
