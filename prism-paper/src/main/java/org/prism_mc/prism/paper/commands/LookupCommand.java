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

package org.prism_mc.prism.paper.commands;

import com.google.inject.Inject;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.CommandFlags;
import dev.triumphteam.cmd.core.annotations.NamedArguments;
import dev.triumphteam.cmd.core.argument.keyed.Arguments;
import org.bukkit.command.CommandSender;
import org.prism_mc.prism.api.activities.ActivityQuery;
import org.prism_mc.prism.loader.services.configuration.ConfigurationService;
import org.prism_mc.prism.paper.services.lookup.LookupService;
import org.prism_mc.prism.paper.services.messages.MessageService;
import org.prism_mc.prism.paper.services.query.QueryService;

@Command(value = "prism", alias = { "pr" })
public class LookupCommand {

    /**
     * The configuration service.
     */
    private final ConfigurationService configurationService;

    /**
     * The query service.
     */
    private final QueryService queryService;

    /**
     * The lookup service.
     */
    private final LookupService lookupService;

    /**
     * The help command.
     */
    private final HelpCommand helpCommand;

    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * Construct the lookup command.
     *
     * @param configurationService The configuration service
     * @param queryService The query service
     * @param lookupService The lookup service
     * @param helpCommand The help command
     * @param messageService The message service
     */
    @Inject
    public LookupCommand(
        ConfigurationService configurationService,
        QueryService queryService,
        LookupService lookupService,
        HelpCommand helpCommand,
        MessageService messageService
    ) {
        this.configurationService = configurationService;
        this.queryService = queryService;
        this.lookupService = lookupService;
        this.helpCommand = helpCommand;
        this.messageService = messageService;
    }

    /**
     * Run a lookup.
     *
     * @param sender The command sender
     * @param arguments The arguments
     */
    @CommandFlags(key = "query-flags")
    @NamedArguments("query-parameters")
    @Command(value = "lookup", alias = { "l" })
    @Permission("prism.lookup")
    public void onLookup(final CommandSender sender, final Arguments arguments) {
        // Check if any parameters were provided
        if (!hasAnyParameters(arguments)) {
            // Show error and help when no parameters provided
            messageService.errorNoParameters(sender);
            helpCommand.onHelp(sender, "lookup");
            return;
        }

        var builder = queryService.queryFromArguments(sender, arguments);
        if (builder.isEmpty()) {
            // Parameter parsing failed, show error and help
            messageService.errorInvalidParameters(sender);
            helpCommand.onHelp(sender, "lookup");
            return;
        }
        
        final ActivityQuery query = builder
            .get()
            .limit(configurationService.prismConfig().defaults().perPage())
            .build();
        
        // Check if defaults were used (meaning parameters were incomplete)
        if (!query.defaultsUsed().isEmpty() && !arguments.hasFlag("nodefaults")) {
            messageService.errorIncompleteParameters(sender);
            helpCommand.onHelp(sender, "lookup");
            return;
        }
        
        lookupService.lookup(sender, query);
    }

    /**
     * Check if any query parameters were provided.
     *
     * @param arguments The arguments
     * @return true if parameters exist
     */
    private boolean hasAnyParameters(Arguments arguments) {
        // Check if any named arguments were provided
        try {
            return arguments.getArgument("p", String.class).isPresent() ||
                   arguments.getArgument("r", Integer.class).isPresent() ||
                   arguments.getArgument("since", String.class).isPresent() ||
                   arguments.getArgument("a", String.class).isPresent() ||
                   arguments.getArgument("b", String.class).isPresent() ||
                   arguments.getArgument("u", String.class).isPresent() ||
                   arguments.getArgument("t", String.class).isPresent() ||
                   arguments.getArgument("time", String.class).isPresent() ||
                   arguments.getArgument("before", String.class).isPresent() ||
                   arguments.getArgument("world", String.class).isPresent() ||
                   arguments.getArgument("w", String.class).isPresent() ||
                   arguments.getArgument("at", String.class).isPresent() ||
                   arguments.getArgument("in", String.class).isPresent() ||
                   arguments.getArgument("bounds", String.class).isPresent() ||
                   arguments.getArgument("id", Integer.class).isPresent() ||
                   arguments.getArgument("c", String.class).isPresent() ||
                   arguments.getArgument("i", String.class).isPresent() ||
                   arguments.getArgument("e", String.class).isPresent() ||
                   arguments.getArgument("radius", Integer.class).isPresent() ||
                   arguments.getArgument("action", String.class).isPresent();
        } catch (Exception e) {
            // If there's any error, assume no parameters
            return false;
        }
    }
}
