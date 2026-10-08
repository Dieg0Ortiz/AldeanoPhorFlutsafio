package aldeanoforaflut.aldeanoforaflut.network;

import aldeanoforaflut.aldeanoforaflut.entity.TransformableMerchantEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PhoraCycleAccessPacket {
    private final int entityId;

    public PhoraCycleAccessPacket(int entityId) {
        this.entityId = entityId;
    }

    public PhoraCycleAccessPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                Entity entity = player.level().getEntity(entityId);
                if (entity instanceof TransformableMerchantEntity phora) {
                    if (phora.isOwner(player)) {
                        phora.cycleAccessMode();
                    }
                }
            }
        });
        return true;
    }
}
