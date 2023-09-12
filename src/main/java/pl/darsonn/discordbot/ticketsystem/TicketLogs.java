package pl.darsonn.discordbot.ticketsystem;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import pl.darsonn.discordbot.database.DatabaseOperation;
import pl.darsonn.discordbot.embedMessagesGenerator.EmbedMessageGenerator;

import java.sql.Timestamp;

public class TicketLogs {
    EmbedMessageGenerator embedMessageGenerator = new EmbedMessageGenerator();
    private final String ticketLogsChannelID = "1150147211980701919";

    public void createTicket(Member member, String channelID, Timestamp timestamp) {
        TextChannel ticketLogsChannel = member.getJDA().getTextChannelById(ticketLogsChannelID);
        embedMessageGenerator.sendInformationAboutCreationNewTicket(ticketLogsChannel, member, channelID, timestamp);
    }

    public void closeTicket(Member member, String channelID) {
        TextChannel ticketLogsChannel = member.getJDA().getTextChannelById(ticketLogsChannelID);
        Timestamp closeDate = new Timestamp(System.currentTimeMillis());
        embedMessageGenerator.sendInformationAboutClosingTicket(ticketLogsChannel, member, channelID, closeDate);
    }

    public void deleteTicket(Member member, String channelID) {
        TextChannel ticketLogsChannel = member.getJDA().getTextChannelById(ticketLogsChannelID);
        Timestamp closeDate = new Timestamp(System.currentTimeMillis());
        embedMessageGenerator.sendInformationAboutDeletingTicket(ticketLogsChannel, member, channelID, closeDate);
    }
}
