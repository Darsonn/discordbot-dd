package pl.darsonn.discordbot.ticketsystem;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import pl.darsonn.discordbot.embedMessagesGenerator.EmbedMessageGenerator;

public class TicketLogs {
    EmbedMessageGenerator embedMessageGenerator = new EmbedMessageGenerator();

    private final String ticketLogsChannelID = "1150147211980701919";

    public void createTicket(Member member, String channelID) {
        TextChannel ticketLogsChannel = member.getJDA().getTextChannelById(ticketLogsChannelID);
        embedMessageGenerator.sendInformationAboutCreationNewTicket(ticketLogsChannel, member, channelID);
    }

    public void closeTicket(Member member, String channelID) {
        TextChannel ticketLogsChannel = member.getJDA().getTextChannelById(ticketLogsChannelID);
        embedMessageGenerator.sendInformationAboutClosingTicket(ticketLogsChannel, member, channelID);
    }

    public void deleteTicket(Member member, String channelID) {
        TextChannel ticketLogsChannel = member.getJDA().getTextChannelById(ticketLogsChannelID);
        embedMessageGenerator.sendInformationAboutDeletingTicket(ticketLogsChannel, member, channelID);
    }
}
