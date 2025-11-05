/*
 * prism
 *
 * Copyright (c) 2022 M Botsko (viveleroi)
 *                    Contributors
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

package org.prism_mc.prism.paper.services.confirmation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.prism_mc.prism.paper.PrismPaper;
import org.prism_mc.prism.paper.services.messages.MessageService;
import org.prism_mc.prism.paper.services.translation.PaperTranslationService;

/**
 * Service for managing command confirmations.
 */
@Singleton
public class ConfirmationService {

    /**
     * The main plugin instance.
     */
    private final JavaPlugin plugin;

    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * The translation service.
     */
    private final PaperTranslationService translationService;

    /**
     * Pending confirmations mapped by player UUID.
     */
    private final Map<UUID, PendingConfirmation> pendingConfirmations = new ConcurrentHashMap<>();

    /**
     * Timeout for confirmations in seconds.
     */
    private static final int CONFIRMATION_TIMEOUT = 30;

    /**
     * Construct the confirmation service.
     *
     * @param messageService The message service
     * @param translationService The translation service
     */
    @Inject
    public ConfirmationService(MessageService messageService, PaperTranslationService translationService) {
        this.plugin = PrismPaper.instance().loaderPlugin();
        this.messageService = messageService;
        this.translationService = translationService;
    }

    /**
     * Request confirmation for a world-affecting action.
     *
     * @param sender The command sender
     * @param action The action to confirm
     * @param scopeInfo Information about the scope of the action
     * @param onConfirm The runnable to execute when confirmed
     */
    public void requestConfirmation(
        CommandSender sender,
        String action,
        String scopeInfo,
        Runnable onConfirm
    ) {
        // Only players can confirm (console commands execute immediately)
        if (!(sender instanceof Player player)) {
            onConfirm.run();
            return;
        }

        UUID playerId = player.getUniqueId();
        String confirmId = UUID.randomUUID().toString().substring(0, 8);

        // Store the pending confirmation
        PendingConfirmation pending = new PendingConfirmation(confirmId, onConfirm);
        pendingConfirmations.put(playerId, pending);

        // Send confirmation message
        sendConfirmationMessage(player, action, scopeInfo, confirmId);

        // Schedule timeout
        new BukkitRunnable() {
            @Override
            public void run() {
                PendingConfirmation current = pendingConfirmations.get(playerId);
                if (current != null && current.getId().equals(confirmId)) {
                    pendingConfirmations.remove(playerId);
                    messageService.confirmationTimeout(player);
                }
            }
        }.runTaskLater(plugin, CONFIRMATION_TIMEOUT * 20L);
    }

