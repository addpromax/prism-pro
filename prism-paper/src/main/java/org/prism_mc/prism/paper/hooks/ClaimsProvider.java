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

package org.prism_mc.prism.paper.hooks;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Interface for claim/region protection providers.
 */
public interface ClaimsProvider {

    /**
     * Get the claim plugin type.
     *
     * @return The claim plugin
     */
    ClaimPlugin getClaimPlugin();

    /**
     * Check if a player has access to a location.
     *
     * @param player The player
     * @param location The location
     * @return True if the player has access, false otherwise
     */
    boolean hasRegionAccess(Player player, Location location);

    /**
     * Enum of supported claim plugins.
     */
    enum ClaimPlugin {
        LANDS_7,
        FACTIONS_X,
        PLOT_SQUARED,
        BENTOBOX,
        SUPERIOR_SKYBLOCK,
        GRIEF_DEFENDER,
        RESIDENCE,
        NONE  // No claim at location (wilderness)
    }
}

