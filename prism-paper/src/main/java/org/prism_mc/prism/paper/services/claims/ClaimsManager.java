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

package org.prism_mc.prism.paper.services.claims;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.prism_mc.prism.loader.services.configuration.ConfigurationService;
import org.prism_mc.prism.loader.services.logging.LoggingService;
import org.prism_mc.prism.paper.hooks.ClaimsProvider;

/**
 * Manages claim protection providers.
 */
@Singleton
public class ClaimsManager {

    /**
     * List of loaded claim providers.
     */
    private final List<ClaimsProvider> providers = new ArrayList<>();

    /**
     * The configuration service.
     */
    private final ConfigurationService configurationService;

    /**
     * The logging service.
     */
    private final LoggingService loggingService;

    /**
     * Construct the claims manager.
     *
     * @param configurationService The configuration service
     * @param loggingService The logging service
     */
    @Inject
    public ClaimsManager(ConfigurationService configurationService, LoggingService loggingService) {
        this.configurationService = configurationService;
        this.loggingService = loggingService;

        // Try to load all claim providers
        loadProviders();
    }

    /**
     * Load all available claim providers.
     */
    private void loadProviders() {
        // Lands 7.x
        tryLoadProvider(
            "me.angeschossen.lands.api.LandsIntegration",
            "org.prism_mc.prism.paper.hooks.claims.Lands7Provider"
        );

        // FactionsX
        tryLoadProvider(
            "net.prosavage.factionsx.FactionsX",
            "org.prism_mc.prism.paper.hooks.claims.FactionsXProvider"
        );

        // PlotSquared 6.x / 7.x
        tryLoadProvider(
            "com.plotsquared.bukkit.BukkitPlatform",
            "org.prism_mc.prism.paper.hooks.claims.PlotSquared6Provider"
        );

        // BentoBox
        tryLoadProvider(
            "world.bentobox.bentobox.BentoBox",
            "org.prism_mc.prism.paper.hooks.claims.BentoBoxProvider"
        );

        // SuperiorSkyblock
        tryLoadProvider(
            "com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI",
            "org.prism_mc.prism.paper.hooks.claims.SuperiorSkyblockProvider"
        );

        // GriefDefender
        tryLoadProvider(
            "com.griefdefender.api.GriefDefender",
            "org.prism_mc.prism.paper.hooks.claims.GriefDefenderProvider"
        );

        // Residence
        tryLoadProvider(
            "com.bekvon.bukkit.residence.Residence",
            "org.prism_mc.prism.paper.hooks.claims.ResidenceProvider"
        );

        if (providers.isEmpty()) {
            loggingService.info("No claim protection plugins detected");
        } else {
            List<String> providerNames = providers.stream()
                .map(p -> p.getClaimPlugin().name())
                .toList();
            loggingService.info("Loaded {0} claim provider(s): {1}", providers.size(), String.join(", ", providerNames));
        }
    }

    /**
     * Try to load a claim provider.
     *
     * @param pluginClass The plugin main class to check
     * @param providerClass The provider class to load
     */
    private void tryLoadProvider(String pluginClass, String providerClass) {
        try {
            // Check if plugin is installed
            Class.forName(pluginClass);

            // Try to load provider
            Class<?> clazz = Class.forName(providerClass);
            ClaimsProvider provider = (ClaimsProvider) clazz.getDeclaredConstructor().newInstance();
            providers.add(provider);
        } catch (ClassNotFoundException e) {
            // Plugin not installed, ignore
        } catch (Exception e) {
            loggingService.warn("Failed to load claim provider {0}: {1}", providerClass, e.getMessage());
        }
    }

    /**
     * Check if a player can use inspect at a location.
     * This checks if the player has access to the claim at the location,
     * or has wilderness permission if no claim exists.
     *
     * @param player The player
     * @param location The location
     * @return True if player can inspect, false otherwise
     */
    public boolean canInspectAt(Player player, Location location) {
        // Check if claim check is disabled in config
        // TODO: Add config option when config system is implemented
        // if (!configurationService.prismConfig().inspect().enableClaimCheck()) {
        //     return true;
        // }

        boolean foundClaim = false;
        boolean hasAccess = false;

        // Check all providers
        for (ClaimsProvider provider : providers) {
            boolean access = provider.hasRegionAccess(player, location);
            if (access) {
                // Player has access to at least one claim
                return true;
            }
            // If provider returned false, it means there's a claim but player doesn't have access
            // We need to continue checking other providers in case of multiple claim plugins
            foundClaim = true;
        }

        // If we found a claim but player has no access, deny
        if (foundClaim && !hasAccess) {
            return false;
        }

        // No claim found at location (wilderness), check permission
        return player.hasPermission("prism.inspect.wilderness");
    }

    /**
     * Check if a player can inspect at a location and get the reason if not.
     *
     * @param player The player
     * @param location The location
     * @return The denial reason, or null if allowed
     */
    public InspectDenialReason getInspectDenialReason(Player player, Location location) {
        boolean foundClaim = false;

        // Check all providers
        for (ClaimsProvider provider : providers) {
            boolean access = provider.hasRegionAccess(player, location);
            if (access) {
                return null; // Access granted
            }
            foundClaim = true;
        }

        // If we found a claim but player has no access
        if (foundClaim) {
            return InspectDenialReason.NOT_YOUR_CLAIM;
        }

        // No claim found, check wilderness permission
        if (!player.hasPermission("prism.inspect.wilderness")) {
            return InspectDenialReason.NO_WILDERNESS_PERMISSION;
        }

        return null; // Access granted
    }

    /**
     * Enum for inspect denial reasons.
     */
    public enum InspectDenialReason {
        NOT_YOUR_CLAIM,
        NO_WILDERNESS_PERMISSION
    }
}