    /**
     * Send the confirmation message with clickable buttons.
     *
     * @param player The player
     * @param action The action description
     * @param scopeInfo Scope information
     * @param confirmId The confirmation ID
     */
    private void sendConfirmationMessage(Player player, String action, String scopeInfo, String confirmId) {
        String border = msg("prism.confirmation.header-border", player);
        String title = msg("prism.confirmation.header-title", player);
        String actionLabel = msg("prism.confirmation.action-label", player);
        String scopeLabel = msg("prism.confirmation.scope-label", player);
        String warning = msg("prism.confirmation.warning", player);
        String buttonConfirm = msg("prism.confirmation.button-confirm", player);
        String buttonConfirmHover = msg("prism.confirmation.button-confirm-hover", player);
        String buttonCancel = msg("prism.confirmation.button-cancel", player);
        String buttonCancelHover = msg("prism.confirmation.button-cancel-hover", player);
        String timeoutInfo = msg("prism.confirmation.timeout-info", player).replace("<timeout>", String.valueOf(CONFIRMATION_TIMEOUT));

        player.sendMessage(Component.empty());
        player.sendMessage(Component.text(border, NamedTextColor.DARK_GRAY));
        
        // Warning header
        player.sendMessage(Component.text()
            .append(Component.text("⚠ ", NamedTextColor.GOLD, TextDecoration.BOLD))
            .append(Component.text(title, NamedTextColor.GOLD, TextDecoration.BOLD))
            .build());
        
        player.sendMessage(Component.empty());
        
        // Action info
        player.sendMessage(Component.text()
            .append(Component.text(actionLabel + ": ", NamedTextColor.AQUA))
            .append(Component.text(action, NamedTextColor.WHITE))
            .build());
        
        // Scope info
        player.sendMessage(Component.text()
            .append(Component.text(scopeLabel + ": ", NamedTextColor.AQUA))
            .append(Component.text(scopeInfo, NamedTextColor.YELLOW))
            .build());
        
        player.sendMessage(Component.empty());
        
        // Warning message
        player.sendMessage(Component.text()
            .append(Component.text("⚠ ", NamedTextColor.RED))
            .append(Component.text(warning, NamedTextColor.GRAY))
            .build());
        
        player.sendMessage(Component.empty());
        
        // Confirm button (green)
        Component confirmButton = Component.text()
            .append(Component.text(" " + buttonConfirm + " ", NamedTextColor.GREEN, TextDecoration.BOLD))
            .clickEvent(ClickEvent.runCommand("/prism confirm " + confirmId))
            .hoverEvent(HoverEvent.showText(Component.text(buttonConfirmHover, NamedTextColor.GREEN)))
            .build();
        
        // Cancel button (red)
        Component cancelButton = Component.text()
            .append(Component.text(" " + buttonCancel + " ", NamedTextColor.RED, TextDecoration.BOLD))
            .clickEvent(ClickEvent.runCommand("/prism cancel " + confirmId))
            .hoverEvent(HoverEvent.showText(Component.text(buttonCancelHover, NamedTextColor.RED)))
            .build();
        
        // Send buttons on same line
        player.sendMessage(Component.text()
            .append(confirmButton)
            .append(Component.text("  ", NamedTextColor.WHITE))
            .append(cancelButton)
            .build());
        
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text()
            .append(Component.text("⏱ ", NamedTextColor.GRAY))
            .append(Component.text(timeoutInfo, NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))
            .build());
        
        player.sendMessage(Component.text(border, NamedTextColor.DARK_GRAY));
        player.sendMessage(Component.empty());
    }

    /**
     * Get a localized message.
     *
     * @param key The message key
     * @param player The player
     * @return The localized message
     */
    private String msg(String key, Player player) {
        String message = translationService.messageOf(player, key);
        return message != null ? message : key;
    }

    /**
     * Confirm a pending action.
     *
     * @param player The player
     * @param confirmId The confirmation ID
     * @return true if the confirmation was successful
     */
    public boolean confirm(Player player, String confirmId) {
        PendingConfirmation pending = pendingConfirmations.get(player.getUniqueId());
        
        if (pending == null) {
            messageService.confirmationNoPending(player);
            return false;
        }
        
        if (!pending.getId().equals(confirmId)) {
            messageService.confirmationInvalidId(player);
            return false;
        }
        
        // Remove from pending and execute
        pendingConfirmations.remove(player.getUniqueId());
        
        messageService.confirmationConfirmed(player);
        
        pending.getOnConfirm().run();
        return true;
    }

    /**
     * Cancel a pending action.
     *
     * @param player The player
     * @param confirmId The confirmation ID
     * @return true if the cancellation was successful
     */
    public boolean cancel(Player player, String confirmId) {
        PendingConfirmation pending = pendingConfirmations.get(player.getUniqueId());
        
        if (pending == null) {
            messageService.confirmationNoCancelPending(player);
            return false;
        }
        
        if (!pending.getId().equals(confirmId)) {
            messageService.confirmationInvalidId(player);
            return false;
        }
        
        // Remove from pending
        pendingConfirmations.remove(player.getUniqueId());
        
        messageService.confirmationCancelled(player);
        
        return true;
    }

    /**
     * Check if a player has a pending confirmation.
     *
     * @param player The player
     * @return true if the player has a pending confirmation
     */
    public boolean hasPendingConfirmation(Player player) {
        return pendingConfirmations.containsKey(player.getUniqueId());
    }

    /**
     * Clear all pending confirmations for a player.
     *
     * @param player The player
     */
    public void clearPendingConfirmations(Player player) {
        pendingConfirmations.remove(player.getUniqueId());
    }
}

