package aldeanoforaflut.aldeanoforaflut;

import aldeanoforaflut.aldeanoforaflut.entity.ModEntities;
import aldeanoforaflut.aldeanoforaflut.entity.TransformableMerchantEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class TransformableMerchantBlock extends Block {

    public TransformableMerchantBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.getItemInHand(hand).is(Items.STICK)) {
            if (!level.isClientSide) {
                // Eliminar el bloque
                level.removeBlock(pos, false);
                
                // Generar el aldeano en el centro del bloque
                TransformableMerchantEntity merchant = ModEntities.TRANSFORMABLE_MERCHANT.get().create(level);
                if (merchant != null) {
                    merchant.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
                    level.addFreshEntity(merchant);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}
