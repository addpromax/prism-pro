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

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.prism_mc.prism.paper.hooks.ClaimsProvider;

/**
 * Claims provider for GriefDefender.
 */
public class GriefDefenderProvider implements ClaimsProvider {

    @Override
    public ClaimPlugin getClaimPlugin() {
        return ClaimPlugin.GRIEF_DEFENDER;
    }

    @Override
    public boolean hasRegionAccess(Player player, Location location) {
        Claim claim = GriefDefender.getCore().getClaimAt(location);
        
        // No claim at location
        if (claim == null) {
            return false;
        }
        
        // Check if player is owner
        return claim.getOwnerUniqueId().equals(player.getUniqueId());
    }
}

