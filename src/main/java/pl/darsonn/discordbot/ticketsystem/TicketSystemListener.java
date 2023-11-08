package pl.darsonn.discordbot.ticketsystem;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import pl.darsonn.Main;
import pl.darsonn.discordbot.database.DatabaseOperation;
import pl.darsonn.discordbot.embedMessagesGenerator.EmbedMessageGenerator;

import java.sql.Timestamp;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class TicketSystemListener extends ListenerAdapter {
    TicketLogs ticketLogs = new TicketLogs();
    EmbedMessageGenerator embedMessageGenerator = new EmbedMessageGenerator();
    DatabaseOperation databaseOperation = new DatabaseOperation();

    public void interactionListener(ButtonInteractionEvent event, Button component) {
        switch (Objects.requireNonNull(component.getId())) {
            case "main-open-ticket" -> createTicket(event);
            case "shop-open-ticket" -> createShopTicket(event);
            case "apply-open-ticket" -> embedMessageGenerator.sendApplyOptionsMenu(event);
            //case "close-ticket" -> closeTicket(event);
            case "close-ticket" -> removeTicket(event);
        }
    }

    public void createApplyTicket(StringSelectInteractionEvent event, String option) {
        Category category = Objects.requireNonNull(event.getGuild()).getCategoryById(Main.config.getTicketSystemCategoryID());
        String textChannel = option + "-"+ Objects.requireNonNull(event.getMember()).getEffectiveName();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        if(event.getJDA().getTextChannelsByName(textChannel, true).isEmpty()) {
            ChannelAction<TextChannel> channelAction = Objects.requireNonNull(category).createTextChannel(textChannel);
            channelAction.addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null).queue();
            channelAction.queue(channel -> {
                String channelID = channel.getId();
                TextChannel ticket = event.getJDA().getTextChannelById(channelID);
                ticketLogs.createTicket(event.getMember(), channelID, timestamp);
                databaseOperation.createTicket(event.getMember(), "apply"+option, channel, timestamp);
                embedMessageGenerator.sendPanelInTicket(ticket, event.getMember(), "apply"+option);
                event.reply("Utworzono ticket " + channel.getAsMention()).setEphemeral(true).queue();
            });
        } else {
            event.reply("Nie możesz utworzyć nowego ticketa, najpierw musisz usunąć poprzedni!").setEphemeral(true).queue();
        }
    }

    public void createShopTicket(ButtonInteractionEvent event) {
        Category category = Objects.requireNonNull(event.getGuild()).getCategoryById(Main.config.getTicketSystemCategoryID());
        String textChannel = "shop-"+ Objects.requireNonNull(event.getMember()).getEffectiveName();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        if(event.getJDA().getTextChannelsByName(textChannel, true).isEmpty()) {
            ChannelAction<TextChannel> channelAction = Objects.requireNonNull(category).createTextChannel(textChannel);
            channelAction.addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null).queue();
            channelAction.queue(channel -> {
                String channelID = channel.getId();
                databaseOperation.createTicket(event.getMember(), "shop", channel, timestamp);
                sendPanelInTicket(event, channelID, "shop", timestamp);
                event.reply("Utworzono ticket " + channel.getAsMention()).setEphemeral(true).queue();
            });
        } else {
            event.reply("Nie możesz utworzyć nowego ticketa, najpierw musisz usunąć poprzedni!").setEphemeral(true).queue();
        }
    }

    public void createTicket(ButtonInteractionEvent event) {
        Category category = Objects.requireNonNull(event.getGuild()).getCategoryById(Main.config.getTicketSystemCategoryID());
        String textChannel = "ticket-"+ Objects.requireNonNull(event.getMember()).getEffectiveName();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        if(event.getJDA().getTextChannelsByName(textChannel, true).isEmpty()) {
            ChannelAction<TextChannel> channelAction = Objects.requireNonNull(category).createTextChannel(textChannel);
            channelAction.addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null).queue();
            channelAction.queue(channel -> {
                String channelID = channel.getId();
                databaseOperation.createTicket(event.getMember(), "ticket", channel, timestamp);
                sendPanelInTicket(event, channelID, "ticket", timestamp);
                event.reply("Utworzono ticket " + channel.getAsMention()).setEphemeral(true).queue();
            });
        } else {
            event.reply("Nie możesz utworzyć nowego ticketa, najpierw musisz usunąć poprzedni!").setEphemeral(true).queue();
        }
    }

    public void sendPanelInTicket(ButtonInteractionEvent event, String channelID, String ticketType, Timestamp timestamp) {
        TextChannel ticket = event.getJDA().getTextChannelById(channelID);
        ticketLogs.createTicket(Objects.requireNonNull(event.getMember()), channelID, timestamp);
        embedMessageGenerator.sendPanelInTicket(ticket, event.getMember(), ticketType);
    }

    public void closeTicket(ButtonInteractionEvent event) {
        TextChannel channel = event.getChannel().asTextChannel();
        event.reply("Zamknięto ticket <#"+channel.getId()+">").setEphemeral(true).queue();
        String openerID = databaseOperation.getTicketOpener(channel.getId());
        Member member = Objects.requireNonNull(event.getGuild()).getMemberById(openerID);          // TODO: DO POPRAWY
        channel.getManager().removePermissionOverride(Objects.requireNonNull(member)).queue();

        ticketLogs.closeTicket(Objects.requireNonNull(event.getMember()), channel.getId());
        embedMessageGenerator.sendPanelStaffAfterClosingTicket(channel, member);
    }

    public void removeTicket(ButtonInteractionEvent event) {
        TextChannel channel = event.getChannel().asTextChannel();

        channel.delete().reason("Ticket closed.").queue();

        ticketLogs.deleteTicket(Objects.requireNonNull(event.getMember()), channel.getId());
    }
}
