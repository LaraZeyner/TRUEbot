package de.zahrie.trues.api.coverage.player.model;

import java.time.LocalDateTime;

import de.zahrie.trues.api.coverage.team.model.Team;
import de.zahrie.trues.api.discord.user.DiscordUser;

public interface APlayer {
  String getPuuid();
  String getSummonerName();
  void setSummonerName(String summonerName);
  DiscordUser getDiscordUser();
  void setDiscordUser(DiscordUser discordUser);
  Team getTeam();
  void setTeam(Team team);
  LocalDateTime getUpdated();
  void setUpdated(LocalDateTime updated);
  boolean isPlayed();
}
