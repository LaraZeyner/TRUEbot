package de.zahrie.trues.api.discord.builder.modal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import de.zahrie.trues.api.community.application.TeamPosition;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.player.PlayerFactory;
import de.zahrie.trues.api.coverage.team.TeamFactory;
import de.zahrie.trues.api.coverage.team.TeamLoader;
import de.zahrie.trues.api.coverage.team.model.PRMTeam;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.database.Database;
import de.zahrie.trues.api.database.QueryBuilder;
import de.zahrie.trues.api.scouting.ScoutingGameType;
import de.zahrie.trues.util.StringUtils;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.RoleGranter;
import de.zahrie.trues.api.discord.message.Emote;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.api.riot.matchhistory.champion.Champion;
import de.zahrie.trues.api.riot.matchhistory.champion.ChampionFactory;
import de.zahrie.trues.api.riot.matchhistory.performance.Lane;
import de.zahrie.trues.util.Util;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.ExtensionMethod;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@EqualsAndHashCode(callSuper = true)
@ExtensionMethod(StringUtils.class)
public abstract class ModalImpl extends ModalBase {
  private DiscordUser target;

  public ModalImpl() {
    super();
  }

  //<editor-fold desc="fields">
  @NotNull
  public ActionRow getTargetUser() {
    final String nickname = target.getMember().getAsMention();
    return ActionRow.of(StringSelectMenu.create("target-name").setPlaceholder("Spielername")
        .addOption(nickname, String.valueOf(target.getId()))
        .build());
  }

  @NotNull
  public ActionRow getTeams() {
    final List<OrgaTeam> orgaTeams = QueryBuilder.hql(OrgaTeam.class, "FROM OrgaTeam").list();
    return ActionRow.of(StringSelectMenu.create("team-name").setPlaceholder("Teamname")
        .addOptions(orgaTeams.stream().map(team -> SelectOption.of(team.getName(), String.valueOf(team.getId()))).toList())
        .build());
  }

  @NotNull
  public ActionRow getApplicationRoleField() {
    return ActionRow.of(StringSelectMenu.create("app-role").setPlaceholder("Rolle im Team")
        .addOption("Standin", "STANDIN", "Tryout erhält für 1 Tag Zugriff")
        .addOption("Tryout", "TRYOUT", "Tryout erhält für 14 Tage Zugriff")
        .addOption("Substitude", "SUBSTITUDE", "Spieler erhält permanenten Zugriff")
        .addOption("Mainspieler", "MAIN", "Spieler erhält permanenten Zugriff")
        .build());
  }

  @NotNull
  public ActionRow getApplicationRoleField2() {
    return ActionRow.of(StringSelectMenu.create("app-role").setPlaceholder("Rolle im Team")
        .addOption("Tryout", "TRYOUT", "Tryout erhält für 14 Tage Zugriff")
        .addOption("Mainspieler", "MAIN", "Nutzer erhält permanenten Zugriff")
        .build());
  }

  @NotNull
  public ActionRow getBool() {
    return ActionRow.of(StringSelectMenu.create("bool").setPlaceholder("nein")
        .addOption("true", "ja")
        .addOption("false", "nein")
        .build());
  }

  @NotNull
  public ActionRow getApplicationRoleFields() {
    return ActionRow.of(StringSelectMenu.create("app-role").setPlaceholder("Rolle im Team")
        .addOption("Mainspieler", "MAIN")
        .addOption("Substitude", "SUBSTITUDE")
        .build());
  }

  @NotNull
  public ActionRow getApplicationPosition() {
    return ActionRow.of(StringSelectMenu.create("app-position").setPlaceholder("Position im Team")
        .addOption("Toplane", "TOP", Emote.TOP.getEmoji())
        .addOption("Jungle", "JUNGLE", Emote.JUNGLE.getEmoji())
        .addOption("Middle", "MIDDLE", Emote.MIDDLE.getEmoji())
        .addOption("Bottom", "BOTTOM", Emote.BOTTOM.getEmoji())
        .addOption("Support", "SUPPORT", Emote.SUPPORT.getEmoji())
        .addOption("Mentor", "MENTOR", "Begleiter für das Team")
        .addOption("andere", "OTHER")
        .build());
  }

  @NotNull
  public ActionRow getLolPositionField() {
    return ActionRow.of(StringSelectMenu.create("lol-position").setPlaceholder("Lane")
        .addOption("Toplane", "TOP", Emote.TOP.getEmoji())
        .addOption("Jungle", "JUNGLE", Emote.JUNGLE.getEmoji())
        .addOption("Middle", "MIDDLE", Emote.MIDDLE.getEmoji())
        .addOption("Bottom", "BOTTOM", Emote.BOTTOM.getEmoji())
        .addOption("Support", "UTILITY", Emote.SUPPORT.getEmoji())
        .setRequiredRange(0, 1)
        .build());
  }

