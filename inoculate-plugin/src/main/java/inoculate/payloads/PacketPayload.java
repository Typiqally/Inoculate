package inoculate.payloads;

import inoculate.injector.Payload;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PacketPayload implements Payload, Listener {

    private ChannelDuplexHandler handler;

    @Override
    public void onLoad(Plugin plugin) {

    }

    @Override
    public void onEnable(Plugin plugin) {
        this.handler = new Handler();
        Bukkit.getServer().getConsoleSender().sendMessage("Inoculate loaded inside " + plugin.getName());
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final CraftPlayer player = (CraftPlayer) event.getPlayer();
        final ChannelPipeline pipeline = player.getHandle().playerConnection.networkManager.channel.pipeline();

        pipeline.addBefore("packet_handler", player.getName(), handler);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final CraftPlayer player = (CraftPlayer) event.getPlayer();
        final Channel channel = player.getHandle().playerConnection.networkManager.channel;

        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getName());
            return null;
        });
    }

    public static class Handler extends ChannelDuplexHandler {
        public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "Read: " + ChatColor.GOLD + message.toString());
            super.channelRead(context, message);
        }
    }
}
