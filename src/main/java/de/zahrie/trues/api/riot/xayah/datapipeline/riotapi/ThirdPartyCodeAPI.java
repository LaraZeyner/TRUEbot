package de.zahrie.trues.api.riot.xayah.datapipeline.riotapi;

import java.util.Iterator;
import java.util.Map;

import com.merakianalytics.datapipelines.PipelineContext;
import com.merakianalytics.datapipelines.iterators.CloseableIterator;
import com.merakianalytics.datapipelines.iterators.CloseableIterators;
import com.merakianalytics.datapipelines.sources.Get;
import com.merakianalytics.datapipelines.sources.GetMany;
import de.zahrie.trues.api.riot.xayah.datapipeline.common.HTTPClient;
import de.zahrie.trues.api.riot.xayah.datapipeline.common.Utilities;
import de.zahrie.trues.api.riot.xayah.datapipeline.common.rates.RateLimiter;
import de.zahrie.trues.api.riot.xayah.datapipeline.riotapi.RiotAPI.Configuration;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.dto.thirdpartycode.VerificationString;

public class ThirdPartyCodeAPI extends RiotAPIService {
    public ThirdPartyCodeAPI(final Configuration config, final HTTPClient client, final Map<Platform, RateLimiter> applicationRateLimiters,
        final Map<Platform, Object> applicationRateLimiterLocks) {
        super(config, client, applicationRateLimiters, applicationRateLimiterLocks);
    }

    @SuppressWarnings("unchecked")
    @GetMany(VerificationString.class)
    public CloseableIterator<VerificationString> getManyVerificationString(final Map<String, Object> query, final PipelineContext context) {
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
          public VerificationString next() {
            final String summonerId = iterator.next();

            final String endpoint = "lol/platform/v4/third-party-code/by-summoner/" + summonerId;
            final VerificationString data = get(VerificationString.class, endpoint, platform, "lol/platform/v4/third-party-code/by-summoner/summonerId");
            if (data == null) {
              return null;
            }

            data.setPlatform(platform.getTag());
            data.setSummonerId(summonerId);
            return data;
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }
        });
    }

    @Get(VerificationString.class)
    public VerificationString getVerificationString(final Map<String, Object> query, final PipelineContext context) {
        final Platform platform = (Platform)query.get("platform");
        final String summonerId = (String)query.get("summonerId");
        Utilities.checkNotNull(platform, "platform", summonerId, "summonerId");

        final String endpoint = "lol/platform/v4/third-party-code/by-summoner/" + summonerId;
        final VerificationString data = get(VerificationString.class, endpoint, platform, "lol/platform/v4/third-party-code/by-summoner/summonerId");
        if(data == null) {
            return null;
        }

        data.setPlatform(platform.getTag());
        data.setSummonerId(summonerId);
        return data;
    }
}
