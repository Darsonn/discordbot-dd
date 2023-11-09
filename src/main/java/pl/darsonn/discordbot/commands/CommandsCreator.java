package pl.darsonn.discordbot.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class CommandsCreator {
    public void createCommands(JDA builder) {
        CommandListUpdateAction commands = builder.updateCommands();

        commands.addCommands(
                Commands.slash("purge", "Usuwa określoną ilość wiadomości")
                        .addOption(INTEGER, "amount", "How many messages to prune (Default 100)")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE))
        );

        commands.addCommands(
                Commands.slash("invite", "Zaproszenie na serwer")
                        .setGuildOnly(false)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND))
        );

        commands.addCommands(
                Commands.slash("setup", "Ustawia odpowiedni system")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                        .addOption(STRING, "setup", "Wybierz system", true, true)
        );

        commands.addCommands(
                Commands.slash("changelog", "Wysyła wiadomość z nowościami na kanale changelog")
                        .setGuildOnly(false)
        );

        commands.addCommands(
                Commands.slash("databaseoperations", "Operacje na bazie danych")
                        .setGuildOnly(false)
                        .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
        );

        commands.queue();
    }
}
