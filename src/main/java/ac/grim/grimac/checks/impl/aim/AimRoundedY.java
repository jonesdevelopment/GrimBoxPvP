package ac.grim.grimac.checks.impl.aim;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.RotationCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.RotationUpdate;

@CheckData(name = "AimRoundedY", decay = 0.01)
public class AimRoundedY extends Check implements RotationCheck {
    public AimRoundedY(GrimPlayer playerData) {
        super(playerData);
    }

    private float lastRoundedPitch;

    @Override
    public void process(final RotationUpdate rotationUpdate) {
        if (player.packetStateData.lastPacketWasTeleport || player.packetStateData.lastPacketWasOnePointSeventeenDuplicate) return;

        final float deltaPitch = Math.abs(rotationUpdate.getDeltaYRot());

        if (deltaPitch > 1.5f && Math.round(deltaPitch) == deltaPitch && deltaPitch % 1.5f != 0) {
            if (deltaPitch == lastRoundedPitch) {
                alert("deltaPitch=" + deltaPitch);
            } else {
                reward();
            }

            lastRoundedPitch = Math.round(deltaPitch);
        } else {
            lastRoundedPitch = 0;
            reward();
        }
    }
}
