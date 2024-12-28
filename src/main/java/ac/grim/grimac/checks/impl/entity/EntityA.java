package ac.grim.grimac.checks.impl.entity;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import me.tofaa.entitylib.meta.EntityMeta;

import java.util.UUID;

@CheckData(name = "Entity", experimental = true)
public class EntityA extends Check implements PacketCheck {
    public EntityA(GrimPlayer player) {
        super(player);
        uuid = UUID.randomUUID();
    }

    // This can easily be detected, but it's still safer than using a huge number.
    private final int generatedEntityId = -player.entityID;
    private final UUID uuid;
    private int entityId = -1;
    private int packetsSinceAttack;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Client.INTERACT_ENTITY)) {
            final WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);
            if (packet.getAction() != WrapperPlayClientInteractEntity.InteractAction.ATTACK) return;
            if (entityId != -1) {
                if (entityId == packet.getEntityId()) {
                    alert("");
                }
                return;
            }
            packetsSinceAttack = 0;
            entityId = generatedEntityId;
            final EntityMeta meta = EntityMeta.createMeta(entityId, EntityTypes.SKELETON);
            final Vector3d position = new Vector3d(player.x, player.y + 5, player.z);
            final Location location = new Location(position, (int) (player.xRot / 3), (int) (player.yRot / 3));
            meta.setInvisible(true);
            player.user.sendPacket(new WrapperPlayServerSpawnEntity(
                    entityId, uuid, EntityTypes.SKELETON, location, 0, 0, Vector3d.zero()));
            player.user.sendPacket(meta.createPacket());
            damageEntity();
        } else if (event.getPacketType().equals(PacketType.Play.Client.PLAYER_POSITION)
                || event.getPacketType().equals(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION)
                || event.getPacketType().equals(PacketType.Play.Client.PLAYER_ROTATION)) {
            if (entityId == -1) return;
            if (++packetsSinceAttack > 20) {
                player.user.sendPacket(new WrapperPlayServerDestroyEntities(entityId));
                entityId = -1;
                return;
            }
            if (player.packetStateData.lastPacketWasTeleport || player.packetStateData.lastPacketWasOnePointSeventeenDuplicate) return;
            if (packetsSinceAttack % 3 == 0) {
                correctEntityPosition();
            }
            if (packetsSinceAttack % 8 == 0) {
                correctEntityRotation();
            }
        }
    }

    private void correctEntityPosition() {
        final double distance = -1.8;
        final double offsetX = distance * Math.sin(Math.toRadians(-player.xRot));
        final double offsetZ = distance * Math.cos(Math.toRadians(-player.xRot));
        final double entityX = player.x + offsetX;
        final double entityY = player.y + player.getEyeHeight() + 1.25;
        final double entityZ = player.z + offsetZ;
        player.user.sendPacket(new WrapperPlayServerEntityTeleport(entityId,
                new Vector3d(entityX, entityY, entityZ), 0, 0, false));
    }

    private void correctEntityRotation() {
        player.user.sendPacket(new WrapperPlayServerEntityRotation(entityId,
                (int) (player.xRot / 4), (int) (player.yRot / 4), false));
    }

    private void damageEntity() {
        player.user.sendPacket(new WrapperPlayServerEntityAnimation(entityId,
                WrapperPlayServerEntityAnimation.EntityAnimationType.HURT));
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Server.RESPAWN)) {
            entityId = -1;
        }
    }
}
