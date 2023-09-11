package pl.darsonn.discordbot.embedMessagesGenerator;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import pl.darsonn.discordbot.Main;

import java.awt.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmbedMessageGenerator {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    LocalDateTime time = LocalDateTime.now();
    EmbedBuilder embedBuilder = new EmbedBuilder();

    public void sendRulesEmbedMessage(SlashCommandInteractionEvent event) {
        TextChannel textChannel = event.getGuildChannel().asTextChannel();

        embedBuilder.clear();

        embedBuilder.setTitle(Main.serverName);
        embedBuilder.setColor(Color.YELLOW);

        embedBuilder.addField("Regulamin jest dostępny na naszej stronie internetowej\n",
                "[Rules - " + Main.serverName + "](" + Main.rulesLink + ")", false);

        textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void sendTicketPanelEmbedMessage(SlashCommandInteractionEvent event) {
        TextChannel textChannel = event.getGuildChannel().asTextChannel();

        embedBuilder.clear();

        embedBuilder.setTitle(Main.serverName + " Ticket Support");
        embedBuilder.setColor(Color.YELLOW);

        embedBuilder.addField("Przed utworzeniem ticketa!",
                "Upewnij się, że przeczytałeś takie kanały jak: <#1150210897256665129>, <#1118929071997460650> oraz <#1145107437414789160>." +
                        "\n\nW przypadku utworzenia ticketa bez powodu będą wyciągane z tego tytułu konsekwencje." +
                        "\n\nWybierz rodzaj sprawy przez którą chcesz utworzyć ticket", true);

        textChannel.sendMessageEmbeds(embedBuilder.build())
                .addActionRow(
                        Button.primary("main-open-ticket", "Problem ogólny"),
                        Button.success("shop-open-ticket", "Zamówienie/problem ze sklepem"),
                        Button.danger("apply-open-ticket", "Aplikuj do " + Main.serverName + " Staff")
                )
                .queue();
    }

    public void sendApplyOptionsMenu(ButtonInteractionEvent event, Member member) {
        event.reply("Wybierz stanowisko na jakie chcesz aplikować")
                .addActionRow(
                        StringSelectMenu.create("applyoption")
                                .addOption("Administrator", "adm", "Aplikuj na stanowisko administratora")
                                .addOption("Developer", "dev", "Aplikuj na stanowisko developera")
                                //.addOption("Creator", "creator", "Aplikuj na stanowisko twórcy")
                                .build())
                .setEphemeral(true)
                .queue();
    }

    public void sendPanelInTicket(TextChannel ticket, Member member, String ticketType) {
        embedBuilder.clear();

        embedBuilder.setTitle(Main.serverName + " Ticket Support");
        embedBuilder.setColor(Color.YELLOW);

        switch (ticketType) {
            case "ticket":
                embedBuilder.setDescription("Dziękujemy za kontakt z "+ Main.serverName +" Support.\n" +
                        "Proszę opisać swój problem i czekać na odpowiedź z naszej strony.");
                break;
            case "shop":
                embedBuilder.addField("Chcesz zakupić skrypt/usługę nie wymieniony w <#1150210897256665129>?",
                        "- Proszę opisać jakie funkcjonalności powinien zawierać ten skrypt" +
                                "\n- Na jaki framework skrypt ma zostać napisany" +
                                "\n- Inne dodatkowe informacje", true);
                embedBuilder.addField("Chcesz kupić jeden ze skryptów z <#1150210897256665129>?",
                        "- Proszę podać skrypt/usługę, którą chcesz zakupić" +
                                "\n- Czy będziesz potrzebował edycji/dostosowania owego skryptu pod swój serwer?", true);
                embedBuilder.addField("Problem ze skryptem/usługą",
                        "Opisz problem jaki występuje", true);
                break;
            case "applyadm":
                embedBuilder.addField("Podanie na administratora jest dostępne na naszej stronie internetowej pod linkiem:",
                        "[Podanie - " + Main.serverName + "](https://dev.darsonn.pl/#adm)", true);
                break;
            case "applydev":
                embedBuilder.addField("Niezbędne informacje jakie powinieneś zawrzeć",
                        "- Imię" +
                                "\n- Wiek" +
                                "\n- Doświadczenie" +
                                "\n- Portfolio" +
                                "\n- Umiejętności" +
                                "\n\nPo napisaniu podania zadamy kilka pytań uzupełniających.", true);
                break;
            case "applycreator":
                embedBuilder.setDescription("Creator is closed position!");
        }

        embedBuilder.setFooter("Created at " + dtf.format(time));

        ticket.sendMessage("||<@"+ member.getId()+">||").queue();
        ticket.sendMessageEmbeds(embedBuilder.build())
                .addActionRow(
                        Button.danger("close-ticket", "Zamknij ticket")
                )
                .queue();
    }

    public void sendPanelStaffAfterClosingTicket(TextChannel ticket, Member member) {
        ticket.sendMessage("Ticket closed by <@"+ member.getId()+">").queue();

        embedBuilder.clear();
        embedBuilder.setTitle(Main.serverName + " Staff Ticket Panel");
        embedBuilder.setColor(Color.RED);

        ticket.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void sendInformationAboutCreationNewTicket(TextChannel ticketLogsChannel, Member member, String channelID) {
        embedBuilder.clear();

        embedBuilder.setTitle("Ticket created");
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setDescription("Created by " + member.getEffectiveName() + " at " + dtf.format(time) +
                "\n<#" + channelID + ">");

        ticketLogsChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void sendInformationAboutClosingTicket(TextChannel ticketLogsChannel, Member member, String channelID) {
        embedBuilder.clear();

        embedBuilder.setTitle("Ticket closed");
        embedBuilder.setColor(Color.YELLOW);
        embedBuilder.setDescription("Closed by " + member.getEffectiveName() + " at " + dtf.format(time) +
                "\n<#" + channelID + ">");

        ticketLogsChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void sendInformationAboutDeletingTicket(TextChannel ticketLogsChannel, Member member, String channelID) {
        embedBuilder.clear();

        embedBuilder.setTitle("Ticket deleted");
        embedBuilder.setColor(Color.RED);
        embedBuilder.setDescription("Deleted by " + member.getEffectiveName() + " at " + dtf.format(time) +
                "\n<#" + channelID + ">");

        ticketLogsChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void sendWelcomeMessage(TextChannel welcomeChannel, Member member) {
        embedBuilder.clear();

        embedBuilder.setTitle("Witamy "+member.getEffectiveName()+"!");
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setDescription("Witamy na serwerze **" + Main.serverName + "**");
        embedBuilder.setTimestamp(Instant.now());

        welcomeChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void sendShopEmbedMessage(SlashCommandInteractionEvent event) {
        TextChannel textChannel = event.getGuildChannel().asTextChannel();

        embedBuilder.clear();

        embedBuilder.setTitle(Main.serverName + " shop informations");
        embedBuilder.setColor(Color.YELLOW);

        embedBuilder.setDescription("Najważniejsze informacje odnośnie funkcjonowania sklepu");

        embedBuilder.addBlankField(false);

        embedBuilder.addField("Znalazłeś skrypt, którego szukasz?", "Utwórz ticket na kanale <#" + Main.ticketChannelID + "> i poinformuj nas, że chcesz kupić dostęp do niego.\n" +
                "Po dogadaniu się z płatnością dostaniesz dostęp do prywatnego repozytorium na GitHubie, gdzie będziesz mógł go pobrać" +
                "i będziesz na bieżąco z aktualizacjami", true);

        embedBuilder.addField("Nie ma w sklepie tego czego szukasz?",
                "To nie problem! Utwórz ticket na kanale <#" + Main.ticketChannelID + "> i napisz czego oczekujesz od skryptu, a my zajmiemy się resztą.", true);

        embedBuilder.addBlankField(false);

        embedBuilder.addField("Nie masz wystarczającej wiedzy jak wgrać skrypt na swój serwer?",
                "To również nie problem! Posiadamy rozbudowaną sekcję poradników, gdzie na kanale <#1145107437414789160>" +
                        " znajdziesz dokładny poradnik jak wgrać skrypt krok po kroku. \n" +
                        "Jeżeli natomiast będziesz miał jakiś problem pomocy uzyskasz na kanale <#1150189231994589264>." +
                        " W ostateczności możesz również utworzyć ticket na kanale <#" + Main.ticketChannelID + ">, a **" + Main.serverName +
                        " Staff** pomoże Ci w rozwiązaniu tego problemu.", true);

        textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void sendStatusRolesEmbedMessage(SlashCommandInteractionEvent event, boolean isAdm, boolean isDev, boolean isCreator) {
        TextChannel textChannel = event.getChannel().asTextChannel();

        embedBuilder.clear();

        embedBuilder.setTitle(Main.serverName + " - statusy rekrutacji");
        embedBuilder.setColor(Color.YELLOW);

        embedBuilder.addBlankField(false);

        String wynik = isAdm ? ":white_check_mark: Otwarta" : ":x: Zamknięta";
        embedBuilder.addField("Administrator", wynik, true);

        wynik = isDev ? ":white_check_mark: Otwarta" : ":x: Zamknięta";
        embedBuilder.addField("Developer", wynik, true);

        wynik = isCreator ? ":white_check_mark: Otwarta" : ":x: Zamknięta";
        embedBuilder.addField("Twórca", wynik, true);

        embedBuilder.addBlankField(false);

        embedBuilder.addField("Proces aplikacji", "Aby zaaplikować należy utworzyć odpowiedni ticket na kanale " +
                "<#1145139398204215406>, dalej zostanie przedstawiony cały proces rekrutacyjny w zależności " +
                "od aplikowanego stanowiska", true);

        textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void sendLinksEmbedMessage(SlashCommandInteractionEvent event) {
        TextChannel textChannel = event.getChannel().asTextChannel();

        embedBuilder.clear();

        embedBuilder.setTitle("Linki do narzędzi potrzebnych w tej kategorii");
        embedBuilder.setColor(Color.YELLOW);
        embedBuilder.addField("Programowanie", "- [Visual Studio Code](https://code.visualstudio.com)", true);
        embedBuilder.addField("Edycja plików pojazdów itd.", "- [OpenIV](https://openiv.com)", true);
        embedBuilder.addField("Grafika",
                "- [GIMP](https://www.gimp.org/) (darmowa opcja)" +
                        "\n- [Photoshop](https://www.adobe.com/pl/products/photoshop.html) (płatna opcja)", true);

        textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
