package pl.darsonn.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import pl.darsonn.discordbot.commands.CommandsCreator;
import pl.darsonn.discordbot.commands.EventHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.darsonn.discordbot.database.DatabaseOperation;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;

public class Main {
    public static String serverName, rulesLink, welcomeChannelID, defaultMemberRoleID, logoURL, ticketSystemCategoryID,
            ticketChannelID;

    public static void main(String[] args) {
        loadConfig();

        DatabaseOperation databaseOperation = new DatabaseOperation();
        databaseOperation.getConnection();

        JDA builder = JDABuilder.createLight(args[0], EnumSet.noneOf(GatewayIntent.class))
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setActivity(Activity.watching(Main.serverName))
                .addEventListeners(new EventHandler())
                .build();

        CommandsCreator commandsCreator = new CommandsCreator();
        commandsCreator.createCommands(builder);
    }

    public static void loadConfig() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("config.json");
            Config config = objectMapper.readValue(inputStream, Config.class);

            serverName = config.getServerName();
            logoURL = config.getLogoURL();
            rulesLink = config.getRulesLink();
            welcomeChannelID = config.getWelcomeChannelID();
            defaultMemberRoleID = config.getDefaultMemberRoleID();
            ticketSystemCategoryID = config.getTicketSystemCategoryID();
            ticketChannelID= config.getTicketChannelID();
        } catch (IOException e) {
            System.err.println("Plik konfiguracyjny nie istnieje lub źle został zdefiniowany.");
            throw new RuntimeException(e);
        }
    }
}