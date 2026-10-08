package aldeanoforaflut.aldeanoforaflut.trade;

import aldeanoforaflut.aldeanoforaflut.item.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.function.Function;

/**
 * Ofertas de los aldeanos transformables que usan Dilitio como moneda.
 * Para agregar un trade nuevo, añade una línea a {@link #TRADES}.
 */
public final class DilitioTrades {

    // Palos -> Dilitio (el rango debe caber en un solo stack de 64)
    public static final int STICKS_MIN = 55;
    public static final int STICKS_MAX = 62;
    public static final int STICKS_MAX_USES = 12;

    // Precios en Dilitio
    public static final int DIAMOND_PRICE = 2;
    public static final int NETHERITE_PRICE = 8;
    public static final int BREAD_PRICE = 1;
    public static final int BREAD_AMOUNT = 16;

    // Valores por defecto de las compras con Dilitio
    public static final int DEFAULT_MAX_USES = 10;
    public static final int DEFAULT_XP = 5;
    public static final float DEFAULT_PRICE_MULTIPLIER = 0.05f;

    private static final List<Function<RandomSource, MerchantOffer>> TRADES = List.of(
            DilitioTrades::sticksForDilitio,
            random -> buy(Items.DIAMOND, 1, DIAMOND_PRICE),
            random -> buy(Items.NETHERITE_INGOT, 1, NETHERITE_PRICE),
            random -> buy(Items.BREAD, BREAD_AMOUNT, BREAD_PRICE)
    );

    private DilitioTrades() {
    }

    /** Agrega todas las ofertas de Dilitio a la lista del comerciante. */
    public static void addAll(MerchantOffers offers, RandomSource random) {
        for (Function<RandomSource, MerchantOffer> trade : TRADES) {
            offers.add(trade.apply(random));
        }
    }

    /** Entre {@value #STICKS_MIN} y {@value #STICKS_MAX} palos por 1 Dilitio; la cantidad se fija al crear la oferta. */
    public static MerchantOffer sticksForDilitio(RandomSource random) {
        int sticks = random.nextIntBetweenInclusive(STICKS_MIN, STICKS_MAX);
        return new MerchantOffer(
                new ItemStack(Items.STICK, sticks),
                new ItemStack(ModItems.DILITIO.get(), 1),
                STICKS_MAX_USES, 2, 0.0f
        );
    }

    /** Oferta para comprar {@code count} de {@code item} pagando {@code price} Dilitio. */
    public static MerchantOffer buy(ItemLike item, int count, int price) {
        return buy(item, count, price, DEFAULT_MAX_USES);
    }

    public static MerchantOffer buy(ItemLike item, int count, int price, int maxUses) {
        return new MerchantOffer(
                new ItemStack(ModItems.DILITIO.get(), price),
                new ItemStack(item, count),
                maxUses, DEFAULT_XP, DEFAULT_PRICE_MULTIPLIER
        );
    }
}
