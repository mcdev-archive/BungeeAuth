package org.nocraft.renay.bungeeauth.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.nocraft.renay.bungeeauth.authentication.AttemptManager;
import org.nocraft.renay.bungeeauth.BungeeAuthPlugin;
import org.nocraft.renay.bungeeauth.config.Message;
import org.nocraft.renay.bungeeauth.config.MessageKeys;
import org.nocraft.renay.bungeeauth.event.PlayerAttemptsLoginExceeded;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class PlayerBanListener extends BungeeAuthListener {

    private volatile Map<UUID, Long> bannedPlayers = new HashMap<>();

    private final BungeeAuthPlugin plugin;
    private final int bannedTime;

    public PlayerBanListener(BungeeAuthPlugin plugin, int bannedTime) {
        super(plugin);
        this.plugin = plugin;
        this.bannedTime = bannedTime;
        plugin.getScheduler().asyncRepeating(
                this::cleanup, 15, TimeUnit.SECONDS);
    }

    private void cleanup() {
        Iterator<Map.Entry<UUID, Long>> iterator = this.bannedPlayers
                .entrySet().iterator();

        while (iterator.hasNext()) {
            Long endTime = iterator.next().getValue();

            if (System.currentTimeMillis() >= endTime) {
                iterator.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PostLoginEvent e) {
        ProxiedPlayer player = e.getPlayer();
        UUID uniqueId = player.getUniqueId();

        AttemptManager attemptManager = this.plugin.getAttemptManager();
        if (this.bannedPlayers.containsKey(uniqueId)) {
            int maxAttempts = attemptManager.maxAttempts();
            long banTime = this.bannedPlayers.get(uniqueId);
            long timeLeftMinutes = getTimeLeftMinutes(banTime);

            Message message = plugin.getMessageConfig().get(MessageKeys.BAD_NICKNAME);
            player.disconnect(message.asComponent(timeLeftMinutes, maxAttempts));
        }
    }

    public long getTimeLeftMinutes(long entry) {
        long now = System.currentTimeMillis();
        return (entry - now) / 1000 / 60;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerExceededAttempts(PlayerAttemptsLoginExceeded e) {
        Optional<ProxiedPlayer> player = this.plugin.getPlayer(e.getPlayerId());
        Message message = plugin.getMessageConfig().get(
                MessageKeys.EXCEEDED_LOGIN_ATTEMPTS);
        player.ifPresent(this::addBannedPlayer);
        player.ifPresent(p -> p.disconnect(message.asComponent()));
    }

    private void addBannedPlayer(ProxiedPlayer p) {
        // end time of ban in minutes represent in milliseconds
        long endTime = System.currentTimeMillis() + (1000 * 60 * bannedTime);
        this.bannedPlayers.put(p.getUniqueId(), endTime);
    }
}
