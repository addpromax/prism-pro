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

package org.prism_mc.prism.paper;

import de.tr7zw.nbtapi.utils.DataFixerUtil;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.argument.keyed.Argument;
import dev.triumphteam.cmd.core.argument.keyed.ArgumentKey;
import dev.triumphteam.cmd.core.argument.keyed.Flag;
import dev.triumphteam.cmd.core.argument.keyed.FlagKey;
import dev.triumphteam.cmd.core.extension.CommandOptions;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Registry;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.prism_mc.prism.api.Prism;
import org.prism_mc.prism.api.actions.types.ActionTypeRegistry;
import org.prism_mc.prism.api.services.recording.RecordingService;
import org.prism_mc.prism.api.storage.StorageAdapter;
import org.prism_mc.prism.loader.services.configuration.ConfigurationService;
import org.prism_mc.prism.loader.services.dependencies.Dependency;
import org.prism_mc.prism.loader.services.dependencies.DependencyService;
import org.prism_mc.prism.loader.services.dependencies.loader.PluginLoader;
import org.prism_mc.prism.loader.services.scheduler.ThreadPoolScheduler;
import org.prism_mc.prism.paper.actions.types.PaperActionTypeRegistry;
import org.prism_mc.prism.paper.commands.AboutCommand;
import org.prism_mc.prism.paper.commands.CacheCommand;
import org.prism_mc.prism.paper.commands.ConfigsCommand;
import org.prism_mc.prism.paper.commands.ConfirmCommand;
import org.prism_mc.prism.paper.commands.DrainCommand;
import org.prism_mc.prism.paper.commands.ExtinguishCommand;
import org.prism_mc.prism.paper.commands.HelpCommand;
import org.prism_mc.prism.paper.commands.LookupCommand;
import org.prism_mc.prism.paper.commands.NearCommand;
import org.prism_mc.prism.paper.commands.PageCommand;
import org.prism_mc.prism.paper.commands.PreviewCommand;
import org.prism_mc.prism.paper.commands.PurgeCommand;
import org.prism_mc.prism.paper.commands.ReportCommand;
import org.prism_mc.prism.paper.commands.RestoreCommand;
import org.prism_mc.prism.paper.commands.RollbackCommand;
import org.prism_mc.prism.paper.commands.TeleportCommand;
import org.prism_mc.prism.paper.commands.VaultCommand;
import org.prism_mc.prism.paper.commands.WandCommand;
import org.prism_mc.prism.paper.listeners.block.BlockBreakListener;
import org.prism_mc.prism.paper.listeners.block.BlockBurnListener;
import org.prism_mc.prism.paper.listeners.block.BlockDispenseListener;
import org.prism_mc.prism.paper.listeners.block.BlockExplodeListener;
import org.prism_mc.prism.paper.listeners.block.BlockFadeListener;
import org.prism_mc.prism.paper.listeners.block.BlockFertilizeListener;
import org.prism_mc.prism.paper.listeners.block.BlockFormListener;
import org.prism_mc.prism.paper.listeners.block.BlockFromToListener;
import org.prism_mc.prism.paper.listeners.block.BlockIgniteListener;
import org.prism_mc.prism.paper.listeners.block.BlockPistonExtendListener;
import org.prism_mc.prism.paper.listeners.block.BlockPistonRetractListener;
import org.prism_mc.prism.paper.listeners.block.BlockPlaceListener;
import org.prism_mc.prism.paper.listeners.block.BlockSpreadListener;
import org.prism_mc.prism.paper.listeners.block.TntPrimeListener;
import org.prism_mc.prism.paper.listeners.entity.EntityBlockFormListener;
import org.prism_mc.prism.paper.listeners.entity.EntityChangeBlockListener;
import org.prism_mc.prism.paper.listeners.entity.EntityDamageByEntityListener;
import org.prism_mc.prism.paper.listeners.entity.EntityDeathListener;
import org.prism_mc.prism.paper.listeners.entity.EntityExplodeListener;
import org.prism_mc.prism.paper.listeners.entity.EntityPickupItemListener;
import org.prism_mc.prism.paper.listeners.entity.EntityPlaceListener;
import org.prism_mc.prism.paper.listeners.entity.EntityTransformListener;
import org.prism_mc.prism.paper.listeners.entity.EntityUnleashListener;
import org.prism_mc.prism.paper.listeners.hanging.HangingBreakByEntityListener;
import org.prism_mc.prism.paper.listeners.hanging.HangingBreakListener;
import org.prism_mc.prism.paper.listeners.hanging.HangingPlaceListener;
import org.prism_mc.prism.paper.listeners.inventory.InventoryClickListener;
import org.prism_mc.prism.paper.listeners.inventory.InventoryDragListener;
import org.prism_mc.prism.paper.listeners.inventory.InventoryMoveItemListener;
import org.prism_mc.prism.paper.listeners.leaves.LeavesDecayListener;
import org.prism_mc.prism.paper.listeners.player.PlayerArmorStandManipulateListener;
import org.prism_mc.prism.paper.listeners.player.PlayerBedEnterListener;
import org.prism_mc.prism.paper.listeners.player.PlayerBucketEmptyListener;
import org.prism_mc.prism.paper.listeners.player.PlayerBucketEntityListener;
import org.prism_mc.prism.paper.listeners.player.PlayerBucketFillListener;
import org.prism_mc.prism.paper.listeners.player.PlayerCommandPreprocessListener;
import org.prism_mc.prism.paper.listeners.player.PlayerDropItemListener;
import org.prism_mc.prism.paper.listeners.player.PlayerExpChangeListener;
import org.prism_mc.prism.paper.listeners.player.PlayerHarvestBlockListener;
import org.prism_mc.prism.paper.listeners.player.PlayerInteractEntityListener;
import org.prism_mc.prism.paper.listeners.player.PlayerInteractListener;
import org.prism_mc.prism.paper.listeners.player.PlayerJoinListener;
import org.prism_mc.prism.paper.listeners.player.PlayerLeashEntityListener;
import org.prism_mc.prism.paper.listeners.player.PlayerQuitListener;
import org.prism_mc.prism.paper.listeners.player.PlayerShearEntityListener;
import org.prism_mc.prism.paper.listeners.player.PlayerTakeLecternBookListener;
import org.prism_mc.prism.paper.listeners.player.PlayerTeleportListener;
import org.prism_mc.prism.paper.listeners.player.PlayerUnleashEntityListener;
import org.prism_mc.prism.paper.listeners.portal.PortalCreateListener;
import org.prism_mc.prism.paper.listeners.projectile.ProjectileLaunchListener;
import org.prism_mc.prism.paper.listeners.raid.RaidTriggerListener;
import org.prism_mc.prism.paper.listeners.sheep.SheepDyeWoolListener;
import org.prism_mc.prism.paper.listeners.sign.SignChangeListener;
import org.prism_mc.prism.paper.listeners.sponge.SpongeAbsorbListener;
import org.prism_mc.prism.paper.listeners.structure.StructureGrowListener;
import org.prism_mc.prism.paper.listeners.vehicle.VehicleDestroyListener;
import org.prism_mc.prism.paper.listeners.vehicle.VehicleEnterListener;
import org.prism_mc.prism.paper.listeners.vehicle.VehicleExitListener;
import org.prism_mc.prism.paper.providers.InjectorProvider;
import org.prism_mc.prism.paper.services.messages.MessageService;
import org.prism_mc.prism.paper.services.purge.PurgeService;
import org.prism_mc.prism.paper.services.recording.PaperRecordingService;
import org.prism_mc.prism.paper.services.scheduling.SchedulingService;
import org.prism_mc.prism.paper.utils.VersionUtils;

