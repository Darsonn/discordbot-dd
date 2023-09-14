package pl.darsonn.discordbot.database;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseOperation {
    private Connection connection;
    private Statement statement;

    public DatabaseOperation() {
        String request = "jdbc:mysql://localhost:3306/darsonndevelopment?useUnicode=true&characterEncoding=utf8";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(request, "root", "");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DatabaseOperation.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    public Connection getConnection() {
        String request = "jdbc:mysql://localhost:3306/darsonndevelopment?useUnicode=true&characterEncoding=utf8";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(request, "root", "");
            System.out.println("Nawiązano połączenie z bazą danych");
            return connection;

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DatabaseOperation.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Błąd z połączeniem z bazą danych.");
            return null;
        }
    }

    public void createTicket(Member member, String type, TextChannel channel, Timestamp timeOfOpeningTicket) {
        String request = "INSERT INTO `tickets`(`ID`,`DisplayName` , `OpenerID`, `CloserID`, `OpenedTime`, `ClosedTime`, `Type`, `ChannelName`, `ChannelID`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (final var statement = connection.prepareStatement(request)) {
            statement.setString(1, null);
            statement.setString(2, member.getEffectiveName());
            statement.setString(3, member.getId());
            statement.setString(4, null);
            statement.setString(5, String.valueOf(timeOfOpeningTicket));
            statement.setString(6, null);
            statement.setString(7, type);
            statement.setString(8, channel.getName());
            statement.setString(9, channel.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeTicket(String ticketChannelID, String ticketCloserID, Timestamp closingTime) {
        setClosedTime(ticketChannelID, closingTime);
        setTicketCloser(ticketChannelID, ticketCloserID);
    }

    public Timestamp getTicketCreateDate(String ticketChannelID) {
        String request = "SELECT * FROM tickets WHERE ChannelID = '" + ticketChannelID + "'";
        Timestamp ticketCreateDate = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(request);
            while (rs.next()) {
                ticketCreateDate = rs.getTimestamp("OpenedTime");
            }
            return ticketCreateDate;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTicketOpener(String ticketChannelID) {
        String request = "SELECT * FROM tickets WHERE ChannelID = '" + ticketChannelID + "'";
        String ticketOpener = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(request);
            while (rs.next()) {
                ticketOpener = rs.getString("OpenerID");
            }
            return ticketOpener;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setTicketCloser(String ticketChannelID, String ticketCloserID) {
        String request = "UPDATE tickets SET CloserID = ? WHERE ChannelID = " + ticketChannelID;
        try (final var statement = connection.prepareStatement(request)) {
            statement.setString(1, ticketCloserID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setClosedTime(String ticketChannelID, Timestamp timestamp) {
        String request = "UPDATE tickets SET ClosedTime = ? WHERE ChannelID = " + ticketChannelID;
        try (final var statement = connection.prepareStatement(request)) {
            statement.setTimestamp(1, timestamp);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}