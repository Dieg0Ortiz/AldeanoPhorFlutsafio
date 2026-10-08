package aldeanoforaflut.aldeanoforaflut;

import aldeanoforaflut.aldeanoforaflut.entity.client.PhoraMerchantScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Aldeanoforaflut.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModClientForgeEvents {
    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof MerchantScreen merchantScreen) {
            if (merchantScreen.getTitle().getString().contains("Aldeano Phora")) {
                event.setNewScreen(new PhoraMerchantScreen(merchantScreen.getMenu(), net.minecraft.client.Minecraft.getInstance().player.getInventory(), merchantScreen.getTitle()));
            }
        }
    }
}