public class PrismPaper implements Prism {

    /**
     *  Get this instance.
     */
    @Getter
    private static PrismPaper instance;

    /**
     * The bootstrap.
     */
    private final PrismPaperBootstrap bootstrap;

    /**
     * The injector provider.
     */
    @Getter
    private InjectorProvider injectorProvider;

    /**
     * The purge service.
     */
    private PurgeService purgeService;

    /**
     * The recording service.
     */
    private RecordingService recordingService;

    /**
     * Sets a numeric version we can use to handle differences between serialization formats.
     */
    @Getter
    protected short serializerVersion;

    /**
     * The storage adapter.
     */
    @Getter
    private StorageAdapter storageAdapter;

    /**
     * The action type registry.
     */
    @Getter
    private ActionTypeRegistry actionTypeRegistry;

    /**
     * The thread pool scheduler.
     */
    private final ThreadPoolScheduler threadPoolScheduler;

    /**
     * Constructor.
     */
    public PrismPaper(PrismPaperBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.threadPoolScheduler = new ThreadPoolScheduler(loader());
        instance = this;
    }

    /**
     * Get all platform dependencies.
     *
     * @return The platform dependencies
     */
    protected Set<Dependency> platformDependencies() {
        return EnumSet.of(Dependency.TASKCHAIN_BUKKIT, Dependency.TASKCHAIN_CORE);
    }

