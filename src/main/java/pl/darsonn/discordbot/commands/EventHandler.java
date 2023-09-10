package pl.darsonn.discordbot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import pl.darsonn.discordbot.Main;
import pl.darsonn.discordbot.embedMessagesGenerator.EmbedMessageGenerator;
import pl.darsonn.discordbot.ticketsystem.TicketSystemListener;

import java.util.Objects;

public class EventHandler extends ListenerAdapter {
    EmbedMessageGenerator embedMessageGenerator = new EmbedMessageGenerator();
    TicketSystemListener ticketSystemListener = new TicketSystemListener();

    String[] setupOptions = new String[]{"rules", "ticketsystem", "shopinfo"};


    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Member member = event.getMember();

        event.getGuild().addRoleToMember(member, Objects.requireNonNull(event.getJDA().getRoleById(Main.defaultMemberRoleID))).queue();

        embedMessageGenerator.sendWelcomeMessage(Objects.requireNonNull(event.getGuild().getTextChannelById(Main.welcomeChannelID)), member);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) return;

        switch (event.getName()) {
            case "sendrules" -> sendRulesCommand(event);
            case "setupticket" -> setupTicketCommand(event);
            case "sendshopinfo" -> sendShopInfoCommand(event);
            case "sendstatusroles" -> sendStatusRolesCommand(event);
            case "sendlinkmessage" -> sendLinksMessageCommand(event);
            //case "editstatusofapplication" ->
            default -> event.reply("I can't handle that command right now :(").setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Button component = event.getComponent();

        if(Objects.requireNonNull(component.getId()).endsWith("-ticket")) {
            ticketSystemListener.interactionListener(event, component);
        }
//        switch(component) {} // inne interakcje niż ticket system
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if(event.getComponentId().equals("applyoption")) {
            ticketSystemListener.createApplyTicket(event, event.getValues().get(0));
        }
    }

    public void sendRulesCommand(SlashCommandInteractionEvent event) {
        event.reply("Rules embed message sent!").setEphemeral(true).queue();
        embedMessageGenerator.sendRulesEmbedMessage(event);
    }

    public void setupTicketCommand(SlashCommandInteractionEvent event) {
        event.reply("Ticket panel has been created!").setEphemeral(true).queue();
        embedMessageGenerator.sendTicketPanelEmbedMessage(event);
    }

    private void sendShopInfoCommand(SlashCommandInteractionEvent event) {
        event.reply("Shop embed message sent!").setEphemeral(true).queue();
        embedMessageGenerator.sendShopEmbedMessage(event);
    }

    private void sendStatusRolesCommand(SlashCommandInteractionEvent event) {
        event.reply("Message with roles status sent!").setEphemeral(true).queue();
        embedMessageGenerator.sendStatusRolesEmbedMessage(event, event.getOption("administrator").getAsBoolean(),
                event.getOption("developer").getAsBoolean(), event.getOption("creator").getAsBoolean());
    }

    private void sendLinksMessageCommand(SlashCommandInteractionEvent event) {
        event.reply("Message with links sent!").setEphemeral(true).queue();
        embedMessageGenerator.sendLinksEmbedMessage(event);
    }
}
