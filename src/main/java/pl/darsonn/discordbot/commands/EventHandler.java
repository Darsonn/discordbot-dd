package pl.darsonn.discordbot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import pl.darsonn.Main;
import pl.darsonn.discordbot.embedMessagesGenerator.EmbedMessageGenerator;
import pl.darsonn.discordbot.ticketsystem.TicketSystemListener;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class EventHandler extends ListenerAdapter {
    EmbedMessageGenerator embedMessageGenerator = new EmbedMessageGenerator();
    TicketSystemListener ticketSystemListener = new TicketSystemListener();
    private String[] systems = new String[] {"Rules", "Tickets", "Shop Info", "Status roles", "Links", "Price List", "WIP", "Partner Informations"};

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Member member = event.getMember();

        event.getGuild().addRoleToMember(member, Objects.requireNonNull(event.getJDA().getRoleById(Main.config.getDefaultMemberRoleID()))).queue();

        embedMessageGenerator.sendWelcomeMessage(Objects.requireNonNull(event.getGuild().getTextChannelById(Main.config.getWelcomeChannelID())), member);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) return;

        switch (event.getName()) {
            case "purge" -> purgeCommand(event);
            case "invite" -> embedMessageGenerator.sendInviteMessage(event);
            case "setup" -> setupCommand(event);
            case "changelog" -> changelogCommand(event);
            case "databaseoperations" -> databaseOperationsCommand(event);
            default -> event.reply("I can't handle that command right now :(").setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Button component = event.getComponent();

        if(Objects.requireNonNull(component.getId()).endsWith("-ticket")) {
            ticketSystemListener.interactionListener(event, component);
        } else if(Objects.requireNonNull(component.getId()).startsWith("requirements")) {
            switch (component.getId()) {
                case "requirements-adm" -> embedMessageGenerator.sendRequirements(event, "adm");
                case "requirements-dev" -> embedMessageGenerator.sendRequirements(event, "dev");
                case "requirements-tworca" -> embedMessageGenerator.sendRequirements(event, "tworca");
            }
        } else if(component.getId().equals("getInformationsChangelog")) {
            event.reply("(+) - oznacza dodanie nowych funkcjonalności\n" +
                    "(-) - oznacza usunięcie funkcjonalności\n" +
                    "(/) - oznacza poprawienie/zmianę funkcjonalności").setEphemeral(true).queue();
        } else {
            String[] id = event.getComponentId().split(":");
            String authorId = id[0];
            String type = id[1];

            if (!authorId.equals(event.getUser().getId()))
                return;
            event.deferEdit().queue();

            MessageChannel channel = event.getChannel();
            switch (type)
            {
                case "prune":
                    int amount = Integer.parseInt(id[2]);
                    event.getChannel().getIterableHistory()
                            .skipTo(event.getMessageIdLong())
                            .takeAsync(amount)
                            .thenAccept(channel::purgeMessages);
                case "delete":
                    event.getHook().deleteOriginal().queue();
            }
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if(event.getComponentId().equals("applyoption")) {
            ticketSystemListener.createApplyTicket(event, event.getValues().get(0));
        }
        switch(event.getComponentId()) {
            case "applyoption" -> ticketSystemListener.createApplyTicket(event, event.getValues().get(0));
            case "choose-message-pricelist" -> embedMessageGenerator.sendPriceListEmbedMessage(event);
            case "choose-open-positions" -> embedMessageGenerator.sendStatusRolesEmbedMessage(event);
            case "choose-operation-on-database" -> {
                StringBuilder message = new StringBuilder();
                for(int i = 0; i < event.getSelectedOptions().size(); i++) {
                    switch(event.getSelectedOptions().get(i).getValue()) {
                        case "checkconn" -> {
                            if(Main.databaseOperation.getConnection() != null) {
                                message.append("- Połączenie z bazą danych jest bez zarzutów!\n");
                            } else {
                                event.reply("Występuje problem połączenia z bazą danych. **Sprawdź logi!**").setEphemeral(true).queue();
                            }
                        }
                        case "cleardb" -> {
                            Main.databaseOperation.cleanDatabaseFromClosedTickets();
                            message.append("- Pomyślnie wyczyszczono bazę danych ze śmieci\n");
                        }
                        case "autoincrement" -> {
                            Main.databaseOperation.changeAutoIncrement(1);
                            message.append("- Pomyślnie ustawiono wartość autoincrement na " + 1 + "\n");
                        }
                    }
                }
                event.reply(message.toString()).setEphemeral(true).queue();

            }
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if(event.getName().equals("setup") && event.getFocusedOption().getName().equals("setup")) {
            List<Command.Choice> options = Stream.of(systems)
                    .filter(word -> word.startsWith(event.getFocusedOption().getValue()))
                    .map(word -> new Command.Choice(word, word))
                    .toList();
            event.replyChoices(options).queue();
        }
    }

    private void purgeCommand(SlashCommandInteractionEvent event) {
        OptionMapping amountOption = event.getOption("amount");
        int amount = amountOption == null
                ? 100 // default 100
                : (int) Math.min(200, Math.max(2, amountOption.getAsLong()));
        String userId = event.getUser().getId();
        event.reply("To usunie " + amount + " wiadomości.\nJesteś pewny?")
                .addActionRow(
                        Button.secondary(userId + ":delete", "Rezygnuję"),
                        Button.danger(userId + ":prune:" + amount, "Tak"))
                .queue();
    }

    private void changelogCommand(SlashCommandInteractionEvent event) {
        TextChannel textChannel = Objects.requireNonNull(event.getGuild()).getTextChannelById(Main.config.getChangelogChannelID());

        if(event.getMember().getRoles().contains(event.getGuild().getRoleById(Main.config.getDevTeamRoleID()))) {
            event.getChannel().getHistory().retrievePast(1)
                    .map(messages -> messages.get(0))
                    .queue(message -> {
                        if(!message.getAuthor().isBot()) {
                            Objects.requireNonNull(textChannel).sendMessage(Objects.requireNonNull(event.getJDA().getRoleById(Main.config.getChangelogRoleID())).getAsMention() + "\n\n" + message.getContentRaw())
                                    .setAllowedMentions(EnumSet.of(Message.MentionType.ROLE))
                                    .mentionRoles(Objects.requireNonNull(event.getJDA().getRoleById(Main.config.getChangelogRoleID())).getId())
                                    .addActionRow(
                                            Button.success("getInformationsChangelog", "Show informations about changelogs symbols meaning")
                                    )
                                    .queue();
                            event.reply("Wysłano wiadomość na <#" + Main.config.getChangelogChannelID() + ">!").setEphemeral(true).queue();
                        } else {
                            event.reply("Błąd! Nie znaleziono wiadomości do wysłania.").setEphemeral(true).queue();
                        }
                    });
        } else {
            event.reply("Nie posiadasz wymaganych permisji, aby wywołać to polecenie!").setEphemeral(true).queue();
        }
    }

    private void setupCommand(SlashCommandInteractionEvent event) {
        switch(Objects.requireNonNull(event.getOption("setup")).getAsString().toLowerCase()) {
            case "rules" -> {
                event.reply("Rules embed message sent!").setEphemeral(true).queue();
                embedMessageGenerator.sendRulesEmbedMessage(event);
            }
            case "tickets" -> {
                event.reply("Ticket panel has been created!").setEphemeral(true).queue();
                embedMessageGenerator.sendTicketPanelEmbedMessage(event);
            }
            case "shop info" -> {
                event.reply("Shop embed message sent!").setEphemeral(true).queue();
                embedMessageGenerator.sendShopEmbedMessage(event);
            }
            case "status roles" -> {
                event.reply("Choose open positions")
                        .addActionRow(
                                StringSelectMenu.create("choose-open-positions")
                                        .addOption("Administrator", "administrator")
                                        .addOption("Developer", "developer")
                                        .addOption("Creator", "creator")
                                        .addOption("None", "none")
                                        .setMaxValues(3)
                                        .build()
                        ).setEphemeral(true).queue();
            }
            case "links" -> {
                event.reply("Message with links sent!").setEphemeral(true).queue();
                embedMessageGenerator.sendLinksEmbedMessage(event);
            }
            case "price list" -> {
                event.reply("Choose type")
                        .addActionRow(
                                StringSelectMenu.create("choose-message-pricelist")
                                        .addOption("Fivem", "fivem", "Send message with Fivem price list")
                                        .addOption("Discord bot", "dcbot", "Send message with Discord bot price list")
                                        .addOption("Korepetycje", "korepetycje", "Send message with private lessons price list")
                                        .build()
                        ).setEphemeral(true).queue();
            }
            case "wip" -> embedMessageGenerator.sendWIPEmbedMessage(event);
            case "partner informations" -> {
                event.reply("Message with partners info sent!").setEphemeral(true).queue();
                embedMessageGenerator.sendPartnerInfo(event);
            }
        }
    }

    private void databaseOperationsCommand(SlashCommandInteractionEvent event) {
        event.reply("Choose operation")
                .addActionRow(
                        StringSelectMenu.create("choose-operation-on-database")
                                .addOption("Check database connection status", "checkconn", "Sprawdza poprawność połączenia z bazą danych")
                                .addOption("Clear database", "cleardb", "Czyści bazę danych ze zamkniętych ticketów")
                                .addOption("Change autoincrement value", "autoincrement", "Zmienia wartość autoincrement na 1")
                                .setMaxValues(3)
                                .build()
                ).setEphemeral(true).queue();
    }
}
