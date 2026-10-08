package aldeanoforaflut.aldeanoforaflut.trade;

import aldeanoforaflut.aldeanoforaflut.item.ModItems;
import net.minecraft.util.RandomSource;
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

    public static void addTradesForLevel(MerchantOffers offers, RandomSource random, int level) {
        List<ItemTrade> availableTrades = new ArrayList<>();

        switch (level) {
            case 1:
                availableTrades.add(new ItemTrade(Items.STICK, 32, 64));
                availableTrades.add(new ItemTrade(Items.DIRT, 64, 64));
                availableTrades.add(new ItemTrade(Items.COBBLESTONE, 64, 64));
                availableTrades.add(new ItemTrade(Items.WHEAT, 20, 30));
                availableTrades.add(new ItemTrade(Items.SAND, 64, 64));
                break;
            case 2:
                availableTrades.add(new ItemTrade(Items.COAL, 10, 20));
                availableTrades.add(new ItemTrade(Items.BAKED_POTATO, 10, 20));
                availableTrades.add(new ItemTrade(Items.APPLE, 5, 10));
                availableTrades.add(new ItemTrade(Items.STRING, 15, 30));
                availableTrades.add(new ItemTrade(Items.ROTTEN_FLESH, 30, 40));
                availableTrades.add(new ItemTrade(Items.BEETROOT, 10, 20));
                break;
            case 3:
                availableTrades.add(new ItemTrade(Items.IRON_INGOT, 5, 10));
                availableTrades.add(new ItemTrade(Items.GOLD_INGOT, 5, 10));
                availableTrades.add(new ItemTrade(Items.BAMBOO, 30, 64));
                availableTrades.add(new ItemTrade(Items.HONEYCOMB, 5, 10));
                availableTrades.add(new ItemTrade(Items.LAPIS_LAZULI, 10, 20));
                break;
            case 4:
                availableTrades.add(new ItemTrade(Items.DIAMOND, 1, 3));
                availableTrades.add(new ItemTrade(Items.OBSIDIAN, 3, 10));
                availableTrades.add(new ItemTrade(Items.ENDER_PEARL, 2, 5));
                availableTrades.add(new ItemTrade(Items.SLIME_BALL, 5, 10));
                availableTrades.add(new ItemTrade(Items.EMERALD, 3, 6));
                break;
            case 5:
                availableTrades.add(new ItemTrade(Items.NETHERITE_INGOT, 1, 1));
                availableTrades.add(new ItemTrade(Items.GHAST_TEAR, 1, 2));
                availableTrades.add(new ItemTrade(Items.BLAZE_ROD, 5, 10));
                availableTrades.add(new ItemTrade(Items.PHANTOM_MEMBRANE, 5, 10));
                availableTrades.add(new ItemTrade(Items.GOLDEN_APPLE, 1, 2));
                break;
        }

        // Pick 2 random unique trades from the available pool for this level
        if (!availableTrades.isEmpty()) {
            int tradesToAdd = Math.min(2, availableTrades.size());
            for (int i = 0; i < tradesToAdd; i++) {
                int randomIndex = random.nextInt(availableTrades.size());
                ItemTrade chosen = availableTrades.remove(randomIndex);
                
                // Determine random count
                int count = chosen.min;
                if (chosen.max > chosen.min) {
                    count = chosen.min + random.nextInt((chosen.max - chosen.min) + 1);
                }
                
                // Add the offer: Player gives 'count' of 'item', gets 1 Dilitio
                // The xp reward scales with level to allow leveling up
                int xpReward = getXpReward(level);
                offers.add(new MerchantOffer(
                        new ItemStack(chosen.item, count),
                        new ItemStack(ModItems.DILITIO.get(), 1),
                        12, xpReward, 0.05f
                ));
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
