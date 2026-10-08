package aldeanoforaflut.aldeanoforaflut;

import aldeanoforaflut.aldeanoforaflut.entity.TransformableMerchantEntity;
import aldeanoforaflut.aldeanoforaflut.entity.client.PhoraMerchantScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Aldeanoforaflut.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModClientForgeEvents {
    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof MerchantScreen merchantScreen) {
            if (merchantScreen.getTitle().getString().contains("Aldeano Phora")) {
                Player player = Minecraft.getInstance().player;
                PhoraMerchantScreen customScreen = new PhoraMerchantScreen(
                        merchantScreen.getMenu(),
                        player.getInventory(),
                        merchantScreen.getTitle()
                );

                // Try to find the nearby TransformableMerchantEntity to get access info
                if (player != null) {
                    AABB searchBox = player.getBoundingBox().inflate(10.0);
                    List<Entity> entities = player.level().getEntities(player, searchBox,
                            e -> e instanceof TransformableMerchantEntity);
                    for (Entity e : entities) {
                        TransformableMerchantEntity phora = (TransformableMerchantEntity) e;
                        if (phora.getTradingPlayer() == player || phora.getCustomName() != null &&
                                phora.getCustomName().getString().equals(merchantScreen.getTitle().getString())) {
                            customScreen.setAccessInfo(phora.getAccessModeText(), phora.isOwner(player));
                            break;
                        }
                    }
                }

                event.setNewScreen(customScreen);
            }
        }
    }
}
