package aldeanoforaflut.aldeanoforaflut.entity;

import aldeanoforaflut.aldeanoforaflut.trade.DilitioTrades;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

public class TransformableMerchantEntity extends WanderingTrader {

    public TransformableMerchantEntity(EntityType<? extends WanderingTrader> entityType, Level level) {
        super(entityType, level);
        // Generar ofertas iniciales.
        // Las almacenaremos en this.offers provisto por AbstractVillager
        this.initCustomTrades();
    }

    private void initCustomTrades() {
        MerchantOffers customOffers = new MerchantOffers();
        DilitioTrades.addAll(customOffers, this.random);
        this.offers = customOffers;
    }

    @Override
    protected void updateTrades() {
        // En WanderingTrader, updateTrades sobreescribe las ofertas.
        // Lo dejamos vacío para que NUNCA modifique nuestras ofertas estáticas.
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            // Un jugador a la vez
            if (this.getTradingPlayer() != null) {
                return InteractionResult.FAIL;
            }
            if (this.offers != null && !this.offers.isEmpty()) {
                this.setTradingPlayer(player);
                this.openTradingScreen(player, this.getDisplayName(), 1);
            }
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }
}