    /**
     * Suppress verbose logging from third-party libraries.
     */
    private void suppressLibraryLogs() {
        // Suppress logs using Java Util Logging (works for most SLF4J bridges)
        try {
            // HikariCP
            java.util.logging.Logger.getLogger("org.prism_mc.prism.libs.hikari").setLevel(java.util.logging.Level.SEVERE);
            
            // JOOQ
            java.util.logging.Logger.getLogger("org.prism_mc.prism.libs.jooq").setLevel(java.util.logging.Level.SEVERE);
            
            // Quartz
            java.util.logging.Logger.getLogger("org.prism_mc.prism.libs.quartz").setLevel(java.util.logging.Level.SEVERE);
        } catch (Exception e) {
            // Ignore if logging configuration fails
        }
    }

    /**
     * On enable.
     */
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        
        // Suppress verbose library logging
        suppressLibraryLogs();
        
        DependencyService dependencyService = new DependencyService(
            bootstrap.loggingService(),
            bootstrap.loader().configurationService(),
            loaderPlugin().getDataFolder().toPath(),
            bootstrap.classPathAppender(),
            threadPoolScheduler
        );
        dependencyService.loadAllDependencies(platformDependencies());

        serializerVersion = (short) DataFixerUtil.getCurrentVersion();

        injectorProvider = new InjectorProvider(this, bootstrap.loggingService());

        // Choose and initialize the datasource
        try {
            storageAdapter = injectorProvider.injector().getInstance(StorageAdapter.class);
            if (!storageAdapter.ready()) {
                disable();

                return;
            }
        } catch (Exception e) {
            bootstrap.loggingService().handleException(e);

            disable();

            return;
        }

        actionTypeRegistry = injectorProvider.injector().getInstance(ActionTypeRegistry.class);

        String pluginName = this.loaderPlugin().getDescription().getName();
        String pluginVersion = this.loaderPlugin().getDescription().getVersion();
        
        long initTime = System.currentTimeMillis() - startTime;
        bootstrap.loggingService().info("Initializing {0} v{1} (schema: {2}, core: {3}ms)", 
            pluginName, pluginVersion, serializerVersion, initTime);

