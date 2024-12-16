/*
 * Copyright (C) 2024 Sonar Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ac.grim.grimac.checks.type;

import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCloseWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCloseWindow;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public class InventoryCheck extends BlockPlaceCheck implements PacketCheck {
    // Impossible transaction ID
    protected static final long NONE = Long.MAX_VALUE;

    protected long closeTransaction = NONE;
    protected int closePacketsToSkip;

    public InventoryCheck(GrimPlayer player) {
        super(player);
    }

    @Override
    @MustBeInvokedByOverriders
    public void onPacketReceive(final PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            // Disallow any clicks if inventory is closing
            if (closeTransaction != NONE && shouldModifyPackets()) {
                event.setCancelled(true);
                player.onPacketCancel();
                player.getInventory().needResend = true;
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW) {
            // Players with high ping can close inventory faster than send transaction back
            if (closeTransaction != NONE && closePacketsToSkip-- <= 0) {
                closeTransaction = NONE;
            }
        }
    }

    public void closeInventory() {
        if (closeTransaction != NONE) {
            return;
        }

        int windowId = player.getInventory().openWindowID;

        player.user.writePacket(new WrapperPlayServerCloseWindow(windowId));

        // Force close inventory on server side
        closePacketsToSkip = 1; // Sending close packet to itself, so skip it
        PacketEvents.getAPI().getProtocolManager().receivePacket(
                player.user.getChannel(), new WrapperPlayClientCloseWindow(windowId)
        );

        player.sendTransaction();

        int transaction = player.lastTransactionSent.get();
        closeTransaction = transaction;
        player.latencyUtils.addRealTimeTask(transaction, () -> {
            if (closeTransaction == transaction) {
                closeTransaction = NONE;
            }
        });

        player.user.flushPackets();
    }
}
