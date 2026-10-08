package aldeanoforaflut.aldeanoforaflut.item;

import aldeanoforaflut.aldeanoforaflut.trade.TradeConfigData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class PhoraTabletItem extends Item {

    public PhoraTabletItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            // Solo ops/admins pueden usar la tablet
            if (player.hasPermissions(2)) {
                // Leer datos y enviarlos al cliente
                TradeConfigData data = TradeConfigData.get((net.minecraft.server.level.ServerLevel) level);
                net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
                data.save(tag);
                
                aldeanoforaflut.aldeanoforaflut.network.ModMessages.sendToPlayer(
                    new aldeanoforaflut.aldeanoforaflut.network.OpenTradeConfigPacket(tag),
                    (ServerPlayer) player
                );
            } else {
                player.sendSystemMessage(Component.literal("§cSolo administradores pueden usar la Tablet Phora."));
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
