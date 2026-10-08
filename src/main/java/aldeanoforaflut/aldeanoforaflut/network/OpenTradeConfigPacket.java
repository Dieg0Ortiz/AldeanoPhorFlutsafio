package aldeanoforaflut.aldeanoforaflut.network;

import aldeanoforaflut.aldeanoforaflut.trade.TradeConfigData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenTradeConfigPacket {
    private final CompoundTag configData;

    public OpenTradeConfigPacket(CompoundTag configData) {
        this.configData = configData;
    }

    public OpenTradeConfigPacket(FriendlyByteBuf buf) {
        this.configData = buf.readAnySizeNbt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(configData);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Se ejecuta en el cliente
            aldeanoforaflut.aldeanoforaflut.entity.client.ClientPacketHandler.handleOpenTradeConfig(configData);
        });
        return true;
    }
}
