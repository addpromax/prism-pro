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

package org.prism_mc.prism.paper.hooks.claims;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.land.Area;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.prism_mc.prism.paper.hooks.ClaimsProvider;

/**
 * Claims provider for Lands 7.x.
 */
public class Lands7Provider implements ClaimsProvider {

    private LandsIntegration landsIntegration;

    /**
     * Constructor that initializes Lands integration.
     *
     * @param plugin The plugin instance
     */
    public Lands7Provider(Plugin plugin) {
        try {
            landsIntegration = LandsIntegration.of(plugin);
        } catch (Exception e) {
            // Failed to initialize
        }
    }

    /**
     * Default constructor for reflection.
     */
    public Lands7Provider() {
        // Will be initialized when needed
    }

    @Override
    public ClaimPlugin getClaimPlugin() {
        return ClaimPlugin.LANDS_7;
    }

    @Override
    public boolean hasRegionAccess(Player player, Location location) {
        if (landsIntegration == null) {
            return false;
        }

        Area area = landsIntegration.getArea(location);
        
        // No land at location (wilderness)
        if (area == null) {
            return false;
        }
        
        // Check if player can place blocks (indicates ownership/trust)
        return area.hasRoleFlag(player.getUniqueId(), Flags.BLOCK_PLACE);
    }
}

