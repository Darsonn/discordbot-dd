# Darsonn Development Discord Bot

[![Java](https://img.shields.io/badge/Java-18-orange.svg)](https://www.oracle.com/java/)
[![JDA](https://img.shields.io/badge/JDA-5.0.0--beta.13-red.svg)](https://github.com/discord-jda/JDA)
[![MySQL](https://img.shields.io/badge/MySQL-8.0.33-blue.svg)](https://www.mysql.com/)

Wielofunkcyjny bot Discord napisany w Javie, stworzony na potrzeby zarządzania serwerem społecznościowym i deweloperskim. Aplikacja opiera się na bibliotece JDA (Java Discord API) i wykorzystuje bazę danych MySQL do trwałego przechowywania logów oraz informacji o zgłoszeniach użytkowników.

## 🚀 Główne funkcjonalności

- **Rozbudowany system Ticketów (Zgłoszeń):** Interaktywne panele tworzone za pomocą przycisków (Buttons) i menu wyboru (Select Menus). Obsługa różnych kategorii: problemy ogólne, zapytania sklepowe oraz system rekrutacji (aplikacje na Administratora/Developera).
- **Zarządzanie użytkownikami (Welcome System):** Automatyczne przypisywanie ról nowym użytkownikom oraz generowanie wiadomości powitalnych typu Embed.
- **Slash Commands:** Pełne wsparcie dla interakcji `/`, w tym komendy administracyjne (`/purge`, `/setup`), informacyjne (`/status`, `/invite`) oraz deweloperskie (`/databaseoperations`).
- **Dynamiczne Wiadomości Embed:** Konfigurowalne szablony dla regulaminu, cenników, statusów rekrutacji i ogłoszeń (Changelog).
- **Logowanie zdarzeń (MySQL):** Zapisywanie informacji o otwarciu i zamknięciu ticketów z przypisaniem do konkretnych ID użytkowników i kanałów.
- **Moduł Socket Server:** Wbudowany prosty serwer TCP pozwalający na dwukierunkową komunikację z zewnętrznymi usługami/klientami.

## 🛠️ Stack technologiczny

- **Java 18**
- **JDA (Java Discord API) 5.0.0-beta.13**
- **MySQL Connector (8.0.33)**
- **Jackson Databind** (do parsowania konfiguracji JSON)
- **SLF4J** (do logowania działania aplikacji)
- **Maven** (zarządzanie zależnościami)

## ⚙️ Wymagania i instalacja

1. **Klonowanie repozytorium**
   ```bash
   git clone https://github.com/Darsonn/discordbot-dd.git
   cd discordbot-dd
   ```

2. **Konfiguracja Bazy Danych**
   Bot wymaga aktywnego serwera MySQL.
   - Stwórz bazę danych o nazwie `darsonndevelopment` na domyślnym porcie `3306`.
   - Zaimplementuj strukturę tabel, ze szczególnym uwzględnieniem tabeli `tickets`.

3. **Plik konfiguracyjny**
   W katalogu `src/main/resources/` upewnij się, że posiadasz poprawnie uzupełniony plik `config.json`. Schemat pliku:
   ```json
   {
     "serverName": "Twoja Nazwa Serwera",
     "welcomeChannelID": "ID_KANALU",
     "defaultMemberRoleID": "ID_ROLI",
     "ticketSystemCategoryID": "ID_KATEGORII",
     "ticketChannelID": "ID_KANALU_TICKET",
     "changelogRoleID": "ID_ROLI",
     "changelogChannelID": "ID_KANALU",
     "devTeamRoleID": "ID_ROLI"
   }
   ```

4. **Konfiguracja Discord Developer Portal**
   Zanim uruchomisz bota, upewnij się, że w portalu deweloperskim Discorda włączyłeś wymagane intencje (Privileged Gateway Intents):
   - `GUILD_MEMBERS`
   - `MESSAGE_CONTENT`

## 💻 Uruchomienie

Aby skompilować i uruchomić bota, użyj narzędzia Maven. 

**Uwaga:** Token bota musi zostać przekazany jako pierwszy argument startowy (args[0]) podczas uruchamiania aplikacji w klasie `Main.java`.

```bash
mvn clean install
java -jar target/darsonndevelopmentbot-1.0-SNAPSHOT.jar <TWOJ_TOKEN_BOTA>
```

## 📜 Główne komendy (Slash Commands)

- `/setup <opcja>` - Generuje wiadomości Embed dla konkretnych modułów (Rules, Tickets, Shop Info, Status roles, Price list, itd.). Wyłącznie dla uprawnionych.
- `/purge <ilość>` - Usuwa wskazaną liczbę wiadomości z kanału (wymaga potwierdzenia interaktywnym przyciskiem).
- `/changelog` - Pobiera ostatnią wiadomość i formatuje ją jako oficjalne ogłoszenie zmian na kanale changelog.
- `/databaseoperations` - Pozwala na zarządzanie bazą z poziomu Discorda (np. czyszczenie bazy ze starych ticketów, sprawdzanie połączenia).
- `/status` - Zwraca PING bramki sieciowej bota (Gateway) oraz weryfikuje połączenie z bazą MySQL.
