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

import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.prism_mc.prism.paper.hooks.ClaimsProvider;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.database.objects.Island;

/**
 * Claims provider for BentoBox.
 */
public class BentoBoxProvider implements ClaimsProvider {

    @Override
    public ClaimPlugin getClaimPlugin() {
        return ClaimPlugin.BENTOBOX;
    }

    @Override
    public boolean hasRegionAccess(Player player, Location location) {
        Optional<Island> island = BentoBox.getInstance().getIslands().getIslandAt(location);
        
        // No island at location
        if (!island.isPresent()) {
            return false;
        }
        
        // Check if player is a member of the island
        return island.get().getMembers().containsKey(player.getUniqueId());
    }
}

