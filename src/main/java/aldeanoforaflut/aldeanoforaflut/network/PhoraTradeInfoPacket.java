package aldeanoforaflut.aldeanoforaflut.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Servidor -> cliente: datos extra de la pantalla de comercio Phora
 * (nivel requerido por oferta, barra pasiva y acceso), enviados al abrir el menú.
 */
public record PhoraTradeInfoPacket(int containerId, int entityId, long passiveTicks,
                                   int accessMode, boolean isOwner, int[] offerLevels) {

    public PhoraTradeInfoPacket(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt(), buf.readVarLong(),
                buf.readVarInt(), buf.readBoolean(), buf.readVarIntArray());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(containerId);
        buf.writeVarInt(entityId);
        buf.writeVarLong(passiveTicks);
        buf.writeVarInt(accessMode);
        buf.writeBoolean(isOwner);
        buf.writeVarIntArray(offerLevels);
    }

    public int getOfferLevel(int index) {
        return index >= 0 && index < offerLevels.length ? offerLevels[index] : 0;
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> aldeanoforaflut.aldeanoforaflut.entity.client.ClientPacketHandler.handlePhoraTradeInfo(this)));
        return true;
    }
}
