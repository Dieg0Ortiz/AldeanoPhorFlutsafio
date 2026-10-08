package aldeanoforaflut.aldeanoforaflut.entity.client;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;

public class ClientPacketHandler {
    public static void handleOpenTradeConfig(CompoundTag configData) {
        Minecraft.getInstance().setScreen(new TradeConfigScreen(configData));
    }
}
