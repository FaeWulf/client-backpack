package xyz.faewulf.backpack.networking;

import commonnetwork.api.Network;

public class networkConstants {
    public static void init() {
        Network
                .registerPacket(Packet_Handle_BackpackModel.CHANNEL, Packet_Handle_BackpackModel.class, Packet_Handle_BackpackModel::encode,Packet_Handle_BackpackModel::decode, Packet_Handle_BackpackModel::handle)
                .registerPacket(Packet_Handle_BackpackData.CHANNEL, Packet_Handle_BackpackData.class, Packet_Handle_BackpackData::encode, Packet_Handle_BackpackData::decode, Packet_Handle_BackpackData::handle);
    }
}
