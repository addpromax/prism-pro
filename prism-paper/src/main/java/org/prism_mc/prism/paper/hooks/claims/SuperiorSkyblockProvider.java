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

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.prism_mc.prism.paper.hooks.ClaimsProvider;

/**
 * Claims provider for SuperiorSkyblock.
 */
public class SuperiorSkyblockProvider implements ClaimsProvider {

    private static final IslandPrivilege BUILD_PRIVILEGE = IslandPrivilege.getByName("BUILD");

    @Override
    public ClaimPlugin getClaimPlugin() {
        return ClaimPlugin.SUPERIOR_SKYBLOCK;
    }

    @Override
    public boolean hasRegionAccess(Player player, Location location) {
        Island island = SuperiorSkyblockAPI.getIslandAt(location);
        
        // No island at location
        if (island == null) {
            return false;
        }
        
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);
        
        // Check if player has build privilege on island
        return island.hasPermission(superiorPlayer, BUILD_PRIVILEGE);
    }
}

