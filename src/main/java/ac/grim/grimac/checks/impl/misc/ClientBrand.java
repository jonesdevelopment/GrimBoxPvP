package ac.grim.grimac.checks.impl.misc;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.impl.exploit.ExploitA;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.MessageUtil;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ClientBrand extends Check implements PacketCheck {
    String brand = "unresolved";
    boolean hasBrand = false;

    public ClientBrand(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage packet = new WrapperPlayClientPluginMessage(event);
            String channelName = packet.getChannelName();
            if (channelName.equalsIgnoreCase("minecraft:brand") || channelName.equals("MC|Brand")) {
                handle(packet.getData());
            } else if (channelName.equalsIgnoreCase("autototem")) {
                String message = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("client-register-bad-channel", "%prefix% &f%player% registered channel %channel%")
                        .replace("%channel%", channelName);
                message = MessageUtil.replacePlaceholders(player, message);
                Component component = MessageUtil.miniMessage(message);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("grim.brand")) {
                        MessageUtil.sendMessage(player, component);
                    }
                }
                Bukkit.getLogger().warning(player.getName() + " registered channel " + channelName);
                player.timedOut();
            } else if ((channelName.equalsIgnoreCase("minecraft:register") || channelName.equalsIgnoreCase("REGISTER")) && !brand.equals("fabric")) {
                final String data = new String(packet.getData());
                if (data.contains("fabric")) {
                    String message = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("client-brand-format-spoofed", "%prefix% &f%player% tried to spoof their brand to %brand%");
                    message = MessageUtil.replacePlaceholders(player, message);
                    Component component = MessageUtil.miniMessage(message);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.hasPermission("grim.brand")) {
                            MessageUtil.sendMessage(player, component);
                        }
                    }
                    Bukkit.getLogger().warning(player.getName() + " tried to spoof their brand to " + brand);
                    player.timedOut();
                }
            }
        }
    }

    public void handle(byte[] data) {
        if (!hasBrand) {
            if (data.length > 64 || data.length == 0) {
                brand = "sent " + data.length + " bytes as brand";
            } else {
                byte[] minusLength = new byte[data.length - 1];
                System.arraycopy(data, 1, minusLength, 0, minusLength.length);

                brand = new String(minusLength).replace(" (Velocity)", ""); //removes velocity's brand suffix
                brand = ChatColor.stripColor(brand); //strip color codes from client brand
                if (player.checkManager.getPrePredictionCheck(ExploitA.class).checkString(brand)) brand = "sent log4j";
                if (!GrimAPI.INSTANCE.getConfigManager().isIgnoredClient(brand)) {
                    String message = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("client-brand-format", "%prefix% &f%player% joined using %brand%");
                    message = MessageUtil.replacePlaceholders(player, message);

                    Component component = MessageUtil.miniMessage(message);

                    // sendMessage is async safe while broadcast isn't due to adventure
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.hasPermission("grim.brand")) {
                            MessageUtil.sendMessage(player, component);
                        }
                    }
                }
            }

            hasBrand = true;
        }
    }

    public String getBrand() {
        return brand;
    }
}
