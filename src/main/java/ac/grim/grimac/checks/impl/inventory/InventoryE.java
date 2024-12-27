package ac.grim.grimac.checks.impl.inventory;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.InventoryCheck;
import ac.grim.grimac.checks.type.RotationCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.RotationUpdate;

@CheckData(name = "InventoryE")
public class InventoryE extends InventoryCheck implements RotationCheck {
    public InventoryE(final GrimPlayer player) {
        super(player);
    }

    @Override
    public void process(final RotationUpdate rotationUpdate) {
        if (hasInventoryOpen
                && !player.skippedTickInActualMovement
                && !player.packetStateData.lastPacketWasTeleport
                && !player.packetStateData.lastPacketWasOnePointSeventeenDuplicate
                && flagAndAlert()) {
            closeInventory();
        }
    }
}
