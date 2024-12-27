package ac.grim.grimac.checks.impl.inventory;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;

@CheckData(name = "InventoryD")
public class InventoryD extends Check implements PacketCheck {
    public InventoryD(final GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Client.PLAYER_DIGGING)
                && new WrapperPlayClientPlayerDigging(event).getAction() == DiggingAction.START_DIGGING) {
            handleAction(event);
        } else if (event.getPacketType().equals(PacketType.Play.Client.ENTITY_ACTION)) {
            final WrapperPlayClientEntityAction entityAction = new WrapperPlayClientEntityAction(event);
            if (entityAction.getAction() == WrapperPlayClientEntityAction.Action.START_FLYING_WITH_ELYTRA
                    || entityAction.getAction() == WrapperPlayClientEntityAction.Action.START_JUMPING_WITH_HORSE
                    || entityAction.getAction() == WrapperPlayClientEntityAction.Action.START_SNEAKING
                    || entityAction.getAction() == WrapperPlayClientEntityAction.Action.START_SPRINTING) {
                handleAction(event);
            }
        } else if (event.getPacketType().equals(PacketType.Play.Client.INTERACT_ENTITY)) {
            handleAction(event);
        }
    }

    private void handleAction(PacketReceiveEvent event) {
        if (player.hasInventoryOpen && flagAndAlert("type=" + event.getPacketType())) {
            if (shouldModifyPackets()) {
                event.setCancelled(true);
                player.onPacketCancel();
            }
            player.closeInventory();
        }
    }
}
