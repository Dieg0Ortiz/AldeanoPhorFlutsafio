package aldeanoforaflut.aldeanoforaflut.entity.client;

import aldeanoforaflut.aldeanoforaflut.network.PhoraTradeInfoPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;

public class ClientPacketHandler {
    // Último paquete recibido; la pantalla lo toma si coincide su containerId
    private static PhoraTradeInfoPacket lastTradeInfo;

    public static void handleOpenTradeConfig(CompoundTag configData) {
        Minecraft.getInstance().setScreen(new TradeConfigScreen(configData));
    }

    public static void handlePhoraTradeInfo(PhoraTradeInfoPacket packet) {
        lastTradeInfo = packet;
    }

    public static PhoraTradeInfoPacket getTradeInfo(int containerId) {
        return lastTradeInfo != null && lastTradeInfo.containerId() == containerId ? lastTradeInfo : null;
    }
}
