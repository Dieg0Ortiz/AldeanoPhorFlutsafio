package aldeanoforaflut.aldeanoforaflut.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;

public class TransformableMerchantRenderer extends VillagerRenderer {

    public TransformableMerchantRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(net.minecraft.world.entity.npc.Villager entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity instanceof TransformableMerchantEntity merchant && merchant.isBlockForm()) {
            poseStack.pushPose();
            // Centramos el renderizado del bloque para que coincida con la hitbox de la entidad
            poseStack.translate(-0.5D, 0.0D, -0.5D);
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                    Blocks.IRON_BLOCK.defaultBlockState(),
                    poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, net.minecraftforge.client.model.data.ModelData.EMPTY, null
            );
            poseStack.popPose();
        } else {
            // Si ya no es un bloque, renderiza como aldeano normal
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }
    }
}
