package ac.grim.grimac.checks.impl.inventory;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.InventoryCheck;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.BlockPlace;

@CheckData(name = "InventoryC")
public class InventoryC extends InventoryCheck implements PacketCheck {
    public InventoryC(final GrimPlayer player) {
        super(player);
    }

    @Override
    public void onBlockPlace(final BlockPlace place) {
        if (hasInventoryOpen) {
            if (flagAndAlert()) {
                if (shouldModifyPackets()) {
                    place.resync();
                }
                closeInventory();
            }
        } else {
            reward();
        }
    }
}
