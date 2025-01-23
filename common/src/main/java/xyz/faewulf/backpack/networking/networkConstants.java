package xyz.faewulf.backpack.networking;

import commonnetwork.api.Network;

public class networkConstants {
    public static void init() {
        Network
                .registerPacket(Packet_Handle_BackpackModel.type(), Packet_Handle_BackpackModel.class, Packet_Handle_BackpackModel.STREAM_CODEC, Packet_Handle_BackpackModel::handle)
                .registerPacket(Packet_Handle_BackpackData.type(), Packet_Handle_BackpackData.class, Packet_Handle_BackpackData.STREAM_CODEC, Packet_Handle_BackpackData::handle);
    }
}
