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

package org.prism_mc.prism.loader.services.dependencies;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Detects the user's geographical location to optimize dependency download sources.
 */
public class GeoLocationDetector {

    private static Boolean isInChina;

    /**
     * Checks if the server is likely running in China based on system properties.
     *
     * @return true if in China, false otherwise
     */
    public static boolean isInChina() {
        if (isInChina != null) {
            return isInChina;
        }

        isInChina = detectChina();
        return isInChina;
    }

    /**
     * Detects if the system is in China using multiple methods.
     *
     * @return true if in China
     */
    private static boolean detectChina() {
        // Check user language
        String language = System.getProperty("user.language", "");
        String country = System.getProperty("user.country", "");
        
        if ("zh".equals(language) && "CN".equals(country)) {
            return true;
        }

        // Check default locale
        Locale defaultLocale = Locale.getDefault();
        if ("CN".equals(defaultLocale.getCountry())) {
            return true;
        }

        // Check timezone
        TimeZone timeZone = TimeZone.getDefault();
        String zoneId = timeZone.getID();
        if (zoneId != null && zoneId.startsWith("Asia/Shanghai")) {
            return true;
        }
        if (zoneId != null && zoneId.startsWith("Asia/Chongqing")) {
            return true;
        }
        if (zoneId != null && zoneId.startsWith("Asia/Hong_Kong")) {
            return true;
        }

        // Check timezone offset (UTC+8)
        int offset = timeZone.getRawOffset();
        int hours = offset / (1000 * 60 * 60);
        if (hours == 8 && "zh".equals(language)) {
            return true;
        }

        return false;
    }

    /**
     * Resets the cached detection result (for testing purposes).
     */
    public static void resetCache() {
        isInChina = null;
    }
}

