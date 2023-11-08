package pl.darsonn.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import pl.darsonn.Main;
import pl.darsonn.discordbot.commands.CommandsCreator;
import pl.darsonn.discordbot.commands.EventHandler;

import java.util.EnumSet;

public class DiscordBot {
    public void startBot(String token) {
        JDA builder = JDABuilder.createLight(token, EnumSet.noneOf(GatewayIntent.class))
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.watching(Main.config.getServerName()))
                .addEventListeners(new EventHandler())
                .build();

        CommandsCreator commandsCreator = new CommandsCreator();
        commandsCreator.createCommands(builder);
    }
}
