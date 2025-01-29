package ac.grim.grimac.checks.impl.inventory;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClientStatus;

@CheckData(name = "InventoryE")
public class InventoryE extends Check implements PacketCheck {
    public InventoryE(final GrimPlayer player) {
        super(player);
    }

    private boolean exempt;

    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Client.PLAYER_ROTATION)
                || event.getPacketType().equals(PacketType.Play.Server.PLAYER_POSITION_AND_LOOK)) {
            if (!player.hasInventoryOpen) {
                return;
            }
            if (exempt) {
                exempt = false;
                return;
            }
            if (!player.skippedTickInActualMovement
                    && !player.packetStateData.lastPacketWasTeleport
                    && !player.packetStateData.lastPacketWasOnePointSeventeenDuplicate
                    && flagAndAlert()) {
                player.closeInventory();
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CLIENT_STATUS) {
            WrapperPlayClientClientStatus wrapper = new WrapperPlayClientClientStatus(event);
            if (wrapper.getAction() == WrapperPlayClientClientStatus.Action.OPEN_INVENTORY_ACHIEVEMENT) {
                exempt = true;
            }
        }
    }
}
