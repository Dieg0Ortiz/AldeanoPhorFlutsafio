package aldeanoforaflut.aldeanoforaflut.entity.client;

import aldeanoforaflut.aldeanoforaflut.Aldeanoforaflut;
import aldeanoforaflut.aldeanoforaflut.entity.TransformableMerchantEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PhoraModel extends GeoModel<TransformableMerchantEntity> {
    @Override
    public ResourceLocation getModelResource(TransformableMerchantEntity object) {
        return new ResourceLocation(Aldeanoforaflut.MODID, "geo/phora.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TransformableMerchantEntity object) {
        if (object.isActive()) {
            return new ResourceLocation(Aldeanoforaflut.MODID, "textures/entity/phora_on.png");
        }
        return new ResourceLocation(Aldeanoforaflut.MODID, "textures/entity/phora_off.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TransformableMerchantEntity animatable) {
        return new ResourceLocation(Aldeanoforaflut.MODID, "animations/phora.animation.json");
    }
}
