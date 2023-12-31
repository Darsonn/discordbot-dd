package pl.darsonn.discordbot;

public class Config {
    private String serverName;
    private String logoURL;
    private String rulesLink;
    private String welcomeChannelID;
    private String defaultMemberRoleID;
    private String ticketSystemCategoryID;
    private String ticketChannelID;
    private String changelogRoleID;
    private String discountsRoleID;
    private String changelogChannelID;
    private String devTeamRoleID;

    public String getServerName() {
        return this.serverName;
    }
    public String getLogoURL() {
        return this.logoURL;
    }
    public String getRulesLink() {
        return this.rulesLink;
    }
    public String getWelcomeChannelID() {
        return this.welcomeChannelID;
    }
    public String getDefaultMemberRoleID() {
        return this.defaultMemberRoleID;
    }
    public String getTicketSystemCategoryID() {
        return this.ticketSystemCategoryID;
    }
    public String getTicketChannelID() {
        return this.ticketChannelID;
    }
    public String getChangelogRoleID() {
        return changelogRoleID;
    }
    public String getDiscountsRoleID() {
        return discountsRoleID;
    }
    public String getChangelogChannelID() {
        return changelogChannelID;
    }
    public String getDevTeamRoleID() {
        return devTeamRoleID;
    }
}
