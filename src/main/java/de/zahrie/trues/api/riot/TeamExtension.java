package de.zahrie.trues.api.riot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.merakianalytics.orianna.types.core.match.Participant;
import com.merakianalytics.orianna.types.core.match.Team;
import de.zahrie.trues.api.riot.champion.Champion;
import de.zahrie.trues.api.riot.champion.ChampionFactory;

public class TeamExtension {
  public static List<Champion> getBanned(Team team) {
    return team.getBans().stream().map(ChampionFactory::getChampion).toList();
  }
  public static List<Champion> getPicks(Team team) {
    final Supplier<List<Champion>> pickTurns = Suppliers.memoize(() ->
        team.getParticipants().stream().map(Participant::getChampion).map(ChampionFactory::getChampion).toList())::get;
    return new ArrayList<>(pickTurns.get());
  }
}
