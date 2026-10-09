package aldeanoforaflut.aldeanoforaflut.network;

import aldeanoforaflut.aldeanoforaflut.trade.TradeConfigData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SaveTradeConfigPacket {
    private final CompoundTag configData;

    public SaveTradeConfigPacket(CompoundTag configData) {
        this.configData = configData;
    }

    public SaveTradeConfigPacket(FriendlyByteBuf buf) {
        // readNbt limita el tamaño (2 MB): el paquete llega de un cliente y se decodifica antes de validar permisos
        CompoundTag tag = buf.readNbt();
        this.configData = tag != null ? tag : new CompoundTag();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(configData);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.hasPermissions(2)) {
                TradeConfigData data = TradeConfigData.get((ServerLevel) player.level());
                data.loadFromClient(configData);
            }
        });
        return true;
    }
}
