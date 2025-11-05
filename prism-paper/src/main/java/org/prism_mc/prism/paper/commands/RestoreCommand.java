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
import org.prism_mc.prism.api.storage.StorageAdapter;
import org.prism_mc.prism.loader.services.logging.LoggingService;
import org.prism_mc.prism.paper.providers.TaskChainProvider;
import org.prism_mc.prism.paper.services.confirmation.ConfirmationService;
import org.prism_mc.prism.paper.services.messages.MessageService;
import org.prism_mc.prism.paper.services.modifications.PaperModificationQueueService;
import org.prism_mc.prism.paper.services.query.QueryService;

@Command(value = "prism", alias = { "pr" })
public class RestoreCommand {

    /**
     * The storage adapter.
     */
    private final StorageAdapter storageAdapter;

    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * The modification queue service.
     */
    private final PaperModificationQueueService modificationQueueService;

    /**
     * The query service.
     */
    private final QueryService queryService;

    /**
     * The task chain provider.
     */
    private final TaskChainProvider taskChainProvider;

    /**
     * The logging service.
     */
    private final LoggingService loggingService;

    /**
     * The help command.
     */
    private final HelpCommand helpCommand;

    /**
     * The confirmation service.
     */
    private final ConfirmationService confirmationService;

    /**
     * Construct the restore command.
     *
     * @param storageAdapter The storage adapter
     * @param messageService The message service
     * @param modificationQueueService The modification queue service
     * @param queryService The query service
     * @param taskChainProvider The task chain provider
     * @param loggingService The logging service
     * @param helpCommand The help command
     * @param confirmationService The confirmation service
     */
    @Inject
    public RestoreCommand(
        StorageAdapter storageAdapter,
        MessageService messageService,
        PaperModificationQueueService modificationQueueService,
        QueryService queryService,
        TaskChainProvider taskChainProvider,
        LoggingService loggingService,
        HelpCommand helpCommand,
        ConfirmationService confirmationService
    ) {
        this.storageAdapter = storageAdapter;
        this.messageService = messageService;
        this.modificationQueueService = modificationQueueService;
        this.queryService = queryService;
        this.taskChainProvider = taskChainProvider;
        this.loggingService = loggingService;
        this.helpCommand = helpCommand;
        this.confirmationService = confirmationService;
    }

    /**
     * Run the restore command.
     *
     * @param sender The command sender
     */
    @CommandFlags(key = "query-flags")
    @NamedArguments("query-parameters")
    @Command(value = "restore", alias = { "rs" })
    @Permission("prism.modify")
    public void onRestore(final CommandSender sender, final Arguments arguments) {
        // Check if any parameters were provided
        if (!hasAnyParameters(arguments)) {
            // Show error and help when no parameters provided
            messageService.errorNoParameters(sender);
            helpCommand.onHelp(sender, "restore");
            return;
        }

        // Ensure a queue is free
        if (!modificationQueueService.queueAvailable()) {
            messageService.errorQueueNotFree(sender);

            return;
        }

        var builder = queryService.queryFromArguments(sender, arguments);
        if (builder.isEmpty()) {
            // Parameter parsing failed, show error and help
            messageService.errorInvalidParameters(sender);
            helpCommand.onHelp(sender, "restore");
            return;
        }
        
        final ActivityQuery query = builder.get().restore().build();
        
        // Check if defaults were used (meaning parameters were incomplete)
        if (!query.defaultsUsed().isEmpty() && !arguments.hasFlag("nodefaults")) {
            messageService.errorIncompleteParameters(sender);
            helpCommand.onHelp(sender, "restore");
            return;
        }
        
        taskChainProvider
            .newChain()
            .asyncFirst(() -> {
                try {
                    return storageAdapter.queryActivities(query);
                } catch (Exception e) {
                    messageService.errorQueryExec(sender);
                    loggingService.handleException(e);
                }

                return null;
            })
            .abortIfNull()
            .syncLast(modifications -> {
                if (modifications.isEmpty()) {
                    messageService.noResults(sender);

                    return;
                }

                // Load the modification ruleset from the configs, and apply flags
                var modificationRuleset = modificationQueueService
                    .applyFlagsToModificationRuleset(arguments)
                    .build();

                // Build scope info for confirmation
                String scopeInfo = buildScopeInfo(query, modifications.size());

                // Request confirmation before applying
                confirmationService.requestConfirmation(
                    sender,
                    "恢复操作",
                    scopeInfo,
                    () -> {
                        modificationQueueService.newRestoreQueue(modificationRuleset, sender, query, modifications).apply();
                    }
                );
            })
            .execute();
    }

    /**
     * Build scope information for confirmation.
     *
     * @param query The activity query
     * @param modificationCount The number of modifications
     * @return The scope information string
     */
    private String buildScopeInfo(ActivityQuery query, int modificationCount) {
        StringBuilder scope = new StringBuilder();
        
        scope.append("将恢复 ").append(modificationCount).append(" 个修改");
        
        if (query.causePlayerNames() != null && !query.causePlayerNames().isEmpty()) {
            scope.append(" | 玩家: ").append(String.join(", ", query.causePlayerNames()));
        }
        
        // Show specific time range
        if (query.after() != null || query.before() != null) {
            scope.append(" | 时间范围: ");
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            // Note: timestamps in query are in seconds, Date constructor needs milliseconds
            if (query.after() != null && query.before() != null) {
                scope.append(sdf.format(new java.util.Date(query.after() * 1000)))
                     .append(" 至 ")
                     .append(sdf.format(new java.util.Date(query.before() * 1000)));
            } else if (query.after() != null) {
                scope.append(sdf.format(new java.util.Date(query.after() * 1000)))
                     .append(" 至今");
            } else if (query.before() != null) {
                scope.append("开始 至 ")
                     .append(sdf.format(new java.util.Date(query.before() * 1000)));
            }
        }
        
        // Show world name instead of UUID
        if (query.worldUuid() != null) {
            var world = org.bukkit.Bukkit.getWorld(query.worldUuid());
            String worldName = world != null ? world.getName() : query.worldUuid().toString().substring(0, 8);
            scope.append(" | 世界: ").append(worldName);
        }
        
        return scope.toString();
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
            return false;
        }
    }
}
