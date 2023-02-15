package de.zahrie.trues.util.io.request;

import de.zahrie.trues.util.io.cfg.JSON;

/**
 * Created by Lara on 14.02.2023 for TRUEbot
 */
public enum URLType {
  LEAGUE,
  MATCH,
  PLAYER,
  TEAM;

  public String getUrlName() {
    final var apiConfig = JSON.fromFile("apis.json");
    final var primeConfig = apiConfig.getJSONObject("gamesports");
    final var primeEndpoints = primeConfig.getJSONObject("endpoints");
    return primeEndpoints.getString(this.name().toLowerCase());
  }

}
