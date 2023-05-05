package de.zahrie.trues.api.riot.matchhistory;

import java.util.Set;

import com.merakianalytics.orianna.types.core.match.Participant;
import com.merakianalytics.orianna.types.core.match.ParticipantStats;

public record KDA(short kills, short deaths, short assists) {
  public static KDA fromParticipant(Participant participant) {
    final ParticipantStats stats = participant.getStats();
    return new KDA((short) stats.getKills(), (short) stats.getDeaths(), (short) stats.getAssists());
  }

  public static KDA sum(Set<KDA> kdas) {
    final int kills = kdas.stream().mapToInt(KDA::kills).sum();
    final int deaths = kdas.stream().mapToInt(KDA::deaths).sum();
    final int assists = kdas.stream().mapToInt(KDA::assists).sum();
    return new KDA((short) kills, (short) deaths, (short) assists);
  }

  @Override
  public String toString() {
    return kills + "/" + deaths + "/" + assists;
  }
}
