package de.zahrie.trues.api.coverage.team.model;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Objects;

import de.zahrie.trues.api.community.orgateam.OrgaTeam;
import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.league.model.LeagueBase;
import de.zahrie.trues.api.coverage.league.model.PRMLeague;
import de.zahrie.trues.api.coverage.season.PRMSeason;
import de.zahrie.trues.api.coverage.season.SeasonFactory;
import de.zahrie.trues.api.coverage.stage.model.Stage;
import de.zahrie.trues.api.coverage.team.leagueteam.LeagueTeam;
import de.zahrie.trues.api.database.connector.Table;
import de.zahrie.trues.api.database.query.Condition;
import de.zahrie.trues.api.database.query.Entity;
import de.zahrie.trues.api.database.query.JoinQuery;
import de.zahrie.trues.api.database.query.Query;
import de.zahrie.trues.api.database.query.SQLField;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@Table(value = "team", department = "prime")
public class PRMTeam extends TeamBase implements Entity<PRMTeam> {
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

  public static PRMTeam get(Object[] objects) {
    return new PRMTeam(
        (int) objects[0],
        (String) objects[2],
        (String) objects[3],
        (LocalDateTime) objects[4],
        (boolean) objects[5],
        (Integer) objects[6],
        new Query<OrgaTeam>().where("team", objects[0]).id(),
        (Integer) objects[7],
        new TeamRecord((short) objects[8], (short) objects[9], (short) objects[10])
    );
  }

  @Override
  public PRMTeam create() {
    final PRMTeam team = new Query<PRMTeam>().key("department", "prime").key("prm_id", prmId)
        .col("team_name", name).col("team_abbr", abbreviation).col("refresh", refresh).col("highlight", highlight)
        .col("last_team_mmr", lastMMR).col("total_wins", record.wins()).col("total_losses", record.losses())
        .col("seasons", record.seasons())
        .insert(this);
    if (orgaTeam != null) orgaTeam.setTeam(team);
    return team;
  }

  public LeagueTeam getCurrentLeague() {
    final PRMSeason lastPRMSeason = SeasonFactory.getLastPRMSeason();
    if (lastPRMSeason == null) return null;
    return new Query<LeagueTeam>()
        .join(new JoinQuery<LeagueTeam, League>("league", JoinQuery.JoinType.INNER))
        .join(new JoinQuery<League, Stage>("_league.stage", JoinQuery.JoinType.INNER))
        .where("team", this).and("_stage.season", lastPRMSeason.getId())
        .descending("_stage.stage_start").entity();
  }

  @Nullable
  public PRMLeague getLastLeague() {
    return new Query<LeagueTeam>()
        .field(SQLField.get("league", Integer.class))
        .join(new JoinQuery<LeagueTeam, League>("league", JoinQuery.JoinType.INNER))
        .join(new JoinQuery<League, Stage>("_league.stage", JoinQuery.JoinType.INNER))
        .where("team", this).and(Condition.Comparer.NOT_EQUAL, "_stage.department", "Playoffs")
        .descending("_league._stage.startTime").convert(PRMLeague.class);
  }

  public void setScore(LeagueBase division, String score) {
    final String place = score.split("\\.")[0];
    final short placeInteger = Short.parseShort(place);
    final String wins = score.split("\\(")[1].split("/")[0];
    final short winsInteger = Short.parseShort(wins);
    final String losses = score.split("/")[1].split("\\)")[0];
    final short lossesInteger = Short.parseShort(losses);
    final TeamScore teamScore = new TeamScore(placeInteger, winsInteger, lossesInteger);
    new LeagueTeam(division, this, teamScore).create();
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