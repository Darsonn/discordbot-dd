package pl.darsonn.discordbot.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class CommandsCreator {
    public void createCommands(JDA builder) {
        CommandListUpdateAction commands = builder.updateCommands();

        commands.addCommands(
                Commands.slash("sendrules", "Wysyła regulamin na określony kanał")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
        );

        commands.addCommands(
                Commands.slash("setupticket", "Utwórz panel do systemu ticket")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
        );

        commands.addCommands(
                Commands.slash("sendshopinfo", "Wysyła informacje na temat zamówień")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
        );

        commands.addCommands(
                Commands.slash("sendstatusroles", "Wysyła wiadomość z aktualnymi statusami rekrutacji")
                        .addOption(BOOLEAN, "administrator", "Status of role Administrator", true)
                        .addOption(BOOLEAN, "developer", "Status of role Developer", true)
                        .addOption(BOOLEAN, "creator", "Status of role Creator", true)
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
        );

        commands.addCommands(
                Commands.slash("sendlinkmessage", "Wysyła wiadomość z przydatnymi linkami")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
        );

        commands.addCommands(
                Commands.slash("purge", "Usuwa określoną ilość wiadomości")
                        .addOption(INTEGER, "amount", "How many messages to prune (Default 100)")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE))
        );

        commands.addCommands(
                Commands.slash("sendpricelist", "Wysyła wiadomość z cenami za usługi")
                        .addOptions(
                                new OptionData(STRING, "type", "Typ cennika")
                                        .addChoice("Fivem", "fivem")
                                        .addChoice("Discord Bot", "dcbot")
                                        .addChoice("Korepetycje", "korepetycje")
                                        .setRequired(true)
                        )
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
        );

        commands.addCommands(
                Commands.slash("sendwip", "Wysyła wiadomość work in progress")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
        );

        commands.addCommands(
                Commands.slash("sendpartnerinfo", "Wysyła wiadomość partner informations")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
        );

        commands.queue();
    }
}
