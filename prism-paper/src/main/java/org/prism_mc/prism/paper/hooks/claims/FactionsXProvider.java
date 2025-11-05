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

import net.prosavage.factionsx.core.FPlayer;
import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.manager.GridManager;
import net.prosavage.factionsx.manager.PlayerManager;
import net.prosavage.factionsx.persist.data.FactionsKt;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.prism_mc.prism.paper.hooks.ClaimsProvider;

/**
 * Claims provider for FactionsX.
 */
public class FactionsXProvider implements ClaimsProvider {

    @Override
    public ClaimPlugin getClaimPlugin() {
        return ClaimPlugin.FACTIONS_X;
    }

    @Override
    public boolean hasRegionAccess(Player player, Location location) {
        FPlayer fPlayer = PlayerManager.INSTANCE.getFPlayer(player);
        
        if (fPlayer == null) {
            return false;
        }
        
        Faction faction = GridManager.INSTANCE.getFactionAt(FactionsKt.getFLocation(location));
        
        // Check if player is in bypass mode or owns the faction land
        return fPlayer.getInBypass() || (fPlayer.hasFaction() && fPlayer.getFaction().equals(faction));
    }
}

