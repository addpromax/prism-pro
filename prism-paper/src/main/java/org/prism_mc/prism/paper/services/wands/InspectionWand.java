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

package org.prism_mc.prism.paper.services.wands;

import com.google.inject.Inject;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.prism_mc.prism.api.activities.ActivityQuery;
import org.prism_mc.prism.api.services.wands.Wand;
import org.prism_mc.prism.api.services.wands.WandMode;
import org.prism_mc.prism.api.util.Coordinate;
import org.prism_mc.prism.loader.services.configuration.ConfigurationService;
import org.prism_mc.prism.paper.services.claims.ClaimsManager;
import org.prism_mc.prism.paper.services.lookup.LookupService;
import org.prism_mc.prism.paper.services.messages.MessageService;

public class InspectionWand implements Wand {

    /**
     * The configuration service.
     */
    private final ConfigurationService configurationService;

    /**
     * The lookup service.
     */
    private final LookupService lookupService;

    /**
     * The claims manager.
     */
    private final ClaimsManager claimsManager;

    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * The owner.
     */
    private Object owner;

    /**
     * Construct a new inspection wand.
     *
     * @param configurationService The configuration service
     * @param lookupService The lookup server
     * @param claimsManager The claims manager
     * @param messageService The message service
     */
    @Inject
    public InspectionWand(
        ConfigurationService configurationService,
        LookupService lookupService,
        ClaimsManager claimsManager,
        MessageService messageService
    ) {
        this.configurationService = configurationService;
        this.lookupService = lookupService;
        this.claimsManager = claimsManager;
        this.messageService = messageService;
    }

    @Override
    public WandMode mode() {
        return WandMode.INSPECT;
    }

    @Override
    public void setOwner(Object owner) {
        this.owner = owner;
    }

    @Override
    public void use(UUID worldUuid, Coordinate coordinate) {
        // Check if owner is a player
        if (!(owner instanceof Player)) {
            // Console or non-player, allow inspection
            final ActivityQuery query = ActivityQuery.builder()
                .worldUuid(worldUuid)
                .coordinate(coordinate)
                .limit(configurationService.prismConfig().defaults().perPage())
                .build();

            lookupService.lookup((CommandSender) owner, query);
            return;
        }

        Player player = (Player) owner;

        // Get the world and create location
        World world = Bukkit.getWorld(worldUuid);
        if (world == null) {
            return;
        }

        Location location = new Location(world, coordinate.x(), coordinate.y(), coordinate.z());

        // Check claim permissions
        ClaimsManager.InspectDenialReason denialReason = claimsManager.getInspectDenialReason(player, location);
        
        if (denialReason != null) {
            // Player doesn't have permission to inspect here
            switch (denialReason) {
                case NOT_YOUR_CLAIM:
                    messageService.claimsNotYourClaim(player);
                    break;
                case NO_WILDERNESS_PERMISSION:
                    messageService.claimsWildernessNoPermission(player);
                    break;
            }
            return;
        }

        // Permission granted, perform lookup
        final ActivityQuery query = ActivityQuery.builder()
            .worldUuid(worldUuid)
            .coordinate(coordinate)
            .limit(configurationService.prismConfig().defaults().perPage())
            .build();

        lookupService.lookup((CommandSender) owner, query);
    }
}
