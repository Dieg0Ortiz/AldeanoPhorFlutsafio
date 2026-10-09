package aldeanoforaflut.aldeanoforaflut.network;

import aldeanoforaflut.aldeanoforaflut.Aldeanoforaflut;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Aldeanoforaflut.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(PhoraCycleAccessPacket.class, id())
                .decoder(PhoraCycleAccessPacket::new)
                .encoder(PhoraCycleAccessPacket::toBytes)
                .consumerMainThread(PhoraCycleAccessPacket::handle)
                .add();

        net.messageBuilder(OpenTradeConfigPacket.class, id())
                .decoder(OpenTradeConfigPacket::new)
                .encoder(OpenTradeConfigPacket::toBytes)
                .consumerMainThread(OpenTradeConfigPacket::handle)
                .add();

        net.messageBuilder(SaveTradeConfigPacket.class, id())
                .decoder(SaveTradeConfigPacket::new)
                .encoder(SaveTradeConfigPacket::toBytes)
                .consumerMainThread(SaveTradeConfigPacket::handle)
                .add();

        net.messageBuilder(PhoraTradeInfoPacket.class, id())
                .decoder(PhoraTradeInfoPacket::new)
                .encoder(PhoraTradeInfoPacket::toBytes)
                .consumerMainThread(PhoraTradeInfoPacket::handle)
                .add();

        net.messageBuilder(PhoraResetPacket.class, id())
                .decoder(PhoraResetPacket::new)
                .encoder(PhoraResetPacket::toBytes)
                .consumerMainThread(PhoraResetPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, net.minecraft.server.level.ServerPlayer player) {
        INSTANCE.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player), message);
    }
}
