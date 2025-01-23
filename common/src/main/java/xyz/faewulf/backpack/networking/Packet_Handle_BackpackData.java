package xyz.faewulf.backpack.networking;

import commonnetwork.api.Dispatcher;
import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import xyz.faewulf.backpack.Constants;
import xyz.faewulf.backpack.inter.BackpackStatus;
import xyz.faewulf.backpack.util.converter;

import java.util.ArrayList;
import java.util.List;

public class Packet_Handle_BackpackData {
    public static final ResourceLocation CHANNEL = ResourceLocation.tryBuild(Constants.MOD_ID, "packet_backpack_status");
    public static final StreamCodec<FriendlyByteBuf, Packet_Handle_BackpackData> STREAM_CODEC = StreamCodec.ofMember(Packet_Handle_BackpackData::encode, Packet_Handle_BackpackData::decode);

    private String name;
    private BackpackStatus backpackStatus;

    public Packet_Handle_BackpackData(String name, BackpackStatus backpackStatus) {
        this.name = name;
        this.backpackStatus = backpackStatus;
    }

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(CHANNEL);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(name);

        // Encode booleans
        buf.writeBoolean(this.backpackStatus.invChanged);
        buf.writeBoolean(this.backpackStatus.hasLightSource);

        // Encode int
        buf.writeInt(this.backpackStatus.holdingSlot);

        // Encode lists of ItemStacks
        writeItemList(buf, this.backpackStatus.toolsList);
        writeItemList(buf, this.backpackStatus.liquidList);
        writeItemList(buf, this.backpackStatus.containerList);
        writeItemList(buf, this.backpackStatus.pocketList);

        // Encode banner ItemStack (can be null)
        if (this.backpackStatus.banner != null) {
            buf.writeBoolean(true); // indicates the ItemStack is not null
            ItemStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf) buf, this.backpackStatus.banner); // Encoding ItemStack
        } else {
            buf.writeBoolean(false); // indicates the ItemStack is null
        }
    }

    public static Packet_Handle_BackpackData decode(FriendlyByteBuf buf) {
        BackpackStatus status = new BackpackStatus();

        // Decode Strings
        String name = buf.readUtf();

        // Decode booleans
        status.invChanged = buf.readBoolean();
        status.hasLightSource = buf.readBoolean();

        // Decode int
        status.holdingSlot = buf.readInt();

        // Decode lists of ItemStacks
        status.toolsList = readItemList(buf);
        status.liquidList = readItemList(buf);
        status.containerList = readItemList(buf);
        status.pocketList = readItemList(buf);


        // Decode banner ItemStack (can be null)
        if (buf.readBoolean()) {
            status.banner = ItemStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf);
        } else {
            status.banner = null;
        }

        return new Packet_Handle_BackpackData(name, status);
    }

    public static void handle(PacketContext<Packet_Handle_BackpackData> ctx) {
        if (Side.CLIENT.equals(ctx.side())) {
            // Client side
            String name = ctx.message().name;
            BackpackStatus backpackStatus1 = ctx.message().backpackStatus;
            Constants.PLAYER_INV_STATUS.computeIfPresent(name, (k, v) -> {
                v = backpackStatus1;
                return v;
            });
        } else {
            // Server side
            //ctx.sender().sendSystemMessage(Component.literal("Packet1 received on the server"));

            // get inv status from requested client
            String name = ctx.message().name;
            BackpackStatus backpackStatus = Constants.SERVER_PLAYER_INV_STATUS.get(name);

            //if changed then recalculate
            if (backpackStatus != null && backpackStatus.invChanged)
                converter.updateBackpackStatus(backpackStatus, name, true);

            // Send data to requester
            Dispatcher.sendToClient(new Packet_Handle_BackpackData(name, backpackStatus), ctx.sender());
        }
    }

    // Helper method to encode lists of ItemStacks
    private void writeItemList(FriendlyByteBuf buf, List<ItemStack> list) {
        buf.writeInt(list.size());
        for (ItemStack item : list) {
            if (item != null) {
                buf.writeBoolean(true); // ItemStack is not null
                ItemStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf) buf, item); // Encode ItemStack
            } else {
                buf.writeBoolean(false); // ItemStack is null
            }
        }
    }

    // Helper method to decode lists of ItemStacks
    private static List<ItemStack> readItemList(FriendlyByteBuf buf) {
        int size = buf.readInt();
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (buf.readBoolean()) {
                list.add(ItemStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf));
            }
        }
        return list;
    }
}
