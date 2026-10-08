package aldeanoforaflut.aldeanoforaflut.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
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
        customOffers.add(new MerchantOffer(
                new ItemStack(Items.EMERALD, 5),
                new ItemStack(Items.DIAMOND, 1),
                10, 8, 0.02f
        ));
        customOffers.add(new MerchantOffer(
                new ItemStack(Items.GOLD_INGOT, 10),
                new ItemStack(Items.NETHERITE_INGOT, 1),
                5, 10, 0.05f
        ));
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
