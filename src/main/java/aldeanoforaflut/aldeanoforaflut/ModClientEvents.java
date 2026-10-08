package aldeanoforaflut.aldeanoforaflut;

import aldeanoforaflut.aldeanoforaflut.entity.ModEntities;
import aldeanoforaflut.aldeanoforaflut.entity.TransformableMerchantRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Aldeanoforaflut.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.TRANSFORMABLE_MERCHANT.get(), TransformableMerchantRenderer::new);
    }
}
