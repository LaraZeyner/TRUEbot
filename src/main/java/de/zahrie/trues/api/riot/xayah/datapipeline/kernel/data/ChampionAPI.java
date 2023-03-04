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
import de.zahrie.trues.api.riot.xayah.types.data.champion.ChampionRotation;

public class ChampionAPI extends KernelService {
    public ChampionAPI(final Configuration config, final HTTPClient client) {
        super(config, client);
    }

    @Get(ChampionRotation.class)
    public ChampionRotation getChampionRotation(final Map<String, Object> query, final PipelineContext context) {
        final Platform platform = (Platform)query.get("platform");
        Utilities.checkNotNull(platform, "platform");

        final String endpoint = "lol/platform/v3/champion-rotations";

      return get(ChampionRotation.class, endpoint, ImmutableMap.of("platform", platform.getTag()));
    }

    @SuppressWarnings("unchecked")
    @GetMany(ChampionRotation.class)
    public CloseableIterator<ChampionRotation> getManyChampionRotation(final Map<String, Object> query, final PipelineContext context) {
        final Iterable<Platform> platforms = (Iterable<Platform>)query.get("platforms");
        Utilities.checkNotNull(platforms, "platforms");

        final Iterator<Platform> iterator = platforms.iterator();
        return CloseableIterators.from(new Iterator<>() {
          @Override
          public boolean hasNext() {
            return iterator.hasNext();
          }

          @Override
          public ChampionRotation next() {
            final Platform platform = iterator.next();

            final String endpoint = "lol/platform/v3/champion-rotations";

            return get(ChampionRotation.class, endpoint, ImmutableMap.of("platform", platform.getTag()));
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }
        });
    }
}
