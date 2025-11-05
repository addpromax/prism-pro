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

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.plot.Plot;
import org.bukkit.entity.Player;
import org.prism_mc.prism.paper.hooks.ClaimsProvider;

/**
 * Claims provider for PlotSquared 6.x and 7.x.
 */
public class PlotSquared6Provider implements ClaimsProvider {

    @Override
    public ClaimPlugin getClaimPlugin() {
        return ClaimPlugin.PLOT_SQUARED;
    }

    @Override
    public boolean hasRegionAccess(Player player, org.bukkit.Location bukkitLocation) {
        Location location = BukkitUtil.adaptComplete(bukkitLocation);
        Plot plot = location.getOwnedPlot();
        
        // No plot at location (wilderness)
        if (plot == null) {
            return false;
        }
        
        // Check if player is added or is owner
        return plot.isAdded(player.getUniqueId()) || plot.isOwner(player.getUniqueId());
    }
}

