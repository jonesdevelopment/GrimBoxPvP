package ac.grim.grimac.checks.impl.movement;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PostPredictionCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
import ac.grim.grimac.utils.enums.FluidTag;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;

@CheckData(name = "NoSlowC", description = "Sprinting while sneaking", setback = 5, experimental = true)
public class SprintB extends Check implements PostPredictionCheck {
    public SprintB(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPredictionComplete(final PredictionComplete predictionComplete) {
        if (!predictionComplete.isChecked()) return;

        if (player.isSlowMovement && player.sneakingSpeedMultiplier < 0.8f) {
            ClientVersion client = player.getClientVersion();

            // https://bugs.mojang.com/browse/MC-152728
            if (client.isNewerThanOrEquals(ClientVersion.V_1_14_2) && client.isOlderThan(ClientVersion.V_1_21_4)) {
                return;
            }

            if (player.isSprinting && !player.isSwimming && (player.fluidOnEyes != FluidTag.WATER || client.isOlderThan(ClientVersion.V_1_21_4))) {
                if (flagWithSetback()) alert("");
            } else reward();
        }
    }
}
