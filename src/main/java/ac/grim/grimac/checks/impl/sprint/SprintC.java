package ac.grim.grimac.checks.impl.sprint;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PostPredictionCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;

@CheckData(name = "SprintC", description = "Sprinting while using an item", setback = 5, experimental = true)
public class SprintC extends Check implements PostPredictionCheck {
    public SprintC(GrimPlayer player) {
        super(player);
    }

    private boolean flaggedLastTick = false;

    @Override
    public void onPredictionComplete(final PredictionComplete predictionComplete) {
        if (!predictionComplete.isChecked()) return;

        if (player.packetStateData.isSlowedByUsingItem()) {
            ClientVersion client = player.getClientVersion();

            // https://bugs.mojang.com/browse/MC-152728
            if (client.isNewerThanOrEquals(ClientVersion.V_1_14_2) && client.isOlderThan(ClientVersion.V_1_21_4)) {
                return;
            }

            if (player.isSprinting && (!player.isSwimming || client.isOlderThan(ClientVersion.V_1_21_4))) {
                if (flaggedLastTick && flagWithSetback()) alert("");
                flaggedLastTick = true;
            } else {
                reward();
                flaggedLastTick = false;
            }
        }
    }
}
