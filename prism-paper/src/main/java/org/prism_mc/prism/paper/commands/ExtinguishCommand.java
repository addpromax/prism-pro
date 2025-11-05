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
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Optional;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.prism_mc.prism.loader.services.configuration.ConfigurationService;
import org.prism_mc.prism.paper.services.confirmation.ConfirmationService;
import org.prism_mc.prism.paper.services.messages.MessageService;
import org.prism_mc.prism.paper.services.modifications.state.BlockStateChange;
import org.prism_mc.prism.paper.utils.BlockUtils;

@Command(value = "prism", alias = { "pr" })
public class ExtinguishCommand {

    /**
     * The configuration service.
     */
    private final ConfigurationService configurationService;

    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * The confirmation service.
     */
    private final ConfirmationService confirmationService;

    /**
     * Construct the extinguish command.
     *
     * @param configurationService The configuration service
     * @param messageService The message service
     * @param confirmationService The confirmation service
     */
    @Inject
    public ExtinguishCommand(
        ConfigurationService configurationService,
        MessageService messageService,
        ConfirmationService confirmationService
    ) {
        this.configurationService = configurationService;
        this.messageService = messageService;
        this.confirmationService = confirmationService;
    }

    /**
     * Run the command.
     *
     * @param player The player
     * @param radius The radius
     */
    @Command(value = "extinguish", alias = { "ex" })
    @Permission("prism.extinguish")
    public void onExtinguish(final Player player, @Optional Integer radius) {
        if (radius == null) {
            radius = configurationService.prismConfig().defaults().extinguishRadius();
        }

        final int finalRadius = radius;
        double x1 = player.getLocation().getBlockX() - radius;
        double y1 = player.getLocation().getBlockY() - radius;
        double z1 = player.getLocation().getBlockZ() - radius;
        double x2 = player.getLocation().getBlockX() + radius;
        double y2 = player.getLocation().getBlockY() + radius;
        double z2 = player.getLocation().getBlockZ() + radius;
        BoundingBox boundingBox = new BoundingBox(x1, y1, z1, x2, y2, z2);

        // Build scope info
        String scopeInfo = "扑灭半径 " + finalRadius + " 格内的所有火焰";

        // Request confirmation
        confirmationService.requestConfirmation(
            player,
            "扑灭火焰",
            scopeInfo,
            () -> {
                List<BlockStateChange> changes = BlockUtils.removeBlocksByMaterial(
                    player.getWorld(),
                    boundingBox,
                    List.of(Material.FIRE)
                );
                int removalCount = changes.size();

                if (removalCount > 0) {
                    messageService.modificationsRemovedBlocks(player, removalCount);
                } else {
                    messageService.errorNoBlocksRemoved(player);
                }
            }
        );
    }
}
