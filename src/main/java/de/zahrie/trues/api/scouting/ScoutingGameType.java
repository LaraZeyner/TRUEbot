package de.zahrie.trues.api.scouting;

import de.zahrie.trues.api.coverage.player.model.PlayerBase;
import de.zahrie.trues.api.coverage.team.model.TeamBase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ScoutingGameType {
  PRM_ONLY("nur Prime League"),
  PRM_CLASH("PRM & Clash"),
  TEAM_GAMES("Team Games"),
  MATCHMADE("alle Games");
  private final String displayName;

  public PlayerScoutingQuery<Object[]> playerQuery(PlayerBase player, int days) {
    return new PlayerScoutingQuery<>(Object[].class, this, days, player);
  }

  public <T> PlayerScoutingQuery<T> playerQuery(Class<T> clazz, PlayerBase player, int days) {
    return new PlayerScoutingQuery<>(clazz, this, days, player);
  }

  public TeamScoutingQuery<Object[]> teamQuery(TeamBase team, int days) {
    return new TeamScoutingQuery<>(Object[].class, this, days, team);
  }

  public <T> TeamScoutingQuery<T> teamQuery(Class<T> clazz, TeamBase team, int days) {
    return new TeamScoutingQuery<>(clazz,this, days, team);
  }
}
