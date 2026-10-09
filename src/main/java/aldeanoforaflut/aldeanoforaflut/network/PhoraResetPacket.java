package aldeanoforaflut.aldeanoforaflut.network;

import aldeanoforaflut.aldeanoforaflut.entity.TransformableMerchantEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Cliente -> servidor: botón de reinicio del GUI. Apaga el Phora y lo vuelve bloque. */
public class PhoraResetPacket {
    // Misma distancia máxima que usa vanilla para interactuar con un comerciante
    private static final double MAX_DISTANCE_SQR = 8.0 * 8.0;

    private final int entityId;

    public PhoraResetPacket(int entityId) {
        this.entityId = entityId;
    }

    public PhoraResetPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            Entity entity = player.level().getEntity(entityId);
            if (entity instanceof TransformableMerchantEntity phora
                    && phora.isActive()
                    && player.distanceToSqr(phora) <= MAX_DISTANCE_SQR
                    && (phora.isOwner(player) || player.hasPermissions(2))) {
                phora.resetToBlock();
            }
        });
        return true;
    }
}
