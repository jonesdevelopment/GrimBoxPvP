package ac.grim.grimac.checks.impl.aim;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.RotationCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.RotationUpdate;

@CheckData(name = "AimRoundedX", decay = 0.01)
public class AimRoundedX extends Check implements RotationCheck {
    public AimRoundedX(GrimPlayer playerData) {
        super(playerData);
    }

    private float lastRoundedYaw;

    @Override
    public void process(final RotationUpdate rotationUpdate) {
        if (player.packetStateData.lastPacketWasTeleport || player.packetStateData.lastPacketWasOnePointSeventeenDuplicate) return;

        final float deltaYaw = Math.abs(rotationUpdate.getDeltaXRot()) % 360f;

        if (deltaYaw > 1.5f && Math.round(deltaYaw) == deltaYaw && deltaYaw % 1.5f != 0) {
            if (deltaYaw == lastRoundedYaw) {
                flagAndAlert("deltaYaw=" + deltaYaw);
            }

            lastRoundedYaw = Math.round(deltaYaw);
        } else {
            lastRoundedYaw = 0;
            reward();
        }
    }
}
