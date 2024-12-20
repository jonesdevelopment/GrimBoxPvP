package ac.grim.grimac.checks.impl.inventory;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.InventoryCheck;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;

@CheckData(name = "InventoryE", setback = 1)
public class InventoryE extends InventoryCheck implements PacketCheck {
    public InventoryE(final GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        super.onPacketReceive(event);

        if (event.getPacketType().equals(PacketType.Play.Client.PLAYER_ROTATION)
            || event.getPacketType().equals(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION)) {
            if (hasInventoryOpen
                    && !player.skippedTickInActualMovement
                    && !player.packetStateData.lastPacketWasTeleport
                    && !player.packetStateData.lastPacketWasOnePointSeventeenDuplicate
                    && flagAndAlert()) {
                closeInventory();
            }
        }
    }
}
