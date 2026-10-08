package aldeanoforaflut.aldeanoforaflut.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

public class TransformableMerchantEntity extends Villager {

    public TransformableMerchantEntity(EntityType<? extends Villager> entityType, Level level) {
        super(entityType, level);
        this.setCustomName(net.minecraft.network.chat.Component.literal("Mercader Oculto"));
        this.setCustomNameVisible(true);
        this.initTrades();
    }

    private void initTrades() {
        MerchantOffers offers = new MerchantOffers();
        offers.add(new MerchantOffer(
                new ItemStack(Items.EMERALD, 5),
                new ItemStack(Items.DIAMOND, 1),
                10, 8, 0.02f
        ));
        offers.add(new MerchantOffer(
                new ItemStack(Items.GOLD_INGOT, 10),
                new ItemStack(Items.NETHERITE_INGOT, 1),
                5, 10, 0.05f
        ));
        this.offers = offers;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            // Solo 1 jugador puede interactuar a la vez
            if (this.getTradingPlayer() != null) {
                return InteractionResult.FAIL;
            }
            if (!this.getOffers().isEmpty()) {
                this.setTradingPlayer(player);
                this.openTradingScreen(player, this.getDisplayName(), 1);
            }
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }
}
