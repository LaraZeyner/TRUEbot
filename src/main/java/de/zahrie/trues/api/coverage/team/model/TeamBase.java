package de.zahrie.trues.api.coverage.team.model;

import java.time.LocalDateTime;
import java.util.List;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.coverage.participator.Participator;
import de.zahrie.trues.api.coverage.player.model.PlayerBase;
import de.zahrie.trues.api.coverage.season.Season;
import de.zahrie.trues.api.coverage.season.signup.SeasonSignup;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Id;
import de.zahrie.trues.api.database.query.JoinQuery;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLField;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table("team")
//TODO (Abgie) 02.05.2023: Rename to Team
public class TeamBase implements ATeam, Id, Comparable<TeamBase> {
  protected int id;
  @EqualsAndHashCode.Include
  protected String name; // team_name
  @EqualsAndHashCode.Include
  protected String abbreviation; // team_abbr
  protected LocalDateTime refresh; // refresh
  protected boolean highlight = false; // highlight
  protected Integer lastMMR; // last_team_mmr
  @Getter(AccessLevel.NONE)
  protected int orgaTeamId;
  @Setter(AccessLevel.NONE)
  protected OrgaTeam orgaTeam;

  public OrgaTeam getOrgaTeam() {
    if (orgaTeam == null) this.orgaTeam = new Query<OrgaTeam>().where("team", this).entity();
    return orgaTeam;
  }

  public void setOrgaTeamId(int orgaTeamId) {
    if (orgaTeamId != this.orgaTeamId) orgaTeam = null;
    this.orgaTeamId = orgaTeamId;
  }

  public TeamBase(String name, String abbreviation) {
    this.name = name;
    this.abbreviation = abbreviation;
  }

  public TeamBase(int id, String name, String abbreviation, LocalDateTime refresh, boolean highlight, Integer lastMMR, Integer orgaTeamId) {
    this.id = id;
    this.name = name;
    this.abbreviation = abbreviation;
    this.refresh = refresh;
    this.highlight = highlight;
    this.lastMMR = lastMMR;
    this.orgaTeamId = orgaTeamId;
  }

  @Override
  public List<Participator> getParticipators() {
    return new Query<Participator>().where("team", this).entityList();
  }

  @Override
  public List<SeasonSignup> getSignups() {
    return new Query<SeasonSignup>().where("team", this).entityList();
  }

  @Override
  public List<PlayerBase> getPlayers() {
    return new Query<PlayerBase>().where("team", this).entityList();
  }

  public void setName(String name) {
    if (orgaTeam != null && !orgaTeam.getNameCreation().equals(name)) {
      orgaTeam.setNameCreation(name);
    }
    this.name = name;
    new Query<TeamBase>().col("team_name", name).update(id);
  }

  public void setAbbreviation(String abbreviation) {
    if (orgaTeam != null && !orgaTeam.getAbbreviation().equals(abbreviation)) {
      orgaTeam.setAbbreviationCreation(abbreviation);
    }
    this.abbreviation = abbreviation;
    new Query<TeamBase>().col("team_abbr", abbreviation).update(id);
  }

  @Override
  public void setRefresh(LocalDateTime refresh) {
    final LocalDateTime refreshUntil = orgaTeam == null ? refresh.plusDays(70) : LocalDateTime.MAX;
    if (refreshUntil.isAfter(this.refresh)) {
      this.refresh = refreshUntil;
      new Query<TeamBase>().col("refresh", refreshUntil).update(id);
    }
  }

  public void setHighlight(boolean highlight) {
    this.highlight = highlight;
    new Query<TeamBase>().col("highlight", highlight).update(id);
  }

  @Override
  public void setLastMMR(Integer lastMMR) {
    this.lastMMR = lastMMR;
    new Query<TeamBase>().col("last_team_mmr", lastMMR).update(id);
  }

  public void setOrgaTeam(OrgaTeam orgaTeam) {
    this.orgaTeam = orgaTeam;
  }

  public void highlight() {
    setHighlight(!this.highlight);
  }

  public String getFullName() {
    return name + " (" + abbreviation + ")";
  }

  public Match nextOrLastMatch() {
    final List<Match> matches = new Query<Participator>().field(SQLField.get("_coverage.coverage_id", Integer.class))
        .join(new JoinQuery<Participator, Match>("coverage", JoinQuery.JoinType.INNER))
        .where("team", this).and("_coverage.active", true)
        .ascending("_coverage.start")
        .convertList(Match.class).stream().toList();
    return matches.stream().filter(match -> match.getStart().isAfter(LocalDateTime.now())).findFirst().orElse(matches.get(matches.size() - 1));
  }

  public MatchManager getMatches() {
    return new MatchManager(this);
  }

  @Nullable
  public SeasonSignup getSignupForSeason(Season season) {
    return new Query<SeasonSignup>().where("team", this).and("season", season).entity();
  }

  @Override
  public int compareTo(@NotNull TeamBase o) {
    if (this instanceof PRMTeam prmTeam && o instanceof PRMTeam oPRM) return Integer.compare(prmTeam.getPrmId(), oPRM.getPrmId());
    return Integer.compare(getId(), o.getId());
  }
}
