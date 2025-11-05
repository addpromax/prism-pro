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
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.prism_mc.prism.paper.services.messages.MessageService;
import org.prism_mc.prism.paper.services.translation.PaperTranslationService;

@Command(value = "prism", alias = { "pr" })
public class HelpCommand {

    /**
     * The translation service.
     */
    private final PaperTranslationService translationService;

    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * Construct the help command.
     *
     * @param translationService The translation service
     * @param messageService The message service
     */
    @Inject
    public HelpCommand(PaperTranslationService translationService, MessageService messageService) {
        this.translationService = translationService;
        this.messageService = messageService;
    }

    /**
     * Show help information.
     *
     * @param sender The command sender
     * @param topic Optional help topic
     */
    @Command(value = "help", alias = { "h", "?" })
    @Permission("prism.help")
    public void onHelp(final CommandSender sender, @Optional @Suggestion("help-topics") String topic) {
        // Default to "main" if no topic provided
        if (topic == null || topic.isEmpty()) {
            topic = "main";
        }
        String normalizedTopic = topic.toLowerCase().trim();

        switch (normalizedTopic) {
            case "main":
            case "commands":
                showMainHelp(sender);
                break;
            case "lookup":
            case "l":
                showLookupHelp(sender);
                break;
            case "rollback":
            case "rb":
                showRollbackHelp(sender);
                break;
            case "restore":
            case "rs":
                showRestoreHelp(sender);
                break;
            case "params":
            case "parameters":
                showParametersHelp(sender);
                break;
            case "purge":
                showPurgeHelp(sender);
                break;
            case "near":
                showNearHelp(sender);
                break;
            case "vault":
            case "v":
                showVaultHelp(sender);
                break;
            case "wand":
            case "w":
                showWandHelp(sender);
                break;
            case "preview":
            case "pv":
                showPreviewHelp(sender);
                break;
            case "drain":
                showDrainHelp(sender);
                break;
            case "extinguish":
                showExtinguishHelp(sender);
                break;
            case "teleport":
            case "tp":
                showTeleportHelp(sender);
                break;
            default:
                messageService.helpUnknownTopic(sender, topic);
        }
    }

    /**
     * Show main help page.
     *
     * @param sender The command sender
     */
    private void showMainHelp(CommandSender sender) {
        String header = msg("prism.help.main.header", sender);
        sender.sendMessage(Component.text("───── ", NamedTextColor.WHITE)
            .append(Component.text(header, NamedTextColor.DARK_AQUA, TextDecoration.BOLD))
            .append(Component.text(" ─────", NamedTextColor.WHITE)));

        sendHelpLine(sender, msg("prism.help.command.help", sender), msg("prism.help.command.help.desc", sender), "/prism help params");
        sendHelpLine(sender, msg("prism.help.command.lookup", sender), msg("prism.help.command.lookup.desc", sender), "/prism help lookup");
        sendHelpLine(sender, msg("prism.help.command.rollback", sender), msg("prism.help.command.rollback.desc", sender), "/prism help rollback");
        sendHelpLine(sender, msg("prism.help.command.restore", sender), msg("prism.help.command.restore.desc", sender), "/prism help restore");
        sendHelpLine(sender, msg("prism.help.command.near", sender), msg("prism.help.command.near.desc", sender), "/prism help near");
        sendHelpLine(sender, msg("prism.help.command.vault", sender), msg("prism.help.command.vault.desc", sender), "/prism help vault");
        sendHelpLine(sender, msg("prism.help.command.wand", sender), msg("prism.help.command.wand.desc", sender), "/prism help wand");
        sendHelpLine(sender, msg("prism.help.command.preview", sender), msg("prism.help.command.preview.desc", sender), "/prism help preview");
        sendHelpLine(sender, msg("prism.help.command.drain", sender), msg("prism.help.command.drain.desc", sender), "/prism help drain");
        sendHelpLine(sender, msg("prism.help.command.extinguish", sender), msg("prism.help.command.extinguish.desc", sender), "/prism help extinguish");
        sendHelpLine(sender, msg("prism.help.command.purge", sender), msg("prism.help.command.purge.desc", sender), "/prism help purge");
        sendHelpLine(sender, msg("prism.help.command.teleport", sender), msg("prism.help.command.teleport.desc", sender), "/prism help teleport");
        sendHelpLine(sender, msg("prism.help.command.page", sender), msg("prism.help.command.page.desc", sender), null);
        sendHelpLine(sender, msg("prism.help.command.about", sender), msg("prism.help.command.about.desc", sender), null);

        String tip = msg("prism.help.main.tip", sender);
        sender.sendMessage(Component.text(tip));
    }

