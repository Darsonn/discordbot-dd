package pl.darsonn.discordbot.logs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import pl.darsonn.discordbot.embedMessagesGenerator.EmbedMessageGenerator;

import java.sql.Timestamp;
import java.util.Objects;

public class TicketLogs {
    EmbedMessageGenerator embedMessageGenerator = new EmbedMessageGenerator();
    private final String ticketLogsChannelID = "1150147211980701919";

    public void createTicket(Member member, String channelID, Timestamp timestamp) {
        TextChannel ticketLogsChannel = member.getJDA().getTextChannelById(ticketLogsChannelID);
        embedMessageGenerator.sendInformationAboutCreationNewTicket(Objects.requireNonNull(ticketLogsChannel), member, channelID, timestamp);
    }

    public void closeTicket(Member member, String channelID) {
        TextChannel ticketLogsChannel = member.getJDA().getTextChannelById(ticketLogsChannelID);
        Timestamp closeDate = new Timestamp(System.currentTimeMillis());
        embedMessageGenerator.sendInformationAboutClosingTicket(Objects.requireNonNull(ticketLogsChannel), member, channelID, closeDate);
    }

    public void deleteTicket(Member member, String channelID) {
        TextChannel ticketLogsChannel = member.getJDA().getTextChannelById(ticketLogsChannelID);
        Timestamp closeDate = new Timestamp(System.currentTimeMillis());
        embedMessageGenerator.sendInformationAboutDeletingTicket(Objects.requireNonNull(ticketLogsChannel), member, channelID, closeDate);
    }
}
