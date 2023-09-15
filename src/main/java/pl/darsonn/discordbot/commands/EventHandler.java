package pl.darsonn.discordbot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import pl.darsonn.discordbot.Main;
import pl.darsonn.discordbot.embedMessagesGenerator.EmbedMessageGenerator;
import pl.darsonn.discordbot.ticketsystem.TicketSystemListener;

import java.util.Objects;

public class EventHandler extends ListenerAdapter {
    EmbedMessageGenerator embedMessageGenerator = new EmbedMessageGenerator();
    TicketSystemListener ticketSystemListener = new TicketSystemListener();

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
            case "purge" -> purgeCommand(event);
            case "sendpricelist" -> sendPriceListCommand(event);
            case "sendwip" -> embedMessageGenerator.sendWIPEmbedMessage(event);
            //case "editstatusofapplication" ->
            default -> event.reply("I can't handle that command right now :(").setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Button component = event.getComponent();

        if(Objects.requireNonNull(component.getId()).endsWith("-ticket")) {
            ticketSystemListener.interactionListener(event, component);
        } else {
            String[] id = event.getComponentId().split(":");
            String authorId = id[0];
            String type = id[1];

            if (!authorId.equals(event.getUser().getId()))
                return;
            event.deferEdit().queue();

            MessageChannel channel = event.getChannel();
            switch (type)
            {
                case "prune":
                    int amount = Integer.parseInt(id[2]);
                    event.getChannel().getIterableHistory()
                            .skipTo(event.getMessageIdLong())
                            .takeAsync(amount)
                            .thenAccept(channel::purgeMessages);
                case "delete":
                    event.getHook().deleteOriginal().queue();
            }
        }
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
        embedMessageGenerator.sendStatusRolesEmbedMessage(event, Objects.requireNonNull(event.getOption("administrator")).getAsBoolean(),
                Objects.requireNonNull(event.getOption("developer")).getAsBoolean(), Objects.requireNonNull(event.getOption("creator")).getAsBoolean());
    }

    private void sendLinksMessageCommand(SlashCommandInteractionEvent event) {
        event.reply("Message with links sent!").setEphemeral(true).queue();
        embedMessageGenerator.sendLinksEmbedMessage(event);
    }

    private void purgeCommand(SlashCommandInteractionEvent event) {
        OptionMapping amountOption = event.getOption("amount");
        int amount = amountOption == null
                ? 100 // default 100
                : (int) Math.min(200, Math.max(2, amountOption.getAsLong()));
        String userId = event.getUser().getId();
        event.reply("To usunie " + amount + " wiadomości.\nJesteś pewny?")
                .addActionRow(
                        Button.secondary(userId + ":delete", "Rezygnuję"),
                        Button.danger(userId + ":prune:" + amount, "Tak"))
                .queue();
    }

    private void sendPriceListCommand(SlashCommandInteractionEvent event) {
        event.reply("Message with price list sent!").setEphemeral(true).queue();
        embedMessageGenerator.sendPriceListEmbedMessage(event);
    }
}
