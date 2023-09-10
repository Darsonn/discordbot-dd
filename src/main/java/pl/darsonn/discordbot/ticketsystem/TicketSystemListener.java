package pl.darsonn.discordbot.ticketsystem;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import pl.darsonn.discordbot.Main;
import pl.darsonn.discordbot.embedMessagesGenerator.EmbedMessageGenerator;

import java.util.EnumSet;

public class TicketSystemListener extends ListenerAdapter {
    TicketLogs ticketLogs = new TicketLogs();

    EmbedMessageGenerator embedMessageGenerator = new EmbedMessageGenerator();

    public void interactionListener(ButtonInteractionEvent event, Button component) {
        switch (component.getId()) {
            case "main-open-ticket" -> createTicket(event);
            case "shop-open-ticket" -> createShopTicket(event);
            case "apply-open-ticket" -> embedMessageGenerator.sendApplyOptionsMenu(event, event.getMember());
            //case "close-ticket" -> closeTicket(event);
            case "close-ticket" -> removeTicket(event);
        }
    }

    public void createApplyTicket(StringSelectInteractionEvent event, String option) {
        Category category = event.getGuild().getCategoryById(Main.ticketSystemCategoryID);
        String textChannel = option + "-"+event.getMember().getEffectiveName();

        if(event.getJDA().getTextChannelsByName(textChannel, true).isEmpty()) {
            ChannelAction<TextChannel> channelAction = category.createTextChannel(textChannel);
            channelAction.addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null);
            channelAction.queue(channel -> {
                String channelID = channel.getId();
                TextChannel ticket = event.getJDA().getTextChannelById(channelID);
                ticketLogs.createTicket(event.getMember(), channelID);
                embedMessageGenerator.sendPanelInTicket(ticket, event.getMember());
                event.reply("Utworzono ticket " + channel.getAsMention()).setEphemeral(true).queue();
            });
        } else {
            event.reply("Nie możesz utworzyć nowego ticketa, najpierw musisz usunąć poprzedni!").setEphemeral(true).queue();
        }
    }

    public void createShopTicket(ButtonInteractionEvent event) {
        Category category = event.getGuild().getCategoryById(Main.ticketSystemCategoryID);
        String textChannel = "shop-"+event.getMember().getEffectiveName();

        if(event.getJDA().getTextChannelsByName(textChannel, true).isEmpty()) {
            ChannelAction<TextChannel> channelAction = category.createTextChannel(textChannel);
            channelAction.addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null);
            channelAction.queue(channel -> {
                String channelID = channel.getId();
                sendPanelInTicket(event, channelID);
                event.reply("Utworzono ticket " + channel.getAsMention()).setEphemeral(true).queue();
            });
        } else {
            event.reply("Nie możesz utworzyć nowego ticketa, najpierw musisz usunąć poprzedni!").setEphemeral(true).queue();
        }
    }

    public void createTicket(ButtonInteractionEvent event) {
        Category category = event.getGuild().getCategoryById(Main.ticketSystemCategoryID);
        String textChannel = "ticket-"+event.getMember().getEffectiveName();

        if(event.getJDA().getTextChannelsByName(textChannel, true).isEmpty()) {
            ChannelAction<TextChannel> channelAction = category.createTextChannel(textChannel);
            channelAction.addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null);
            channelAction.queue(channel -> {
                String channelID = channel.getId();
                sendPanelInTicket(event, channelID);
                event.reply("Utworzono ticket " + channel.getAsMention()).setEphemeral(true).queue();
            });
        } else {
            event.reply("Nie możesz utworzyć nowego ticketa, najpierw musisz usunąć poprzedni!").setEphemeral(true).queue();
        }
    }

    public void sendPanelInTicket(ButtonInteractionEvent event, String channelID) {
        TextChannel ticket = event.getJDA().getTextChannelById(channelID);
        ticketLogs.createTicket(event.getMember(), channelID);
        embedMessageGenerator.sendPanelInTicket(ticket, event.getMember());
    }

    public void closeTicket(ButtonInteractionEvent event) {
        TextChannel channel = event.getChannel().asTextChannel();
        event.reply("Zamknięto ticket <#"+channel.getId()+">").setEphemeral(true).queue();
        Member member = event.getMember();          // TODO: DO POPRAWY
        channel.getManager().removePermissionOverride(member).queue();

        embedMessageGenerator.sendPanelStaffAfterClosingTicket(channel, member);
    }

    public void removeTicket(ButtonInteractionEvent event) {
        TextChannel channel = event.getChannel().asTextChannel();
        channel.delete().reason("Ticket closed.").queue();
    }
}
