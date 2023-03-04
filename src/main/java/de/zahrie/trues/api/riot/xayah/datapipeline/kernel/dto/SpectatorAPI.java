package de.zahrie.trues.api.riot.xayah.datapipeline.kernel.dto;

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
import de.zahrie.trues.api.riot.xayah.datapipeline.kernel.dto.Kernel.Configuration;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.dto.spectator.CurrentGameInfo;
import de.zahrie.trues.api.riot.xayah.types.dto.spectator.FeaturedGames;

public class SpectatorAPI extends KernelService {
    public SpectatorAPI(final Configuration config, final HTTPClient client) {
        super(config, client);
    }

    @Get(CurrentGameInfo.class)
    public CurrentGameInfo getCurrentGameInfo(final Map<String, Object> query, final PipelineContext context) {
        final Platform platform = (Platform)query.get("platform");
        final String summonerId = (String)query.get("summonerId");
        Utilities.checkNotNull(platform, "platform", summonerId, "summonerId");

        final String endpoint = "lol/spectator/v4/active-games/by-summoner/" + summonerId;

      return get(CurrentGameInfo.class, endpoint, ImmutableMap.of("platform", platform.getTag()));
    }

    @Get(FeaturedGames.class)
    public FeaturedGames getFeaturedGames(final Map<String, Object> query, final PipelineContext context) {
        final Platform platform = (Platform)query.get("platform");
        Utilities.checkNotNull(platform, "platform");

        final String endpoint = "lol/spectator/v4/featured-games";

      return get(FeaturedGames.class, endpoint, ImmutableMap.of("platform", platform.getTag()));
    }

    @SuppressWarnings("unchecked")
    @GetMany(CurrentGameInfo.class)
    public CloseableIterator<CurrentGameInfo> getManyCurrentGameInfo(final Map<String, Object> query, final PipelineContext context) {
        final Platform platform = (Platform)query.get("platform");
        final Iterable<String> summonerIds = (Iterable<String>)query.get("summonerIds");
        Utilities.checkNotNull(platform, "platform", summonerIds, "summonerIds");

        final Iterator<String> iterator = summonerIds.iterator();
        return CloseableIterators.from(new Iterator<>() {
          @Override
          public boolean hasNext() {
            return iterator.hasNext();
          }

          @Override
          public CurrentGameInfo next() {
            final String summonerId = iterator.next();

            final String endpoint = "lol/spectator/v4/active-games/by-summoner/" + summonerId;

            return get(CurrentGameInfo.class, endpoint, ImmutableMap.of("platform", platform.getTag()));
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }
        });
    }

    @SuppressWarnings("unchecked")
    @GetMany(FeaturedGames.class)
    public CloseableIterator<FeaturedGames> getManyFeaturedGames(final Map<String, Object> query, final PipelineContext context) {
        final Iterable<Platform> platforms = (Iterable<Platform>)query.get("platforms");
        Utilities.checkNotNull(platforms, "platforms");

        final Iterator<Platform> iterator = platforms.iterator();
        return CloseableIterators.from(new Iterator<>() {
          @Override
          public boolean hasNext() {
            return iterator.hasNext();
          }

          @Override
          public FeaturedGames next() {
            final Platform platform = iterator.next();

            final String endpoint = "lol/spectator/v4/featured-games";

            return get(FeaturedGames.class, endpoint, ImmutableMap.of("platform", platform.getTag()));
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }
        });
    }
}
