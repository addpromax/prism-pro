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

package org.prism_mc.prism.paper.services.confirmation;

/**
 * Represents a pending confirmation.
 */
public class PendingConfirmation {

    /**
     * The confirmation ID.
     */
    private final String id;

    /**
     * The action to execute on confirmation.
     */
    private final Runnable onConfirm;

    /**
     * The timestamp when this confirmation was created.
     */
    private final long createdAt;

    /**
     * Construct a pending confirmation.
     *
     * @param id The confirmation ID
     * @param onConfirm The action to execute on confirmation
     */
    public PendingConfirmation(String id, Runnable onConfirm) {
        this.id = id;
        this.onConfirm = onConfirm;
        this.createdAt = System.currentTimeMillis();
    }

    /**
     * Get the confirmation ID.
     *
     * @return The confirmation ID
     */
    public String getId() {
        return id;
    }

    /**
     * Get the action to execute on confirmation.
     *
     * @return The action
     */
    public Runnable getOnConfirm() {
        return onConfirm;
    }

    /**
     * Get the timestamp when this confirmation was created.
     *
     * @return The creation timestamp
     */
    public long getCreatedAt() {
        return createdAt;
    }
}

