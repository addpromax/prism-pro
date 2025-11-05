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

package org.prism_mc.prism.paper.commands;

import com.google.inject.Inject;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.entity.Player;
import org.prism_mc.prism.paper.services.confirmation.ConfirmationService;

@Command(value = "prism", alias = { "pr" })
public class ConfirmCommand {

    /**
     * The confirmation service.
     */
    private final ConfirmationService confirmationService;

    /**
     * Construct the confirm command.
     *
     * @param confirmationService The confirmation service
     */
    @Inject
    public ConfirmCommand(ConfirmationService confirmationService) {
        this.confirmationService = confirmationService;
    }

    /**
     * Confirm a pending action.
     *
     * @param player The player
     * @param confirmId The confirmation ID
     */
    @Command("confirm")
    public void onConfirm(final Player player, String confirmId) {
        confirmationService.confirm(player, confirmId);
    }

    /**
     * Cancel a pending action.
     *
     * @param player The player
     * @param confirmId The confirmation ID
     */
    @Command("cancel")
    public void onCancel(final Player player, String confirmId) {
        confirmationService.cancel(player, confirmId);
    }
}