        if (loaderPlugin().isEnabled()) {
            long servicesStart = System.currentTimeMillis();
            
            // Initialize some classes
            recordingService = injectorProvider.injector().getInstance(PaperRecordingService.class);
            purgeService = injectorProvider.injector().getInstance(PurgeService.class);
            injectorProvider.injector().getInstance(SchedulingService.class);
            
            long servicesTime = System.currentTimeMillis() - servicesStart;
            
            long listenersStart = System.currentTimeMillis();

            // Register event listeners - batch registration for better performance
            registerEvents();
            
            long listenersTime = System.currentTimeMillis() - listenersStart;

            // Register commands
            long commandsStart = System.currentTimeMillis();
            
            BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(
                loaderPlugin(),
                CommandOptions.Builder::suggestLowercaseEnum
            );

            // Customize command messages
            var messagingService = injectorProvider.injector().getInstance(MessageService.class);
            var configurationService = injectorProvider.injector().getInstance(ConfigurationService.class);

            registerCommandMessages(commandManager, messagingService);
            registerCommandSuggestions(commandManager, configurationService);
            registerCommandArguments(commandManager);
            registerCommands(commandManager);
            
            long commandsTime = System.currentTimeMillis() - commandsStart;
            long totalTime = System.currentTimeMillis() - startTime;
            
            bootstrap.loggingService().info("Startup complete in {0}ms (services: {1}ms, listeners: {2}ms, commands: {3}ms)", 
                totalTime, servicesTime, listenersTime, commandsTime);
        }
    }
    
    /**
     * Register all event listeners in batch.
     */
    private void registerEvents() {
        Class<? extends Listener>[] listeners = new Class[] {
            BlockBreakListener.class,
            BlockBurnListener.class,
            BlockDispenseListener.class,
            BlockExplodeListener.class,
            BlockFadeListener.class,
            BlockFertilizeListener.class,
            BlockFormListener.class,
            BlockFromToListener.class,
            BlockIgniteListener.class,
            BlockPistonExtendListener.class,
            BlockPistonRetractListener.class,
            BlockPlaceListener.class,
            BlockSpreadListener.class,
            EntityBlockFormListener.class,
            EntityChangeBlockListener.class,
            EntityDamageByEntityListener.class,
            EntityDeathListener.class,
            EntityExplodeListener.class,
            EntityPickupItemListener.class,
            EntityPlaceListener.class,
            EntityTransformListener.class,
            EntityUnleashListener.class,
            HangingBreakListener.class,
            HangingBreakByEntityListener.class,
            HangingPlaceListener.class,
            InventoryClickListener.class,
            InventoryDragListener.class,
            InventoryMoveItemListener.class,
            LeavesDecayListener.class,
            PlayerArmorStandManipulateListener.class,
            PlayerBedEnterListener.class,
            PlayerBucketEmptyListener.class,
            PlayerBucketEntityListener.class,
            PlayerBucketFillListener.class,
            PlayerCommandPreprocessListener.class,
            PlayerDropItemListener.class,
            PlayerExpChangeListener.class,
            PlayerHarvestBlockListener.class,
            PlayerInteractListener.class,
            PlayerInteractEntityListener.class,
            PlayerJoinListener.class,
            PlayerLeashEntityListener.class,
            PlayerQuitListener.class,
            PlayerShearEntityListener.class,
            PlayerTakeLecternBookListener.class,
            PlayerTeleportListener.class,
            PlayerUnleashEntityListener.class,
            ProjectileLaunchListener.class,
            PortalCreateListener.class,
            RaidTriggerListener.class,
            SheepDyeWoolListener.class,
            SignChangeListener.class,
            SpongeAbsorbListener.class,
            StructureGrowListener.class,
            TntPrimeListener.class,
            VehicleDestroyListener.class,
            VehicleEnterListener.class,
            VehicleExitListener.class
        };
        
        // Cache frequently accessed objects
        var pluginManager = loaderPlugin().getServer().getPluginManager();
        var injector = injectorProvider.injector();
        var plugin = loaderPlugin();
        
        // Register all listeners
        for (Class<? extends Listener> listenerClass : listeners) {
            Listener listener = injector.getInstance(listenerClass);
            pluginManager.registerEvents(listener, plugin);
        }
    }
    
    /**
     * Register command messages.
     */
    private void registerCommandMessages(BukkitCommandManager<CommandSender> commandManager, MessageService messagingService) {
        commandManager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> {
            messagingService.errorConsoleOnly(sender);
        });

        commandManager.registerMessage(BukkitMessageKey.INVALID_ARGUMENT, (sender, context) -> {
            messagingService.errorInvalidParameter(sender);
        });

        commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> {
            messagingService.errorInsufficientPermission(sender);
        });

        commandManager.registerMessage(BukkitMessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> {
            messagingService.errorUnknownCommand(sender);
        });

        commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> {
            messagingService.errorPlayerOnly(sender);
        });

        commandManager.registerMessage(BukkitMessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> {
            messagingService.errorUnknownCommand(sender);
        });

        commandManager.registerMessage(BukkitMessageKey.UNKNOWN_COMMAND, (sender, context) -> {
            messagingService.errorUnknownCommand(sender);
        });
    }
    
    /**
     * Register command suggestions.
     */
    private void registerCommandSuggestions(BukkitCommandManager<CommandSender> commandManager, ConfigurationService configurationService) {

            // Register action types auto-suggest
            commandManager.registerSuggestion(SuggestionKey.of("actions"), (sender, context) -> {
                List<String> actionFamilies = new ArrayList<>();
                for (var actionType : injectorProvider
                    .injector()
                    .getInstance(PaperActionTypeRegistry.class)
                    .actionTypes()) {
                    actionFamilies.add(actionType.familyKey());
                }

                return actionFamilies;
            });

            // Register block types auto-suggest
            commandManager.registerSuggestion(SuggestionKey.of("blocks"), (sender, context) -> {
                List<String> suggestions = new ArrayList<>();
                for (var block : Registry.BLOCK) {
                    suggestions.add(block.getKey().getKey());
                }

                return suggestions;
            });

            // Register block tags auto-suggest
            commandManager.registerSuggestion(SuggestionKey.of("blocktags"), (sender, context) -> {
                var blockTagWhitelistEnabled = configurationService.prismConfig().commands().blockTagWhitelistEnabled();
                var blockTagWhitelist = configurationService.prismConfig().commands().blockTagWhitelist();

                List<String> tags = new ArrayList<>();
                for (Tag<Material> tag : Bukkit.getTags("blocks", Material.class)) {
                    var tagString = tag.getKey().toString();

                    if (
                        tagString.contains("minecraft:") &&
                        !configurationService.prismConfig().commands().allowMinecraftTags()
                    ) {
                        continue;
                    }

                    if (
                        blockTagWhitelist.isEmpty() ||
                        !blockTagWhitelistEnabled ||
                        blockTagWhitelist.contains(tagString)
                    ) {
                        tags.add(tagString);
                    }
                }

                return tags;
            });

            // Register item tags auto-suggest
            commandManager.registerSuggestion(SuggestionKey.of("itemtags"), (sender, context) -> {
                var itemTagWhitelistEnabled = configurationService.prismConfig().commands().itemTagWhitelistEnabled();
                var itemTagWhitelist = configurationService.prismConfig().commands().itemTagWhitelist();

                List<String> tags = new ArrayList<>();
                for (Tag<Material> tag : Bukkit.getTags("items", Material.class)) {
                    var tagString = tag.getKey().toString();

                    if (
                        tagString.contains("minecraft:") &&
                        !configurationService.prismConfig().commands().allowMinecraftTags()
                    ) {
                        continue;
                    }

                    if (
                        itemTagWhitelist.isEmpty() || !itemTagWhitelistEnabled || itemTagWhitelist.contains(tagString)
                    ) {
                        tags.add(tagString);
                    }
                }

                return tags;
            });

            // Register entity tags auto-suggest
            commandManager.registerSuggestion(SuggestionKey.of("entitytypetags"), (sender, context) -> {
                var entityTypeTagWhitelistEnabled = configurationService
                    .prismConfig()
                    .commands()
                    .entityTypeTagWhitelistEnabled();
                var entityTypeTagWhitelist = configurationService.prismConfig().commands().entityTypeTagWhitelist();

                List<String> tags = new ArrayList<>();
                for (Tag<EntityType> tag : Bukkit.getTags("entity_types", EntityType.class)) {
                    var tagString = tag.getKey().toString();

                    if (
                        tagString.contains("minecraft:") &&
                        !configurationService.prismConfig().commands().allowMinecraftTags()
                    ) {
                        continue;
                    }

                    if (
                        entityTypeTagWhitelist.isEmpty() ||
                        !entityTypeTagWhitelistEnabled ||
                        entityTypeTagWhitelist.contains(tagString)
                    ) {
                        tags.add(tagString);
                    }
                }

                return tags;
            });

            // prettier-ignore
            commandManager.registerArgument(OfflinePlayer.class, (sender, argument) ->
                Bukkit.getOfflinePlayer(argument)
            );

            commandManager.registerSuggestion(OfflinePlayer.class, (sender, arguments) ->
                Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList())
            );

            // Register world auto-suggest
            commandManager.registerSuggestion(SuggestionKey.of("worlds"), (sender, context) -> {
                List<String> worlds = new ArrayList<>();
                for (World world : loaderPlugin().getServer().getWorlds()) {
                    worlds.add(world.getName());
                }

                return worlds;
            });

            // Register "in" parameter
            commandManager.registerSuggestion(SuggestionKey.of("ins"), (sender, context) ->
                Arrays.asList("chunk", "world", "worldedit")
            );

            // Register help topics auto-suggest
            commandManager.registerSuggestion(SuggestionKey.of("help-topics"), (sender, context) ->
                Arrays.asList(
                    "main",
                    "commands",
                    "lookup",
                    "rollback",
                    "restore",
                    "params",
                    "parameters",
                    "purge",
                    "near",
                    "vault",
                    "wand",
                    "preview",
                    "drain",
                    "extinguish",
                    "teleport",
                    "tp"
                )
            );

            // Register time unit suggestions for better tab completion
            commandManager.registerSuggestion(SuggestionKey.of("time-examples"), (sender, context) ->
                Arrays.asList("1h", "30m", "1d", "12h", "1w", "2h30m")
            );

            // Register radius examples
            commandManager.registerSuggestion(SuggestionKey.of("radius-examples"), (sender, context) ->
                Arrays.asList("5", "10", "20", "50", "100")
            );

            // Register common player name suggestions (online players + common targets)
            commandManager.registerSuggestion(SuggestionKey.of("player-targets"), (sender, context) -> {
                List<String> suggestions = new ArrayList<>();
                suggestions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
                suggestions.add("#environment");
                suggestions.add("#fire");
                suggestions.add("#water");
                suggestions.add("#lava");
                return suggestions;
            });

            // Register wand mode suggestions
            commandManager.registerSuggestion(SuggestionKey.of("wand-modes"), (sender, context) ->
                Arrays.asList("inspect", "rollback", "restore")
            );
    }
    
    /**
     * Register command arguments.
     */
    private void registerCommandArguments(BukkitCommandManager<CommandSender> commandManager) {
            commandManager.registerFlags(
                FlagKey.of("query-flags"),
                Flag.flag("dl").longFlag("drainlava").argument(Boolean.class).build(),
                Flag.flag("ow").longFlag("overwrite").build(),
                Flag.flag("nd").longFlag("nodefaults").build(),
                Flag.flag("ng").longFlag("nogroup").build(),
                Flag.flag("rd").longFlag("removedrops").argument(Boolean.class).build()
            );

            commandManager.registerNamedArguments(
                ArgumentKey.of("query-parameters"),
                // Basic parameters
                Argument.forBoolean().name("reversed").build(),
                Argument.forInt().name("r").build(),
                Argument.forInt().name("radius").build(), // CoreProtect alias
                Argument.forString().name("in").suggestion(SuggestionKey.of("ins")).build(),
                
                // Time parameters
                Argument.forString().name("since").build(),
                Argument.forString().name("before").build(),
                Argument.forString().name("t").build(), // CoreProtect alias for time
                Argument.forString().name("time").build(), // CoreProtect alias
                
                // Cause/user parameters
                Argument.forString().name("c").build(),
                Argument.forString().name("world").suggestion(SuggestionKey.of("worlds")).build(),
                Argument.forString().name("w").build(), // Short alias for world
                
                // Location parameters
                Argument.forString().name("at").build(),
                Argument.forString().name("bounds").build(),
                
                // ID parameter
                Argument.listOf(Integer.class).name("id").build(),
                
                // Action parameters
                Argument.listOf(String.class).name("a").suggestion(SuggestionKey.of("actions")).build(),
                Argument.listOf(String.class).name("action").suggestion(SuggestionKey.of("actions")).build(), // CoreProtect alias
                
                // Block parameters
                Argument.listOf(String.class).name("b").suggestion(SuggestionKey.of("blocks")).build(),
                Argument.listOf(String.class).name("bc").suggestion(SuggestionKey.of("blocks")).build(),
                Argument.listOf(String.class).name("block").suggestion(SuggestionKey.of("blocks")).build(), // CoreProtect alias
                Argument.listOf(String.class).name("blocks").suggestion(SuggestionKey.of("blocks")).build(), // CoreProtect alias
                
                // Tag parameters
                Argument.listOf(String.class).name("btag").suggestion(SuggestionKey.of("blocktags")).build(),
                Argument.listOf(String.class).name("etag").suggestion(SuggestionKey.of("entitytypetags")).build(),
                Argument.listOf(String.class).name("itag").suggestion(SuggestionKey.of("itemtags")).build(),
                
                // Item parameters
                Argument.listOf(Material.class).name("i").build(),
                Argument.listOf(Material.class).name("item").build(), // CoreProtect alias
                Argument.listOf(Material.class).name("items").build(), // CoreProtect alias
                
                // Entity parameters
                Argument.listOf(EntityType.class).name("e").build(),
                Argument.listOf(EntityType.class).name("ec").build(),
                Argument.listOf(EntityType.class).name("entity").build(), // Alias
                
                // Player parameters
                Argument.listOf(OfflinePlayer.class).name("p").build(),
                Argument.listOf(OfflinePlayer.class).name("pc").build(),
                Argument.listOf(OfflinePlayer.class).name("pa").build(),
                Argument.listOf(OfflinePlayer.class).name("u").build(), // CoreProtect alias
                Argument.listOf(OfflinePlayer.class).name("user").build(), // CoreProtect alias
                Argument.listOf(OfflinePlayer.class).name("users").build(), // CoreProtect alias
                
                // Descriptor
                Argument.forString().name("descriptor").build()
            );
    }
    
    /**
     * Register commands.
     */
    private void registerCommands(BukkitCommandManager<CommandSender> commandManager) {
            commandManager.registerCommand(injectorProvider.injector().getInstance(AboutCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(CacheCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(ConfirmCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(ConfigsCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(DrainCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(ExtinguishCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(HelpCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(LookupCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(NearCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(PageCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(VaultCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(PreviewCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(PurgeCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(ReportCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(RestoreCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(RollbackCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(TeleportCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(WandCommand.class));
    }

    /**
     * Register an event.
     *
     * @param type The event class
     * @param <T> The type
     */
    protected <T> void registerEvent(Class<? extends Listener> type) {
        loaderPlugin()
            .getServer()
            .getPluginManager()
            .registerEvents(injectorProvider.injector().getInstance(type), loaderPlugin());
    }

    /**
     * Get the loader plugin.
     *
     * @return The loader
     */
    public PluginLoader loader() {
        return bootstrap.loader();
    }

    /**
     * Get the loader as a bukkit plugin.
     *
     * @return The loader as a bukkit plugin
     */
    public JavaPlugin loaderPlugin() {
        return (JavaPlugin) bootstrap.loader();
    }

    /**
     * Disable the plugin.
     */
    protected void disable() {
        Bukkit.getPluginManager().disablePlugin(loaderPlugin());

        threadPoolScheduler.shutdownScheduler();
        threadPoolScheduler.shutdownExecutor();

        if (recordingService != null) {
            recordingService.stop();
        }

        bootstrap.loggingService().error("Prism has to disable due to a fatal error.");
    }

    /**
     * On disable.
     */
    public void onDisable() {
        if (recordingService != null) {
            if (!recordingService.queue().isEmpty()) {
                loader()
                    .loggingService()
                    .warn(
                        "Server is shutting down yet there are {0} activities in the queue",
                        recordingService.queue().size()
                    );
            }

            recordingService.stop();
        }

        if (purgeService != null && !purgeService.queueFree()) {
            purgeService.stop();
        }

        if (storageAdapter != null) {
            storageAdapter.close();
        }
    }
}