    /**
     * Show lookup command help.
     *
     * @param sender The command sender
     */
    private void showLookupHelp(CommandSender sender) {
        String header = msg("prism.help.lookup.header", sender);
        sender.sendMessage(Component.text("───── ", NamedTextColor.WHITE)
            .append(Component.text(header, NamedTextColor.DARK_AQUA, TextDecoration.BOLD))
            .append(Component.text(" ─────", NamedTextColor.WHITE)));

        sender.sendMessage(Component.text()
            .append(Component.text(msg("prism.help.lookup.usage", sender) + ": ", NamedTextColor.AQUA))
            .append(Component.text(msg("prism.help.lookup.usage.text", sender), NamedTextColor.GRAY))
            .build());

        sender.sendMessage(Component.text()
            .append(Component.text(msg("prism.help.lookup.aliases", sender) + ": ", NamedTextColor.AQUA))
            .append(Component.text(msg("prism.help.lookup.aliases.text", sender), NamedTextColor.GRAY))
            .build());

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(msg("prism.help.lookup.description", sender) + ":", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("  " + msg("prism.help.lookup.description.text", sender), NamedTextColor.WHITE));

        sender.sendMessage(Component.empty());
        String seeParams = msg("prism.help.see-params", sender).replace("<command>", "");
        sender.sendMessage(Component.text()
            .append(Component.text(seeParams, NamedTextColor.YELLOW))
            .append(createClickableCommand("/prism help params", NamedTextColor.AQUA))
            .build());
    }

    /**
     * Show rollback command help.
     *
     * @param sender The command sender
     */
    private void showRollbackHelp(CommandSender sender) {
        String header = msg("prism.help.rollback.header", sender);
        sender.sendMessage(Component.text("───── ", NamedTextColor.WHITE)
            .append(Component.text(header, NamedTextColor.DARK_AQUA, TextDecoration.BOLD))
            .append(Component.text(" ─────", NamedTextColor.WHITE)));

        sender.sendMessage(Component.text()
            .append(Component.text(msg("prism.help.lookup.usage", sender) + ": ", NamedTextColor.AQUA))
            .append(Component.text(msg("prism.help.rollback.usage.text", sender), NamedTextColor.GRAY))
            .build());

        sender.sendMessage(Component.text()
            .append(Component.text(msg("prism.help.lookup.aliases", sender) + ": ", NamedTextColor.AQUA))
            .append(Component.text(msg("prism.help.rollback.aliases.text", sender), NamedTextColor.GRAY))
            .build());

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(msg("prism.help.lookup.description", sender) + ":", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("  " + msg("prism.help.rollback.description.text", sender), NamedTextColor.WHITE));
        sender.sendMessage(Component.text("  " + msg("prism.help.rollback.note", sender), NamedTextColor.YELLOW));

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(msg("prism.help.rollback.flags", sender) + ":", NamedTextColor.AQUA));
        sendFlagInfo(sender, msg("prism.help.rollback.flag.nodefaults", sender), msg("prism.help.rollback.flag.nodefaults.desc", sender));
        sendFlagInfo(sender, msg("prism.help.rollback.flag.nogroup", sender), msg("prism.help.rollback.flag.nogroup.desc", sender));
        sendFlagInfo(sender, msg("prism.help.rollback.flag.overwrite", sender), msg("prism.help.rollback.flag.overwrite.desc", sender));

