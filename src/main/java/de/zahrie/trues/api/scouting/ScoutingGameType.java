package de.zahrie.trues.api.scouting;

import de.zahrie.trues.api.coverage.team.model.Team;
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

  public TeamAnalyzer teamQuery(Team team, int days) {
    return team.analyze(this, days);
  }
}
