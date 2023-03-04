package de.zahrie.trues.api.riot.xayah.types.core.thirdpartycode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.merakianalytics.datapipelines.iterators.CloseableIterator;
import com.merakianalytics.datapipelines.iterators.CloseableIterators;
import de.zahrie.trues.api.riot.xayah.Orianna;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableList;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableLists;
import de.zahrie.trues.api.riot.xayah.types.core.summoner.Summoner;

public abstract class VerificationStrings {
    public static final class Builder {
        private boolean streaming = false;
        private final Iterable<Summoner> summoners;

        private Builder(final Iterable<Summoner> summoners) {
            this.summoners = summoners;
        }

        public SearchableList<VerificationString> get() {
            final List<String> ids = new ArrayList<>();
            final Iterator<Summoner> iterator = summoners.iterator();
            Summoner summoner = iterator.next();

            if(summoner == null) {
                return SearchableLists.from(Collections.emptyList());
            }

            final Platform platform = summoner.getPlatform();
            ids.add(summoner.getId());
            while(iterator.hasNext()) {
                summoner = iterator.next();

                if(platform != summoner.getPlatform()) {
                    throw new IllegalArgumentException("All summoners must be from the same platform/region!");
                }
                ids.add(summoner.getId());
            }

            final ImmutableMap.Builder<String, Object> builder =
                ImmutableMap.<String, Object> builder().put("platform", platform).put("summonerIds", ids);

            final CloseableIterator<VerificationString> result =
                Orianna.getSettings().getPipeline().getMany(VerificationString.class, builder.build(), streaming);
            return streaming ? SearchableLists.from(CloseableIterators.toLazyList(result)) : SearchableLists.from(CloseableIterators.toList(result));
        }

        public Builder streaming() {
            streaming = true;
            return this;
        }
    }

    public static Builder forSummoners(final Iterable<Summoner> summoners) {
        return new Builder(summoners);
    }

    public static Builder forSummoners(final Summoner... summoners) {
        return new Builder(Arrays.asList(summoners));
    }
}