        sender.sendMessage(Component.empty());
        String seeParams = msg("prism.help.see-params", sender).replace("<command>", "");
        sender.sendMessage(Component.text()
            .append(Component.text(seeParams, NamedTextColor.YELLOW))
            .append(createClickableCommand("/prism help params", NamedTextColor.AQUA))
            .build());
    }

    /**
     * Show restore command help.
     *
     * @param sender The command sender
     */
    private void showRestoreHelp(CommandSender sender) {
        String header = msg("prism.help.restore.header", sender);
        sender.sendMessage(Component.text("───── ", NamedTextColor.WHITE)
            .append(Component.text(header, NamedTextColor.DARK_AQUA, TextDecoration.BOLD))
            .append(Component.text(" ─────", NamedTextColor.WHITE)));

        sender.sendMessage(Component.text()
            .append(Component.text(msg("prism.help.lookup.usage", sender) + ": ", NamedTextColor.AQUA))
            .append(Component.text(msg("prism.help.restore.usage.text", sender), NamedTextColor.GRAY))
            .build());

        sender.sendMessage(Component.text()
            .append(Component.text(msg("prism.help.lookup.aliases", sender) + ": ", NamedTextColor.AQUA))
            .append(Component.text(msg("prism.help.restore.aliases.text", sender), NamedTextColor.GRAY))
            .build());

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(msg("prism.help.lookup.description", sender) + ":", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("  " + msg("prism.help.restore.description.text", sender), NamedTextColor.WHITE));
        sender.sendMessage(Component.text("  " + msg("prism.help.restore.description.note", sender), NamedTextColor.GRAY));

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(msg("prism.help.restore.params.note", sender)));
        
        String seeParams = msg("prism.help.see-params", sender).replace("<command>", "");
        sender.sendMessage(Component.text()
            .append(Component.text(seeParams, NamedTextColor.YELLOW))
            .append(createClickableCommand("/prism help params", NamedTextColor.AQUA))
            .build());
    }

    /**
     * Show parameters help.
     *
     * @param sender The command sender
     */
    private void showParametersHelp(CommandSender sender) {
        String header = msg("prism.help.params.header", sender);
        sender.sendMessage(Component.text("───── ", NamedTextColor.WHITE)
            .append(Component.text(header, NamedTextColor.DARK_AQUA, TextDecoration.BOLD))
            .append(Component.text(" ─────", NamedTextColor.WHITE)));

        sender.sendMessage(Component.text(msg("prism.help.params.intro", sender), NamedTextColor.WHITE));
        sender.sendMessage(Component.empty());

        // Player parameters
        sendParamInfoWithAliases(sender, "p:<玩家名>", msg("prism.help.params.player.desc", sender), 
            msg("prism.help.params.player.example", sender), msg("prism.help.params.player.aliases", sender));
        
        // Time parameters
        sendParamInfoWithAliases(sender, "since:<时间>", msg("prism.help.params.since.desc", sender), 
            msg("prism.help.params.since.example", sender), msg("prism.help.params.since.aliases", sender));
        sendParamInfo(sender, "before:<时间>", msg("prism.help.params.before.desc", sender), 
            msg("prism.help.params.before.example", sender));
        
        // Radius parameters
        sendParamInfoWithAliases(sender, "r:<半径>", msg("prism.help.params.radius.desc", sender), 
            msg("prism.help.params.radius.example", sender), msg("prism.help.params.radius.aliases", sender));
        
        // Action parameters
        sendParamInfoWithAliases(sender, "a:<动作>", msg("prism.help.params.action.desc", sender), 
            msg("prism.help.params.action.example", sender), msg("prism.help.params.action.aliases", sender));
        
        // Block parameters
        sendParamInfoWithAliases(sender, "b:<方块>", msg("prism.help.params.block.desc", sender), 
            msg("prism.help.params.block.example", sender), msg("prism.help.params.block.aliases", sender));
        sendParamInfo(sender, "btag:<标签>", msg("prism.help.params.btag.desc", sender), 
            msg("prism.help.params.btag.example", sender));
        
        // Entity parameters
        sendParamInfoWithAliases(sender, "e:<实体>", msg("prism.help.params.entity.desc", sender), 
            msg("prism.help.params.entity.example", sender), msg("prism.help.params.entity.aliases", sender));
        sendParamInfo(sender, "etag:<标签>", msg("prism.help.params.etag.desc", sender), 
            msg("prism.help.params.etag.example", sender));
        
        // Item parameters
        sendParamInfoWithAliases(sender, "i:<物品>", msg("prism.help.params.item.desc", sender), 
            msg("prism.help.params.item.example", sender), msg("prism.help.params.item.aliases", sender));
        sendParamInfo(sender, "itag:<标签>", msg("prism.help.params.itag.desc", sender), 
            msg("prism.help.params.itag.example", sender));
        
        // World and location parameters
        sendParamInfoWithAliases(sender, "world:<世界>", msg("prism.help.params.world.desc", sender), 
            msg("prism.help.params.world.example", sender), msg("prism.help.params.world.aliases", sender));
        sendParamInfo(sender, "at:<坐标>", msg("prism.help.params.at.desc", sender), 
            msg("prism.help.params.at.example", sender));
        sendParamInfo(sender, "bounds:<范围>", msg("prism.help.params.bounds.desc", sender), 
            msg("prism.help.params.bounds.example", sender));
        sendParamInfo(sender, "in:<范围类型>", msg("prism.help.params.in.desc", sender), 
            msg("prism.help.params.in.example", sender));

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(msg("prism.help.params.time-format", sender) + ":", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("  " + msg("prism.help.params.time-format.desc", sender), NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  " + msg("prism.help.params.time-format.example", sender), NamedTextColor.GRAY));
        
        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(msg("prism.help.params.coreprotect", sender)));
        sender.sendMessage(Component.text("  " + msg("prism.help.params.coreprotect.desc1", sender), NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  " + msg("prism.help.params.coreprotect.desc2", sender), NamedTextColor.GRAY));
    }

    /**
     * Show purge command help.
     *
     * @param sender The command sender
     */
    private void showPurgeHelp(CommandSender sender) {
        String header = msg("prism.help.purge.header", sender);
        sender.sendMessage(Component.text("───── ", NamedTextColor.WHITE)
            .append(Component.text(header, NamedTextColor.DARK_AQUA, TextDecoration.BOLD))
            .append(Component.text(" ─────", NamedTextColor.WHITE)));

        sender.sendMessage(Component.text()
            .append(Component.text(msg("prism.help.lookup.usage", sender) + ": ", NamedTextColor.AQUA))
            .append(Component.text(msg("prism.help.purge.usage.text", sender), NamedTextColor.GRAY))
            .build());

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(msg("prism.help.lookup.description", sender) + ":", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("  " + msg("prism.help.purge.description.text", sender), NamedTextColor.WHITE));
        sender.sendMessage(Component.text("  " + msg("prism.help.purge.warning", sender)));
        
        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text("  " + msg("prism.help.purge.supported-params", sender), NamedTextColor.GRAY));
    }

    /**
     * Show near command help.
     *
     * @param sender The command sender
     */
    private void showNearHelp(CommandSender sender) {
        String header = msg("prism.help.near.header", sender);
        sender.sendMessage(Component.text("───── ", NamedTextColor.WHITE)
            .append(Component.text(header, NamedTextColor.DARK_AQUA, TextDecoration.BOLD))
            .append(Component.text(" ─────", NamedTextColor.WHITE)));

        sender.sendMessage(Component.text()
            .append(Component.text(msg("prism.help.lookup.usage", sender) + ": ", NamedTextColor.AQUA))
            .append(Component.text(msg("prism.help.near.usage.text", sender), NamedTextColor.GRAY))
            .build());

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(msg("prism.help.lookup.description", sender) + ":", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("  " + msg("prism.help.near.description.text", sender), NamedTextColor.WHITE));
    }

    /**
     * Show vault command help.
     *
     * @param sender The command sender
     */
    private void showVaultHelp(CommandSender sender) {
        String header = msg("prism.help.vault.header", sender);
        sender.sendMessage(Component.text("───── ", NamedTextColor.WHITE)
            .append(Component.text(header, NamedTextColor.DARK_AQUA, TextDecoration.BOLD))
            .append(Component.text(" ─────", NamedTextColor.WHITE)));

        sender.sendMessage(Component.text()
            .append(Component.text(msg("prism.help.lookup.usage", sender) + ": ", NamedTextColor.AQUA))
            .append(Component.text(msg("prism.help.vault.usage.text", sender), NamedTextColor.GRAY))
            .build());

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(msg("prism.help.lookup.description", sender) + ":", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("  " + msg("prism.help.vault.description.text", sender), NamedTextColor.WHITE));
        
        sender.sendMessage(Component.empty());
        String seeParams = msg("prism.help.see-params", sender).replace("<command>", "");
        sender.sendMessage(Component.text()
            .append(Component.text(seeParams, NamedTextColor.YELLOW))
            .append(createClickableCommand("/prism help params", NamedTextColor.AQUA))
            .build());
    }

    /**
     * Show wand command help.
     *
     * @param sender The command sender
     */
    private void showWandHelp(CommandSender sender) {
        String header = msg("prism.help.wand.header", sender);
        sender.sendMessage(Component.text("───── ", NamedTextColor.WHITE)
            .append(Component.text(header, NamedTextColor.DARK_AQUA, TextDecoration.BOLD))
            .append(Component.text(" ─────", NamedTextColor.WHITE)));

        sender.sendMessage(Component.text()
            .append(Component.text(msg("prism.help.lookup.usage", sender) + ": ", NamedTextColor.AQUA))
            .append(Component.text(msg("prism.help.wand.usage.text", sender), NamedTextColor.GRAY))
            .build());

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(msg("prism.help.lookup.description", sender) + ":", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("  " + msg("prism.help.wand.description.text", sender), NamedTextColor.WHITE));

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(msg("prism.help.wand.modes", sender) + ":", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("  " + msg("prism.help.wand.mode.inspect", sender), NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  " + msg("prism.help.wand.mode.rollback", sender), NamedTextColor.GRAY));
        sender.sendMessage(Component.text("  " + msg("prism.help.wand.mode.restore", sender), NamedTextColor.GRAY));
    }

    /**
     * Show preview command help.
     *
     * @param sender The command sender
     */
    private void showPreviewHelp(CommandSender sender) {
        String header = msg("prism.help.preview.header", sender);
        sender.sendMessage(Component.text("───── ", NamedTextColor.WHITE)
            .append(Component.text(header, NamedTextColor.DARK_AQUA, TextDecoration.BOLD))
            .append(Component.text(" ─────", NamedTextColor.WHITE)));

        sender.sendMessage(Component.text()
            .append(Component.text(msg("prism.help.lookup.usage", sender) + ": ", NamedTextColor.AQUA))
            .append(Component.text(msg("prism.help.preview.usage.text", sender), NamedTextColor.GRAY))
            .build());

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(msg("prism.help.lookup.description", sender) + ":", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("  " + msg("prism.help.preview.description.text", sender), NamedTextColor.WHITE));
        sender.sendMessage(Component.text("  " + msg("prism.help.preview.description.note", sender), NamedTextColor.GRAY));
    }

    /**
     * Show drain command help.
     *
     * @param sender The command sender
     */
    private void showDrainHelp(CommandSender sender) {
        String header = msg("prism.help.drain.header", sender);
        sender.sendMessage(Component.text("───── ", NamedTextColor.WHITE)
            .append(Component.text(header, NamedTextColor.DARK_AQUA, TextDecoration.BOLD))
            .append(Component.text(" ─────", NamedTextColor.WHITE)));

        sender.sendMessage(Component.text()
            .append(Component.text(msg("prism.help.lookup.usage", sender) + ": ", NamedTextColor.AQUA))
            .append(Component.text(msg("prism.help.drain.usage.text", sender), NamedTextColor.GRAY))
            .build());

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(msg("prism.help.lookup.description", sender) + ":", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("  " + msg("prism.help.drain.description.text", sender), NamedTextColor.WHITE));
        sender.sendMessage(Component.text("  " + msg("prism.help.drain.note", sender), NamedTextColor.GRAY));
    }

    /**
     * Show extinguish command help.
     *
     * @param sender The command sender
     */
    private void showExtinguishHelp(CommandSender sender) {
        String header = msg("prism.help.extinguish.header", sender);
        sender.sendMessage(Component.text("───── ", NamedTextColor.WHITE)
            .append(Component.text(header, NamedTextColor.DARK_AQUA, TextDecoration.BOLD))
            .append(Component.text(" ─────", NamedTextColor.WHITE)));

        sender.sendMessage(Component.text()
            .append(Component.text(msg("prism.help.lookup.usage", sender) + ": ", NamedTextColor.AQUA))
            .append(Component.text(msg("prism.help.extinguish.usage.text", sender), NamedTextColor.GRAY))
            .build());

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(msg("prism.help.lookup.description", sender) + ":", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("  " + msg("prism.help.extinguish.description.text", sender), NamedTextColor.WHITE));
        sender.sendMessage(Component.text("  " + msg("prism.help.extinguish.note", sender), NamedTextColor.GRAY));
    }

    /**
     * Show teleport command help.
     *
     * @param sender The command sender
     */
    private void showTeleportHelp(CommandSender sender) {
        String header = msg("prism.help.teleport.header", sender);
        sender.sendMessage(Component.text("───── ", NamedTextColor.WHITE)
            .append(Component.text(header, NamedTextColor.DARK_AQUA, TextDecoration.BOLD))
            .append(Component.text(" ─────", NamedTextColor.WHITE)));

        sender.sendMessage(Component.text()
            .append(Component.text(msg("prism.help.lookup.usage", sender) + ": ", NamedTextColor.AQUA))
            .append(Component.text(msg("prism.help.teleport.usage.text", sender), NamedTextColor.GRAY))
            .build());

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(msg("prism.help.lookup.description", sender) + ":", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("  " + msg("prism.help.teleport.description.text", sender), NamedTextColor.WHITE));
    }

    /**
     * Send a help line with clickable command.
     *
     * @param sender The command sender
     * @param command The command
     * @param description The description
     * @param helpCommand Optional help command for click action
     */
    private void sendHelpLine(CommandSender sender, String command, String description, String helpCommand) {
        Component commandComponent;
        if (helpCommand != null) {
            String hoverText = msg("prism.help.hover.click-for-help", sender);
            commandComponent = Component.text(command, NamedTextColor.DARK_AQUA)
                .clickEvent(ClickEvent.runCommand(helpCommand))
                .hoverEvent(HoverEvent.showText(Component.text(hoverText, NamedTextColor.GRAY)));
        } else {
            String hoverText = msg("prism.help.hover.click-to-fill", sender);
            commandComponent = Component.text(command, NamedTextColor.DARK_AQUA)
                .clickEvent(ClickEvent.suggestCommand(command))
                .hoverEvent(HoverEvent.showText(Component.text(hoverText, NamedTextColor.GRAY)));
        }

        sender.sendMessage(Component.text()
            .append(commandComponent)
            .append(Component.text(" - ", NamedTextColor.WHITE))
            .append(Component.text(description, NamedTextColor.GRAY))
            .build());
    }

    /**
     * Send an example line.
     *
     * @param sender The command sender
     * @param command The example command
     * @param description The description
     */
    private void sendExample(CommandSender sender, String command, String description) {
        String hoverText = msg("prism.help.hover.click-to-fill", sender);
        sender.sendMessage(Component.text()
            .append(Component.text("  • ", NamedTextColor.DARK_GRAY))
            .append(Component.text(command, NamedTextColor.YELLOW)
                .clickEvent(ClickEvent.suggestCommand(command))
                .hoverEvent(HoverEvent.showText(Component.text(hoverText, NamedTextColor.GRAY))))
            .append(Component.text(" - ", NamedTextColor.WHITE))
            .append(Component.text(description, NamedTextColor.GRAY))
            .build());
    }

    /**
     * Send parameter info.
     *
     * @param sender The command sender
     * @param param The parameter
     * @param description The description
     * @param example The example
     */
    private void sendParamInfo(CommandSender sender, String param, String description, String example) {
        sender.sendMessage(Component.text()
            .append(Component.text("  ", NamedTextColor.DARK_GRAY))
            .append(Component.text(param, NamedTextColor.YELLOW))
            .append(Component.text(" - ", NamedTextColor.WHITE))
            .append(Component.text(description, NamedTextColor.GRAY))
            .build());

        String exampleLabel = msg("prism.help.label.example", sender);
        sender.sendMessage(Component.text()
            .append(Component.text("    " + exampleLabel + ": ", NamedTextColor.DARK_GRAY))
            .append(Component.text(example, NamedTextColor.DARK_AQUA))
            .build());
    }

    /**
     * Send parameter info with aliases.
     *
     * @param sender The command sender
     * @param param The parameter
     * @param description The description
     * @param example The example
     * @param aliases The parameter aliases
     */
    private void sendParamInfoWithAliases(CommandSender sender, String param, String description, String example, String aliases) {
        sender.sendMessage(Component.text()
            .append(Component.text("  ", NamedTextColor.DARK_GRAY))
            .append(Component.text(param, NamedTextColor.YELLOW))
            .append(Component.text(" - ", NamedTextColor.WHITE))
            .append(Component.text(description, NamedTextColor.GRAY))
            .build());

        String aliasLabel = msg("prism.help.label.aliases", sender);
        sender.sendMessage(Component.text()
            .append(Component.text("    " + aliasLabel + ": ", NamedTextColor.DARK_GRAY))
            .append(Component.text(aliases, NamedTextColor.GOLD))
            .build());

        String exampleLabel = msg("prism.help.label.example", sender);
        sender.sendMessage(Component.text()
            .append(Component.text("    " + exampleLabel + ": ", NamedTextColor.DARK_GRAY))
            .append(Component.text(example, NamedTextColor.DARK_AQUA))
            .build());
    }

    /**
     * Send flag info.
     *
     * @param sender The command sender
     * @param flag The flag
     * @param description The description
     */
    private void sendFlagInfo(CommandSender sender, String flag, String description) {
        sender.sendMessage(Component.text()
            .append(Component.text("  ", NamedTextColor.DARK_GRAY))
            .append(Component.text(flag, NamedTextColor.YELLOW))
            .append(Component.text(" - ", NamedTextColor.WHITE))
            .append(Component.text(description, NamedTextColor.GRAY))
            .build());
    }

    /**
     * Create a clickable command component.
     *
     * @param command The command
     * @param color The color
     * @return The component
     */
    private Component createClickableCommand(String command, NamedTextColor color) {
        String hoverText = translationService.messageOf(null, "prism.help.hover.click-to-execute");
        if (hoverText == null || hoverText.isEmpty()) {
            hoverText = "Click to execute";
        }
        return Component.text(command, color)
            .clickEvent(ClickEvent.runCommand(command))
            .hoverEvent(HoverEvent.showText(Component.text(hoverText, NamedTextColor.GRAY)));
    }

    /**
     * Get a localized message.
     *
     * @param key The message key
     * @param sender The command sender
     * @return The localized message
     */
    private String msg(String key, CommandSender sender) {
        String message = translationService.messageOf(sender, key);
        return message != null ? message : key;
    }

    /**
     * Get a localized message (without sender context).
     *
     * @param key The message key
     * @return The localized message
     */
    private String msg(String key) {
        String message = translationService.messageOf(null, key);
        return message != null ? message : key;
    }
}

