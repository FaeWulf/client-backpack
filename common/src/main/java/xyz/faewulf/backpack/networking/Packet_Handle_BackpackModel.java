package xyz.faewulf.backpack.networking;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import xyz.faewulf.backpack.Constants;

public class Packet_Handle_BackpackModel {
    public static final ResourceLocation CHANNEL = ResourceLocation.tryBuild(Constants.MOD_ID, "packet_backpack_model");
    public static final StreamCodec<FriendlyByteBuf, Packet_Handle_BackpackModel> STREAM_CODEC = StreamCodec.ofMember(Packet_Handle_BackpackModel::encode, Packet_Handle_BackpackModel::decode);

    private String name;
    private String model;
    private String variant;

    public Packet_Handle_BackpackModel(String name, String model, String variant) {
        this.name = name;
        this.model = model;
        this.variant = variant;
    }

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(CHANNEL);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeUtf(model);
        buf.writeUtf(variant);
    }

    public static Packet_Handle_BackpackModel decode(FriendlyByteBuf buf) {
        return new Packet_Handle_BackpackModel(buf.readUtf(), buf.readUtf(), buf.readUtf());
    }

    public static void handle(PacketContext<Packet_Handle_BackpackModel> ctx) {
        if (Side.CLIENT.equals(ctx.side())) {
            System.out.println("client received message");
        } else {
            ctx.sender().sendSystemMessage(Component.literal("Packet1 received on the server"));
            System.out.println("Server: " + ctx.message().model);
        }
    }
}