  @NotNull
  public ActionRow getLolPositionOrNameField() {
    return ActionRow.of(TextInput.create("lane-or-name", "Lane/Summonername", TextInputStyle.SHORT)
        .setPlaceholder("Lane/Name ...")
        .setRequiredRange(1, 16).build());
  }

  @NotNull
  public ActionRow getStaffPosition() {
    return ActionRow.of(StringSelectMenu.create("app-position").setPlaceholder("Position im Team")
        .addOption("Event", "EVENT_PLANNING", "Event Planung")
        .addOption("Content", "SOCIAL_MEDIA", "Social Media und Casting")
        .addOption("Coach", "COACH", "Begleiter/Coach oder Teamaufbau")
        .build());
  }

  @NotNull
  public ActionRow getScoutingGameTypeField() {
    return ActionRow.of(StringSelectMenu.create("scouting-game-type").setPlaceholder("Gametyp")
        .addOption("nur Prime League", "PRM_ONLY")
        .addOption("PRM & Clash", "PRM_CLASH")
        .addOption("Team Games", "TEAM_GAMES")
        .addOption("alle Games", "MATCHMADE")
        .setRequiredRange(0, 1)
        .build());
  }

  @NotNull
  public ActionRow getAddRemove(boolean add, boolean remove) {
    StringSelectMenu.Builder builder = StringSelectMenu.create("add-remove").setPlaceholder("Typ");
    if (add) builder = builder.addOption("hinzufügen", "add");
    if (remove) builder = builder.addOption("entfernen", "remove");
    return ActionRow.of(builder.build());
  }

  public ActionRow getMemberGroups() {
    final Set<DiscordGroup> assignGroups = new RoleGranter(getInvoker(), target).getMemberGroups();
    return ActionRow.of(StringSelectMenu.create("staff-group-name").setPlaceholder("Nicht Spieler")
        .addOptions(assignGroups.stream().map(group -> SelectOption.of(group.getName(), group.name())).toList())
        .build());
  }

  public ActionRow getGroups() {
    final Set<DiscordGroup> assignGroups = new RoleGranter(getInvoker(), target).getGroups();
    return ActionRow.of(StringSelectMenu.create("group-name").setPlaceholder("Teamname")
        .addOptions(assignGroups.stream().map(group -> SelectOption.of(group.getName(), group.name())).toList())
        .build());
  }

  public ActionRow getDaysField() {
    return ActionRow.of(TextInput.create("days-text", "Tage", TextInputStyle.SHORT).setPlaceholder("0")
        .setRequired(false).setRequiredRange(0, 3).build());
  }

  public ActionRow getMembers() {
    return ActionRow.of(EntitySelectMenu.create("member", EntitySelectMenu.SelectTarget.USER).setMaxValues(1).build());
  }

  public ActionRow getTextField(int length) {
    return ActionRow.of(TextInput.create("description", "Beschreibung", TextInputStyle.PARAGRAPH)
        .setPlaceholder("Beschreibung hinzufügen...")
        .setMaxLength(length)
        .build());
  }

  public ActionRow getTeamIdField(int length) {
    return ActionRow.of(TextInput.create("team-id", "gegnerische TeamID/Name (sofern bekannt)", TextInputStyle.SHORT)
        .setPlaceholder("Teamid hinzufügen...")
        .setMaxLength(length)
        .build());
  }

  public ActionRow getDateField() {
    return ActionRow.of(TextInput.create("date", "Zeitpunkt eintragen", TextInputStyle.SHORT)
        .setPlaceholder("Datum&Zeit hinzufügen...")
        .setMaxLength(15)
        .build());
  }

  public ActionRow getOpggField(boolean home) {
    return ActionRow.of(TextInput.create("op- " + (home ? "home" : "guest"), (home ? "euer" : "deren") + " Multi-Op.gg", TextInputStyle.SHORT)
        .setPlaceholder("Op.gg hinzufügen...")
        .setMaxLength(1000)
        .build());
  }

  public ActionRow getDurationField() {
    return ActionRow.of(TextInput.create("duration", "Dauer in Tagen", TextInputStyle.SHORT)
        .setPlaceholder("365")
        .setMaxLength(3)
        .setRequired(false)
        .build());
  }

  public ActionRow getPageField() {
    return ActionRow.of(TextInput.create("page", "Seite", TextInputStyle.SHORT)
        .setPlaceholder("1")
        .setMaxLength(5)
        .setRequired(false)
        .build());
  }

