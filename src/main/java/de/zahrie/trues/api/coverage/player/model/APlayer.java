package de.zahrie.trues.api.coverage.player.model;

import java.time.LocalDateTime;

import de.zahrie.trues.api.coverage.team.model.TeamBase;
import de.zahrie.trues.api.discord.user.DiscordUser;

public interface APlayer {
  String getPuuid();
  String getSummonerName();
  void setSummonerName(String summonerName);
  DiscordUser getDiscordUser();
  void setDiscordUser(DiscordUser discordUser);
  TeamBase getTeam();
  void setTeam(TeamBase team);
  LocalDateTime getUpdated();
  void setUpdated(LocalDateTime updated);
  boolean isPlayed();
}
