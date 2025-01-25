package ac.grim.grimac.events.packets;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.data.packetentity.PacketEntitySelf;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClientStatus;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

public class PacketPlayerWindow extends PacketListenerAbstract {

    public PacketPlayerWindow() {
        super(PacketListenerPriority.LOW);
    }

    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
            if (player == null) return;
            if (player.hasInventoryOpen && isNearNetherPortal(player)) {
                player.hasInventoryOpen = false;
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CLIENT_STATUS) {
            WrapperPlayClientClientStatus wrapper = new WrapperPlayClientClientStatus(event);
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
            if (player == null) return;
            if (wrapper.getAction() == WrapperPlayClientClientStatus.Action.OPEN_INVENTORY_ACHIEVEMENT) {
                player.hasInventoryOpen = true;
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
            if (player == null) return;
            // Disallow any clicks if inventory is closing
            if (player.inventoryCloseTransaction != Long.MAX_VALUE) {
                event.setCancelled(true);
                player.onPacketCancel();
                player.getInventory().needResend = true;
            } else {
                player.hasInventoryOpen = true;
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
            if (player == null) return;
            // Players with high ping can close inventory faster than send transaction back
            if (player.inventoryCloseTransaction != Long.MAX_VALUE && player.inventoryClosePacketsToSkip-- <= 0) {
                player.inventoryCloseTransaction = Long.MAX_VALUE;
            } else {
                player.inventoryClosePacketsToSkip = 0;
            }
            player.hasInventoryOpen = false;
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Server.RESPAWN)
                || event.getPacketType().equals(PacketType.Play.Server.OPEN_WINDOW)
                || event.getPacketType().equals(PacketType.Play.Server.CLOSE_WINDOW)) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
            if (player == null) return;
            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.hasInventoryOpen = false);
        }
    }

    private static boolean isNearNetherPortal(GrimPlayer player) {
        // Going inside nether portal with opened inventory cause desync, fixed in 1.12.2
        if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_12_1) &&
                player.pointThreeEstimator.isNearNetherPortal) {
            // Client ignores nether portal if the player has passengers or is riding an entity
            return !player.entities.self.inVehicle() && player.entities.self.passengers.isEmpty();
        }

        return false;
    }
}