  public ActionRow getChampionField() {
    return ActionRow.of(TextInput.create("champion", "gespielter Champiom", TextInputStyle.SHORT)
        .setPlaceholder("keine Auswahl")
        .setMaxLength(20)
        .setRequired(false)
        .build());
  }
  //</editor-fold>

  //<editor-fold desc="objects">
  @NotNull
  protected TeamPosition getTeamPosition() {
    final String positionString = Util.nonNull(modalEvent().getValue("app-position")).getAsString();
    return TeamPosition.valueOf(positionString);
  }

  @Nullable
  protected Lane getLolPosition() {
    final String positionString = Util.nonNull(modalEvent().getValue("lol-position")).getAsString();
    return positionString.toEnum(Lane.class);
  }

  @Nullable
  protected Object getLolPositionOrSummonername() {
    final String nameOrLane = Util.avoidNull(modalEvent().getValue("lol-position"), null, ModalMapping::getAsString);
    if (nameOrLane == null) return null;
    final Lane lane = nameOrLane.toEnum(Lane.class);
    return lane != null ? lane : PlayerFactory.getPlayerFromName(nameOrLane);
  }

  @NotNull
  protected ScoutingGameType getScoutingGameType() {
    final String positionString = Util.nonNull(modalEvent().getValue("scouting-game-type")).getAsString();
    return ScoutingGameType.valueOf(positionString);
  }

  @NotNull
  protected TeamRole getTeamRole() {
    final String roleString = Util.nonNull(modalEvent().getValue("app-role")).getAsString();
    return TeamRole.valueOf(roleString);
  }

  protected boolean getBoolValue() {
    final String boolString = Util.nonNull(modalEvent().getValue("bool")).getAsString();
    return boolString.equals("true");
  }

  protected OrgaTeam getTeam() {
    final String teamIdString = Util.nonNull(modalEvent().getValue("team-name")).getAsString();
    return Database.Find.find(OrgaTeam.class, Long.parseLong(teamIdString));
  }

  @Override
  protected DiscordUser getInvoker() {
    final String targetIdString = Util.nonNull(modalEvent().getValue("target-name")).getAsString();
    return Database.Find.find(DiscordUser.class, Integer.parseInt(targetIdString));
  }

  protected DiscordGroup getGroup() {
    final String groupName = Util.nonNull(modalEvent().getValue("group-name")).getAsString();
    return DiscordGroup.valueOf(groupName);
  }

  protected TeamPosition getMemberGroup() {
    final String groupName = Util.nonNull(modalEvent().getValue("staff-group-name")).getAsString();
    return TeamPosition.valueOf(groupName);
  }

  protected int getDays() {
    final String daysString = Util.nonNull(modalEvent().getValue("days-text")).getAsString();
    return Integer.parseInt(daysString);
  }

  protected String getDescription() {
    return Util.nonNull(modalEvent().getValue("description")).getAsString();
  }

  protected Team getTeamIdOrName() {
    final String asString = Util.nonNull(modalEvent().getValue("team-id")).getAsString();
    PRMTeam team;
    if (asString.intValue() == -1) {
      team = TeamFactory.fromAbbreviation(asString);
      if (team == null) team = TeamFactory.fromName(asString);
    } else {
      team = TeamFactory.getTeam(asString.intValue());
    }
    if (team == null) return null;
    new TeamLoader(team).load();
    return team;
  }

  protected LocalDateTime getTime() {
    return Util.avoidNull(modalEvent().getValue("date"), null, mapping -> mapping.getAsString().getDateTime());
  }

  protected Integer getDuration() {
    return Util.avoidNull(modalEvent().getValue("duration"), 365, modalMapping -> modalMapping.getAsString().intValue());
  }

  protected Integer getPage() {
    return Util.avoidNull(modalEvent().getValue("page"), 1, modalMapping -> modalMapping.getAsString().intValue());
  }

  protected Champion getChampion() {
    return Util.avoidNull(modalEvent().getValue("champion"), null, modalMapping -> ChampionFactory.getChampion(modalMapping.getAsString()));
  }

  protected boolean handleTeamsLineup(Participator participator) {
    final String opGg = Util.nonNull(modalEvent().getValue("op- " + (participator.isFirstPick() ? "home" : "guest"))).getAsString();
    return !participator.get().setOrderedLineup(opGg);
  }

  protected Member getMember() {
    return ((EntitySelectInteractionEvent) event).getMentions().getMembers().get(0);
  }
  //</editor-fold>

  private ModalInteractionEvent modalEvent() {
    return ((ModalInteractionEvent) event);
  }
}
