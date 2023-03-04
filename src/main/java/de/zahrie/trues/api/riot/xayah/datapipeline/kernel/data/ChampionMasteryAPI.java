package de.zahrie.trues.api.riot.xayah.datapipeline.kernel.data;

import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.merakianalytics.datapipelines.PipelineContext;
import com.merakianalytics.datapipelines.iterators.CloseableIterator;
import com.merakianalytics.datapipelines.iterators.CloseableIterators;
import com.merakianalytics.datapipelines.sources.Get;
import com.merakianalytics.datapipelines.sources.GetMany;
import de.zahrie.trues.api.riot.xayah.datapipeline.common.HTTPClient;
import de.zahrie.trues.api.riot.xayah.datapipeline.common.Utilities;
import de.zahrie.trues.api.riot.xayah.datapipeline.kernel.data.Kernel.Configuration;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.data.championmastery.ChampionMasteries;
import de.zahrie.trues.api.riot.xayah.types.data.championmastery.ChampionMastery;
import de.zahrie.trues.api.riot.xayah.types.data.championmastery.ChampionMasteryScore;

public class ChampionMasteryAPI extends KernelService {
  public ChampionMasteryAPI(final Configuration config, final HTTPClient client) {
    super(config, client);
  }

  @Get(ChampionMastery.class)
  public ChampionMastery getChampionMastery(final Map<String, Object> query, final PipelineContext context) {
    final Platform platform = (Platform) query.get("platform");
    final String summonerId = (String) query.get("summonerId");
    final Number championId = (Number) query.get("championId");
    Utilities.checkNotNull(platform, "platform", summonerId, "summonerId", championId, "championId");

    final String endpoint = "lol/champion-mastery/v4/champion-masteries/by-summoner/" + summonerId + "/by-champion/" + championId;

    return get(ChampionMastery.class, endpoint, ImmutableMap.of("platform", platform.getTag()));
  }

  @Get(ChampionMasteries.class)
  public ChampionMasteries getChampionMasteryList(final Map<String, Object> query, final PipelineContext context) {
    final Platform platform = (Platform) query.get("platform");
    final String summonerId = (String) query.get("summonerId");
    Utilities.checkNotNull(platform, "platform", summonerId, "summonerId");

    final String endpoint = "lol/champion-mastery/v4/champion-masteries/by-summoner/" + summonerId;

    return get(ChampionMasteries.class, endpoint, ImmutableMap.of("platform", platform.getTag()));
  }

  @Get(ChampionMasteryScore.class)
  public ChampionMasteryScore getChampionMasteryScore(final Map<String, Object> query, final PipelineContext context) {
    final Platform platform = (Platform) query.get("platform");
    final String summonerId = (String) query.get("summonerId");
    Utilities.checkNotNull(platform, "platform", summonerId, "summonerId");

    final String endpoint = "lol/champion-mastery/v4/scores/by-summoner/" + summonerId;

    return get(ChampionMasteryScore.class, endpoint, ImmutableMap.of("platform", platform.getTag()));
  }

  @SuppressWarnings("unchecked")
  @GetMany(ChampionMastery.class)
  public CloseableIterator<ChampionMastery> getManyChampionMastery(final Map<String, Object> query, final PipelineContext context) {
    final Platform platform = (Platform) query.get("platform");
    final String summonerId = (String) query.get("summonerId");
    final Iterable<Number> championIds = (Iterable<Number>) query.get("championIds");
    Utilities.checkNotNull(platform, "platform", summonerId, "summonerId", championIds, "championIds");

    final String endpoint = "lol/champion-mastery/v4/champion-masteries/by-summoner/" + summonerId;
    final ChampionMasteries data = get(ChampionMasteries.class, endpoint, ImmutableMap.of("platform", platform.getTag()));
    if (data == null) {
      return null;
    }

    final Iterator<Number> iterator = championIds.iterator();
    return CloseableIterators.from(new Iterator<>() {
      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public ChampionMastery next() {
        final Number championId = iterator.next();
        for (final ChampionMastery mastery : data) {
          if (mastery.getChampionId() == championId.longValue()) {
            return mastery;
          }
        }
        return null;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    });
  }

  @SuppressWarnings("unchecked")
  @GetMany(ChampionMasteries.class)
  public CloseableIterator<ChampionMasteries> getManyChampionMasteryList(final Map<String, Object> query, final PipelineContext context) {
    final Platform platform = (Platform) query.get("platform");
    final Iterable<String> summonerIds = (Iterable<String>) query.get("summonerIds");
    Utilities.checkNotNull(platform, "platform", summonerIds, "summonerIds");

    final Iterator<String> iterator = summonerIds.iterator();
    return CloseableIterators.from(new Iterator<>() {
      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public ChampionMasteries next() {
        final String summonerId = iterator.next();

        final String endpoint = "lol/champion-mastery/v4/champion-masteries/by-summoner/" + summonerId;

        return get(ChampionMasteries.class, endpoint, ImmutableMap.of("platform", platform.getTag()));
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    });
  }

  @SuppressWarnings("unchecked")
  @GetMany(ChampionMasteryScore.class)
  public CloseableIterator<ChampionMasteryScore> getManyChampionMasteryScore(final Map<String, Object> query, final PipelineContext context) {
    final Platform platform = (Platform) query.get("platform");
    final Iterable<String> summonerIds = (Iterable<String>) query.get("summonerIds");
    Utilities.checkNotNull(platform, "platform", summonerIds, "summonerIds");

    final Iterator<String> iterator = summonerIds.iterator();
    return CloseableIterators.from(new Iterator<>() {
      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public ChampionMasteryScore next() {
        final String summonerId = iterator.next();

        final String endpoint = "lol/champion-mastery/v4/scores/by-summoner/" + summonerId;

        return get(ChampionMasteryScore.class, endpoint, ImmutableMap.of("platform", platform.getTag()));
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    });
  }
}
