package de.zahrie.trues.api.discord.builder.modal;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import de.zahrie.trues.api.community.application.TeamPosition;
import de.zahrie.trues.api.community.application.TeamRole;
import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.team.TeamFactory;
import de.zahrie.trues.api.coverage.team.TeamLoader;
import de.zahrie.trues.api.coverage.team.model.PrimeTeam;
import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.datatypes.calendar.Time;
import de.zahrie.trues.api.datatypes.symbol.StringExtention;
import de.zahrie.trues.api.discord.group.DiscordGroup;
import de.zahrie.trues.api.discord.group.RoleGranter;
import de.zahrie.trues.api.discord.message.Emote;
import de.zahrie.trues.api.discord.user.DiscordUser;
import de.zahrie.trues.database.Database;
import de.zahrie.trues.discord.scouting.Scouting;
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
import org.jetbrains.annotations.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ExtensionMethod(StringExtention.class)
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
    final List<OrgaTeam> orgaTeams = Database.Find.findList(OrgaTeam.class);
    return ActionRow.of(StringSelectMenu.create("team-name").setPlaceholder("Teamname")
        .addOptions(orgaTeams.stream().map(team -> SelectOption.of(team.getName(), String.valueOf(team.getId()))).toList())
        .build());
  }

  @NotNull
  public ActionRow getApplicationRoleField() {
    return ActionRow.of(StringSelectMenu.create("app-role").setPlaceholder("Rolle im Team")
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
  //</editor-fold>

  //<editor-fold desc="objects">
  @NotNull
  protected TeamPosition getTeamPosition() {
    final String positionString = Objects.requireNonNull(modalEvent().getValue("app-position")).getAsString();
    return TeamPosition.valueOf(positionString);
  }


  @NotNull
  protected Scouting.ScoutingGameType getScoutingGameType() {
    final String positionString = Objects.requireNonNull(modalEvent().getValue("scouting-game-type")).getAsString();
    return Scouting.ScoutingGameType.valueOf(positionString);
  }

  @NotNull
  protected TeamRole getTeamRole() {
    final String roleString = Objects.requireNonNull(modalEvent().getValue("app-role")).getAsString();
    return TeamRole.valueOf(roleString);
  }

  protected boolean getBoolValue() {
    final String boolString = Objects.requireNonNull(modalEvent().getValue("bool")).getAsString();
    return boolString.equals("true");
  }

  protected OrgaTeam getTeam() {
    final String teamIdString = Objects.requireNonNull(modalEvent().getValue("team-name")).getAsString();
    return Database.Find.find(OrgaTeam.class, Long.parseLong(teamIdString));
  }

  @Override
  protected DiscordUser getInvoker() {
    final String targetIdString = Objects.requireNonNull(modalEvent().getValue("target-name")).getAsString();
    return Database.Find.find(DiscordUser.class, Integer.parseInt(targetIdString));
  }

  protected DiscordGroup getGroup() {
    final String groupName = Objects.requireNonNull(modalEvent().getValue("group-name")).getAsString();
    return DiscordGroup.valueOf(groupName);
  }

  protected TeamPosition getMemberGroup() {
    final String groupName = Objects.requireNonNull(modalEvent().getValue("staff-group-name")).getAsString();
    return TeamPosition.valueOf(groupName);
  }

  protected int getDays() {
    final String daysString = Objects.requireNonNull(modalEvent().getValue("days-text")).getAsString();
    return Integer.parseInt(daysString);
  }

  protected String getDescription() {
    return Objects.requireNonNull(modalEvent().getValue("description")).getAsString();
  }

  protected Team getTeamIdOrName() {
    final String asString = Objects.requireNonNull(modalEvent().getValue("team-id")).getAsString();
    PrimeTeam team;
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

  protected Time getTime() {
    return Time.of(Objects.requireNonNull(modalEvent().getValue("date")).getAsString());
  }

  protected Integer getDuration() {
    return Util.avoidNull(modalEvent().getValue("duration"), 365, modalMapping -> modalMapping.getAsString().intValue());
  }

  protected Integer getPage() {
    return Util.avoidNull(modalEvent().getValue("page"), 1, modalMapping -> modalMapping.getAsString().intValue());
  }

  protected boolean handleTeamsLineup(Participator participator) {
    final String opGg = Objects.requireNonNull(modalEvent().getValue("op- " + (participator.isFirstPick() ? "home" : "guest"))).getAsString();
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
