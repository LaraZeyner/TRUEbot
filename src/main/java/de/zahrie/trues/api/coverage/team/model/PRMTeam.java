package de.zahrie.trues.api.coverage.team.model;

import java.io.Serial;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Objects;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.league.model.PRMLeague;
import de.zahrie.trues.api.coverage.season.PRMSeason;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.coverage.team.leagueteam.LeagueTeam;
import de.zahrie.trues.api.database.connector.SQLUtils;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.JoinQuery;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@Table(value = "team", department = "prime")
@ExtensionMethod({SQLUtils.class, StringUtils.class})
public class PRMTeam extends Team implements Entity<PRMTeam> {
  @Serial
  private static final long serialVersionUID = -8914567031452407982L;

  private Integer prmId; // prm_id
  private TeamRecord record;

  public PRMTeam(int prmId, String name, String abbreviation) {
    super(name, abbreviation);
    this.setPrmId(prmId);
  }

  public PRMTeam(int id, String name, String abbreviation, LocalDateTime refresh, boolean highlight, Integer lastMMR, Integer orgaTeamId, Integer prmId, TeamRecord record) {
    super(id, name, abbreviation, refresh, highlight, lastMMR, orgaTeamId);
    this.prmId = prmId;
    this.record = record;
  }

  public static PRMTeam get(List<Object> objects) {
    return new PRMTeam(
        (int) objects.get(0),
        (String) objects.get(2),
        (String) objects.get(3),
        (LocalDateTime) objects.get(4),
        (boolean) objects.get(5),
        (Integer) objects.get(6),
        new Query<>(OrgaTeam.class).where("team", objects.get(0)).id(),
        (Integer) objects.get(7),
        objects.get(8) == null ? null : new TeamRecord(objects.get(8).shortValue(), objects.get(9).shortValue(), objects.get(10).shortValue())
    );
  }

  @Override
  public PRMTeam create() {
    if (refresh == null) this.refresh = LocalDateTime.of(1, Month.JANUARY, 1, 0, 0);
    Query<PRMTeam> q = new Query<>(PRMTeam.class).key("prm_id", prmId)
        .col("team_name", name).col("team_abbr", abbreviation).col("refresh", refresh).col("highlight", highlight).col("last_team_mmr", lastMMR);
    if (record != null) q = q.col("total_wins", record.wins()).col("total_losses", record.losses()).col("seasons", record.seasons());
    final PRMTeam team = q.insert(this);
    if (orgaTeam != null) orgaTeam.setTeam(team);
    return team;
  }

  public LeagueTeam getCurrentLeague() {
    final PRMSeason lastPRMSeason = SeasonFactory.getLastPRMSeason();
    if (lastPRMSeason == null) return null;
    return new Query<>(LeagueTeam.class)
        .join(new JoinQuery<>(LeagueTeam.class, League.class))
        .join(new JoinQuery<>(League.class, Stage.class).col("stage"))
        .where("team", this).and("_stage.season", lastPRMSeason)
        .descending("_stage.stage_start").entity();
  }

  @Nullable
  public PRMLeague getLastLeague() {
    return new Query<>(PRMLeague.class, "SELECT _league.* FROM league_team as _leagueteam " +
        "INNER JOIN coverage_group as _league ON _leagueteam.league = _league.coverage_group_id " +
        "INNER JOIN coverage_stage as _stage ON _league.stage = _stage.coverage_stage_id " +
        "WHERE (team = ? and _stage.department <> ? and _league.department = ?) ORDER BY _stage.stage_start DESC LIMIT 1")
        .entity(List.of(this, "Playoffs", "prime"));
  }

  public boolean setScore(League division, String score) {
    final TeamScore teamScore;
    if (score.equals("Disqualifiziert")) {
      teamScore = TeamScore.disqualified();
    } else {
      String place = score.split("\\.")[0];
      if (place.contains(":")) place = place.after(":");
      final short placeInteger = Short.parseShort(place.strip());
      final String wins = score.split("\\(")[1].split("/")[0];
      final short winsInteger = Short.parseShort(wins.strip());
      final String losses = score.split("/")[1].split("\\)")[0];
      final short lossesInteger = Short.parseShort(losses.strip());
      teamScore = new TeamScore(placeInteger, winsInteger, lossesInteger);
    }
    final LeagueTeam currentLeague = getCurrentLeague();
    final boolean toCreate = currentLeague == null || !currentLeague.getLeague().equals(division);
    new LeagueTeam(division, this, teamScore).create();
    return toCreate;
  }

  public void setRecord(String record, short seasons) {
    final String wins = record.split(" / ")[0];
    final short winsInteger = Short.parseShort(wins);
    final String losses = record.split(" / ")[1];
    final short lossesInteger = Short.parseShort(losses);
    this.record = new TeamRecord(seasons, winsInteger, lossesInteger);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof final PRMTeam prmTeam)) return false;
    if (prmId == null) return super.equals(o);
    return prmId.equals(prmTeam.getPrmId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getPrmId());
  }
}