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
import xyz.faewulf.backpack.util.Converter;
import xyz.faewulf.backpack.util.config.ModConfigs;

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
        buf.writeBoolean(this.backpackStatus.isWearingBackpack());

        buf.writeBoolean(this.backpackStatus.isInvChanged());
        buf.writeBoolean(this.backpackStatus.isHasLightSource());

        // Encode int
        buf.writeInt(this.backpackStatus.getHoldingSlot());

        // Encode lists of ItemStacks
        writeItemList(buf, this.backpackStatus.getToolsList());
        writeItemList(buf, this.backpackStatus.getLiquidList());
        writeItemList(buf, this.backpackStatus.getContainerList());
        writeItemList(buf, this.backpackStatus.getPocketList());

        // Encode banner ItemStack (can be null)
        if (this.backpackStatus.getBanner() != null) {
            buf.writeBoolean(true); // indicates the ItemStack is not null
            ItemStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf) buf, this.backpackStatus.getBanner()); // Encoding ItemStack
        } else {
            buf.writeBoolean(false); // indicates the ItemStack is null
        }
    }

    public static Packet_Handle_BackpackData decode(FriendlyByteBuf buf) {
        BackpackStatus status = new BackpackStatus();

        // Decode Strings
        String name = buf.readUtf();

        // Decode booleans
        status.setWearingBackpack(buf.readBoolean());
        status.setInvChanged(buf.readBoolean());
        status.setHasLightSource(buf.readBoolean());

        // Decode int
        status.setHoldingSlot(buf.readInt());

        // Decode lists of ItemStacks
        status.setToolsList(readItemList(buf));
        status.setLiquidList(readItemList(buf));
        status.setContainerList(readItemList(buf));
        status.setPocketList(readItemList(buf));


        // Decode banner ItemStack (can be null)
        if (buf.readBoolean()) {
            status.setBanner(ItemStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf));
        } else {
            status.setBanner(null);
        }

        return new Packet_Handle_BackpackData(name, status);
    }

    public static void handle(PacketContext<Packet_Handle_BackpackData> ctx) {
        if (Side.CLIENT.equals(ctx.side())) {
            // Client side
            String name = ctx.message().name;

            if (ctx.message().backpackStatus == null)
                return;

            BackpackStatus backpackStatus1 = ctx.message().backpackStatus;

            Constants.PLAYER_INV_STATUS.computeIfPresent(name, (k, v) -> {
                v.updateInvData(backpackStatus1);
                return v;
            });
        } else {
            // Server side
            //ctx.sender().sendSystemMessage(Component.literal("Packet1 received on the server"));


            // get inv status from requested client
            String name = ctx.message().name;
            BackpackStatus backpackStatus = Constants.SERVER_PLAYER_INV_STATUS.get(name);

            if (backpackStatus == null) {
                backpackStatus = new BackpackStatus();
            }

            //if changed then recalculate
            if (backpackStatus.isInvChanged())
                Converter.updateBackpackStatus(backpackStatus, name, true);

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

                if (ModConfigs.hide_items_component_data) {
                    ItemStack defaultItem = new ItemStack(item.getItem());
                    ItemStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf) buf, defaultItem); // Encode ItemStack
                } else
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
