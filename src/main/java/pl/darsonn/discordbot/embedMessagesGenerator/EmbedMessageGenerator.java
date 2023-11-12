package pl.darsonn.discordbot.embedMessagesGenerator;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import pl.darsonn.Main;
import pl.darsonn.discordbot.database.DatabaseOperation;

import java.awt.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class EmbedMessageGenerator {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    LocalDateTime time = LocalDateTime.now();
    EmbedBuilder embedBuilder = new EmbedBuilder();
    DatabaseOperation databaseOperation = new DatabaseOperation();

    public void sendRulesEmbedMessage(SlashCommandInteractionEvent event) {
        TextChannel textChannel = event.getGuildChannel().asTextChannel();

        embedBuilder.clear();

        embedBuilder.setTitle(Main.config.getServerName());
        embedBuilder.setColor(Color.YELLOW);

        embedBuilder.addBlankField(false);

        embedBuilder.addField("Zasady ogólne - panujące na serwerze " + Main.config.getServerName(),
                """
                        1. Szanuj innych użytkowników.
                        2. Zakaz wysyłania treści dla osób pełnoletnich.
                        3. Nie promuj żadnych treści o charakterze rasistowskim, seksistowskim, homofobicznym ani innym obraźliwym.
                        4. Wszelkie treści spamu będą natychmiastowo usuwane.
                        5. Reklama jest dozwolona tylko i wyłącznie na przeznaczonych do tego kanałach lub za zgodą administracji
                        
                        Na serwerze dodatkowo obowiązują ogólnie ustanowione zasady [Discord Terms of Service](https://discord.com/terms)
                        """, false);

        //kontynuacja

        embedBuilder.setTimestamp(Instant.now());

        textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void sendTicketPanelEmbedMessage(SlashCommandInteractionEvent event) {
        TextChannel textChannel = event.getGuildChannel().asTextChannel();

        embedBuilder.clear();

        embedBuilder.setTitle(Main.config.getServerName() + " Ticket Support");
        embedBuilder.setColor(Color.YELLOW);

        embedBuilder.addField("Przed utworzeniem ticketa!",
                """
                        Upewnij się, że przeczytałeś takie kanały jak: <#1150210897256665129>, <#1118929071997460650> oraz <#1145107437414789160>.

                        W przypadku utworzenia ticketa bez powodu będą wyciągane z tego tytułu konsekwencje.

                        Wybierz rodzaj sprawy przez którą chcesz utworzyć ticket""", true);

        textChannel.sendMessageEmbeds(embedBuilder.build())
                .addActionRow(
                        Button.primary("main-open-ticket", "Problem ogólny"),
                        Button.success("shop-open-ticket", "Zamówienie/problem ze sklepem"),
                        Button.danger("apply-open-ticket", "Aplikuj do " + Main.config.getServerName() + " Staff")
                )
                .queue();
    }

    public void sendApplyOptionsMenu(ButtonInteractionEvent event) {
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

        embedBuilder.setTitle(Main.config.getServerName() + " Ticket Support");
        embedBuilder.setColor(Color.YELLOW);

        switch (ticketType) {
            case "ticket":
                embedBuilder.setDescription("Dziękujemy za kontakt z "+ Main.config.getServerName() +" Support.\n" +
                        "Proszę opisać swój problem i czekać na odpowiedź z naszej strony.");
                break;
            case "shop":
                embedBuilder.addField("Chcesz zakupić skrypt/usługę nie wymieniony w <#1150210897256665129>?",
                        """
                                - Proszę opisać jakie funkcjonalności powinien zawierać ten skrypt
                                - Na jaki framework skrypt ma zostać napisany
                                - Inne dodatkowe informacje""", true);
                embedBuilder.addField("Chcesz kupić jeden ze skryptów z <#1150210897256665129>?",
                        "- Proszę podać skrypt/usługę, którą chcesz zakupić" +
                                "\n- Czy będziesz potrzebował edycji/dostosowania owego skryptu pod swój serwer?", true);
                embedBuilder.addField("Problem ze skryptem/usługą",
                        "Opisz problem jaki występuje", true);
                break;
            case "applyadm":
                embedBuilder.addField("Podanie na administratora jest dostępne na naszej stronie internetowej pod linkiem:",
                        "[Podanie - " + Main.config.getServerName() + "](https://dev.darsonn.pl/#adm)", true);
                break;
            case "applydev":
                embedBuilder.addField("Niezbędne informacje jakie powinieneś zawrzeć",
                        """
                                - Imię
                                - Wiek
                                - Doświadczenie
                                - Portfolio
                                - Umiejętności

                                Po napisaniu podania zadamy kilka pytań uzupełniających.""", true);
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
        embedBuilder.setTitle(Main.config.getServerName() + " Staff Ticket Panel");
        embedBuilder.setColor(Color.RED);

        ticket.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void sendInformationAboutCreationNewTicket(TextChannel ticketLogsChannel, Member member, String channelID, Timestamp timestamp) {
        embedBuilder.clear();

        embedBuilder.setTitle("Ticket created");
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setDescription("Created by " + member.getAsMention() + " at " + timestamp +
                "\n<#" + channelID + ">");

        ticketLogsChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void sendInformationAboutClosingTicket(TextChannel ticketLogsChannel, Member member, String channelID, Timestamp closingDate) {
        embedBuilder.clear();

        embedBuilder.setTitle("<#" + channelID + "> - Ticket closed");
        embedBuilder.setColor(Color.YELLOW);
        embedBuilder.addField("Opened", "by <@" + databaseOperation.getTicketOpener(channelID) + ">\nat " +
                databaseOperation.getTicketCreateDate(channelID), false);
        embedBuilder.addField("Closed", "by " + member.getAsMention() + "\nat " +
                closingDate, false);

        ticketLogsChannel.sendMessageEmbeds(embedBuilder.build()).queue();

        databaseOperation.closeTicket(channelID, member.getId(), closingDate);
    }

    public void sendInformationAboutDeletingTicket(TextChannel ticketLogsChannel, Member member, String channelID, Timestamp closingDate) {
        embedBuilder.clear();

        embedBuilder.setTitle("<#" + channelID + "> - Ticket deleted");
        embedBuilder.setColor(Color.RED);
        embedBuilder.addField("Opened", "by <@" + databaseOperation.getTicketOpener(channelID) + ">\nat " +
                databaseOperation.getTicketCreateDate(channelID), false);
        embedBuilder.addField("Deleted", "by " + member.getAsMention() + "\nat " +
                closingDate, false);

        ticketLogsChannel.sendMessageEmbeds(embedBuilder.build()).queue();

        databaseOperation.closeTicket(channelID, member.getId(), closingDate);

    }

    public void sendWelcomeMessage(TextChannel welcomeChannel, Member member) {
        embedBuilder.clear();

        embedBuilder.setTitle("Witamy "+member.getEffectiveName()+"!");
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setDescription("Witamy na serwerze **" + Main.config.getServerName() + "**");
        embedBuilder.setTimestamp(Instant.now());

        welcomeChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void sendShopEmbedMessage(SlashCommandInteractionEvent event) {
        TextChannel textChannel = event.getGuildChannel().asTextChannel();

        embedBuilder.clear();

        embedBuilder.setTitle(Main.config.getServerName() + " shop informations");
        embedBuilder.setColor(Color.YELLOW);

        embedBuilder.setDescription("Najważniejsze informacje odnośnie funkcjonowania sklepu");

        embedBuilder.addBlankField(false);

        embedBuilder.addField("Znalazłeś produkt, którego szukasz?", "Utwórz ticket na kanale <#" + Main.config.getTicketChannelID() + "> i poinformuj nas, że chcesz kupić dostęp do niego.\n" +
                "Po dogadaniu się z płatnością dostaniesz dostęp do prywatnego repozytorium na GitHubie, gdzie będziesz mógł go pobrać" +
                "i będziesz na bieżąco z aktualizacjami", true);

        embedBuilder.addField("Nie ma w sklepie tego czego szukasz?",
                "To nie problem! Utwórz ticket na kanale <#" + Main.config.getTicketChannelID() + "> i napisz czego oczekujesz, a my zajmiemy się resztą.", true);

        embedBuilder.addBlankField(false);

        embedBuilder.addField("Nie masz wystarczającej wiedzy jak uruchomić oraz skonfigurować bota na swój serwer?",
                "To również nie problem! Posiadamy rozbudowaną sekcję poradników, gdzie na kanale <#1145107437414789160>" +
                        " znajdziesz dokładny poradnik jak tego dokonać krok po kroku. \n" +
                        "Jeżeli natomiast będziesz miał jakiś problem pomocy uzyskasz na kanale <#1150189231994589264>." +
                        " W ostateczności możesz również utworzyć ticket na kanale <#" + Main.config.getTicketChannelID() + ">, a **" + Main.config.getServerName() +
                        " Staff** pomoże Ci w rozwiązaniu tego problemu.", true);

        embedBuilder.addBlankField(false);

        embedBuilder.addField("Zasady gwarancji",
                "Jako zespół developerski udzielamy wsparcia w przypadku gdy bot będzię zawierał błędy oraz " +
                        "któraś z wcześniej ustalonych rzeczy nie została uwzględniona i/lub wykonana.", false);

        embedBuilder.addField("Uwaga!", "Pieniędzy za wykonany skrypt **nie** zwracamy!", false);

        textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void sendStatusRolesEmbedMessage(StringSelectInteractionEvent event) {
        boolean isAdm = false, isDev = false, isCreator = false;
        TextChannel textChannel = event.getChannel().asTextChannel();

        textChannel.getHistory().retrievePast(2)
                .map(messages -> messages.get(0))
                .queue(message -> {
                    System.out.println(message.getContentRaw());
                    if(message.getAuthor().isBot()) message.delete().queue();
                });

        for (var element : event.getSelectedOptions()) {
            if(element.getValue().equals("administrator")) isAdm = true;
            if(element.getValue().equals("developer")) isDev = true;
            if(element.getValue().equals("creator")) isCreator = true;
        }

        embedBuilder.clear();

        embedBuilder.setTitle(Main.config.getServerName() + " - statusy rekrutacji");
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

        textChannel.sendMessageEmbeds(embedBuilder.build())
                .addActionRow(
                        isAdm ? Button.success("requirements-adm", "Wymagania na Administratora").asEnabled() : Button.success("requirements-adm", "Wymagania na Administratora").asDisabled(),
                        isDev ? Button.primary("requirements-dev", "Wymagania na Developera").asEnabled() : Button.primary("requirements-dev", "Wymagania na Developera").asDisabled(),
                        isCreator ? Button.danger("requirements-tworca", "Wymagania na Twórcę").asEnabled() : Button.danger("requirements-tworca", "Wymagania na Twórcę").asDisabled()
                ).queue();

        event.reply("Message sent!").setEphemeral(true).queue();
    }

    public void sendLinksEmbedMessage(SlashCommandInteractionEvent event) {
        TextChannel textChannel = event.getChannel().asTextChannel();

        embedBuilder.clear();

        embedBuilder.setTitle("Linki do narzędzi potrzebnych w tej kategorii");
        embedBuilder.setColor(Color.YELLOW);
        embedBuilder.addField("Programowanie", "- [Visual Studio Code](https://code.visualstudio.com)\n" +
                "- [Intellij Idea](https://www.jetbrains.com/idea/)", true);
        embedBuilder.addField("Edycja plików pojazdów itd.", "- [OpenIV](https://openiv.com)", true);
        embedBuilder.addField("Grafika",
                "- [GIMP](https://www.gimp.org/) (darmowa opcja)" +
                        "\n- [Photoshop](https://www.adobe.com/pl/products/photoshop.html) (płatna opcja)", true);

        textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void sendPriceListEmbedMessage(StringSelectInteractionEvent event) {
        TextChannel textChannel = event.getChannel().asTextChannel();
        String option = Objects.requireNonNull(event.getValues().get(0));

        embedBuilder.clear();

        switch (option) {
            case "fivem" -> {
                embedBuilder.addField("Usługi Fivem", "", false);
            }
            case "dcbot" -> {
                embedBuilder.addField("Discord Bot", "", false);
            }
            case "korepetycje" -> {
                embedBuilder.addField("Korepetycje", "", false);
            }
        }

        embedBuilder.setTitle("Cennik - " + Main.config.getServerName());
        embedBuilder.setDescription("Cennik usług podstawowych");
        embedBuilder.setColor(Color.YELLOW);

        event.reply("Message sent!").setEphemeral(true).queue();

        textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void sendWIPEmbedMessage(SlashCommandInteractionEvent event) {
        TextChannel textChannel = event.getChannel().asTextChannel();

        embedBuilder.clear();

        embedBuilder.setTitle(Main.config.getServerName() + " - WIP");
        embedBuilder.setColor(Color.YELLOW);

        embedBuilder.setDescription("Ten element nie został jeszcze ukończony.");

        textChannel.sendMessageEmbeds(embedBuilder.build()).queue();

        event.reply("Utworzono wiadomość").setEphemeral(true).queue();
    }

    public void sendRequirements(ButtonInteractionEvent event, String type) {
        embedBuilder.clear();

        embedBuilder.setTitle(Main.config.getServerName() + " - WIP");
        embedBuilder.setColor(Color.YELLOW);

        if(type.equals("adm")) {
            embedBuilder.addField("Wymagania na stanowisko **Administrator**",
                    """
                            Na stanowisko administratora wymagane jest:\s
                            - min. 16 lat
                            - doświadczenie na podobnym stanowisku
                            - czas na sprawdzanie kanałów""", false);
        } else if (type.equals("dev")) {
            embedBuilder.addField("Wymagania na stanowisko **Developera**",
                    """
                            Na stanowisko developera wymagane jest:\s
                            - umiejętność tworzenia skryptów/oprogramowania itd. w dziedzinach wymienionych na kanale <#1150859679463915591> lub inne przydatne umiejętności w tematyce serwera
                            - umiejętność tworzenia kodu z dobrą optymalizacją
                            - umiejętność pracy zespołowej za pomocą VCS (Git)
                            """, false);
        } else if (type.equals("tworca")) {
            embedBuilder.addField("Wymagania na stanowisko **Twórca**",
                    "Na stanowisko twórcy wymagane jest: " +
                            "Work in progress", false);
        }

        embedBuilder.setDescription(""); //TODO

        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
    }

    public void sendPartnerInfo(SlashCommandInteractionEvent event) {
        TextChannel textChannel = event.getChannel().asTextChannel();

        embedBuilder.clear();

        embedBuilder.setTitle(Main.config.getServerName() + " - Partner informations");
        embedBuilder.setColor(Color.YELLOW);

        embedBuilder.setDescription("Ten element nie został jeszcze ukończony.");

        textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void sendInviteMessage(SlashCommandInteractionEvent event) {
        embedBuilder.clear();

        embedBuilder.setTitle(Main.config.getServerName() + " - Invite Link");
        embedBuilder.setColor(Color.YELLOW);
        embedBuilder.setDescription("https://discord.darsonn.pl/ \n(currently not available)");

        event.replyEmbeds(embedBuilder.build()).queue();
    }

    public void sendStatusEmbedMessage(SlashCommandInteractionEvent event, long ping, boolean databaseStatus) {
        embedBuilder.clear();

        embedBuilder.setTitle("Status bota " + Main.config.getServerName());
        embedBuilder.setColor(Color.YELLOW);

        embedBuilder.addField("Opóźnienie bota (PING): ", String.valueOf(ping), false);
        embedBuilder.addField("Status połączenia z bazą danych: ",
                databaseStatus? "Poprawne" : "Błędne",
                false);

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
