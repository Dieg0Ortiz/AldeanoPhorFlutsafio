package aldeanoforaflut.aldeanoforaflut.trade;

import aldeanoforaflut.aldeanoforaflut.item.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

public final class DilitioTrades {

    private DilitioTrades() {}

    /** Reinicia los usos de las ofertas agotadas o usadas. Devuelve true si alguna cambió. */
    public static boolean restock(MerchantOffers offers) {
        boolean restocked = false;
        if (offers == null) return false;
        for (MerchantOffer offer : offers) {
            if (offer.getUses() > 0) {
                offer.resetUses();
                restocked = true;
            }
        }
        return restocked;
    }

    public static void addTradesForLevel(MerchantOffers offers, RandomSource random, int tradeLevel, int currentVillagerLevel, net.minecraft.server.level.ServerLevel serverLevel) {
        TradeConfigData config = TradeConfigData.get(serverLevel);
        List<TradeConfigData.TradeEntry> availableTrades = config.getEntriesForLevel(tradeLevel);

        // Pick 2 random unique trades based on weights
        if (!availableTrades.isEmpty()) {
            int tradesToAdd = Math.min(2, availableTrades.size());
            for (int i = 0; i < tradesToAdd; i++) {
                if (availableTrades.isEmpty()) break;
                
                // Calculate total weight
                int totalWeight = availableTrades.stream().mapToInt(e -> e.weight).sum();
                if (totalWeight <= 0) break;

                int randomWeight = random.nextInt(totalWeight);
                int currentWeight = 0;
                TradeConfigData.TradeEntry chosen = null;
                for (TradeConfigData.TradeEntry entry : availableTrades) {
                    currentWeight += entry.weight;
                    if (randomWeight < currentWeight) {
                        chosen = entry;
                        break;
                    }
                }
                
                if (chosen != null) {
                    availableTrades.remove(chosen); // Prevent duplicate trade in the same refresh

                    // Determine random count
                    int count = chosen.minCount;
                    if (chosen.maxCount > chosen.minCount) {
                        count = chosen.minCount + random.nextInt((chosen.maxCount - chosen.minCount) + 1);
                    }
                    
                    int xpReward = getXpReward(tradeLevel);
                    
                    ItemStack resultDilitio = new ItemStack(ModItems.DILITIO.get(), 1);
                    resultDilitio.getOrCreateTag().putInt("RequiredLevel", tradeLevel);
                    
                    Item item = chosen.getItem();
                    if (item == null || item == Items.AIR) item = Items.DIRT; // Fallback
                    
                    MerchantOffer newOffer = new MerchantOffer(
                            new ItemStack(item, count),
                            resultDilitio,
                            12, xpReward, 0.05f
                    );
                    
                    // Si el nivel requerido es mayor al actual, se marca como "Agotado" para que no se pueda tradear
                    if (tradeLevel > currentVillagerLevel) {
                        // Forzamos los usos al maximo para bloquearlo
                        for (int j = 0; j < 12; j++) {
                            newOffer.increaseUses();
                        }
                    }
                    
                    offers.add(newOffer);
                }
            }
        }
    }

    private static int getXpReward(int level) {
        return switch (level) {
            case 1 -> 2;
            case 2 -> 10;
            case 3 -> 20;
            case 4 -> 30;
            case 5 -> 0; // Max level
            default -> 2;
        };
    }

    private static class ItemTrade {
        public final ItemLike item;
        public final int min;
        public final int max;

        public ItemTrade(ItemLike item, int min, int max) {
            this.item = item;
            this.min = min;
            this.max = max;
        }
    }
}
