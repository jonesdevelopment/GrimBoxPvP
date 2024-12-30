package ac.grim.grimac.utils.anticheat.update;

import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.util.Vector3i;
import lombok.Getter;

public class BlockBreak {
    public final Vector3i position;
    public final BlockFace face;
    public final int faceId;
    public final DiggingAction action;
    @Getter
    private boolean cancelled;

    public final WrappedBlockState block;

    public BlockBreak(Vector3i position, BlockFace face, int faceId, DiggingAction action, WrappedBlockState block) {
        this.position = position;
        this.face = face;
        this.faceId = faceId;
        this.action = action;
        this.block = block;
    }

    public void cancel() {
        this.cancelled = true;
    }
}
