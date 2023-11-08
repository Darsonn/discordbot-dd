package pl.darsonn;

import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import pl.darsonn.discordbot.Config;
import pl.darsonn.discordbot.DiscordBot;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.darsonn.discordbot.database.DatabaseOperation;

import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static Config config;

    public static void main(String[] args) {
        loadConfig();

        try {
            DiscordBot discordBot = new DiscordBot();
            discordBot.startBot(args[0]);
        } catch (InvalidTokenException exception) {
            System.err.println("Błędny token");
        }

        DatabaseOperation databaseOperation = new DatabaseOperation();
        databaseOperation.getConnection();
    }

    public static void loadConfig() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("config.json");
            config = objectMapper.readValue(inputStream, Config.class);
        } catch (IOException e) {
            System.err.println("Plik konfiguracyjny nie istnieje lub źle został zdefiniowany.");
            throw new RuntimeException(e);
        }
    }
}