package de.zahrie.trues.api.riot.xayah.types.core.league;

import java.util.Arrays;

import com.google.common.collect.ImmutableMap;
import com.merakianalytics.datapipelines.iterators.CloseableIterator;
import com.merakianalytics.datapipelines.iterators.CloseableIterators;
import de.zahrie.trues.api.riot.xayah.Orianna;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.common.Queue;
import de.zahrie.trues.api.riot.xayah.types.common.Region;
import de.zahrie.trues.api.riot.xayah.types.common.Tier;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableList;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableLists;

public abstract class Leagues {
    public static final class Builder {
        private final Iterable<String> ids;
        private Platform platform;
        private boolean streaming = false;

        private Builder(final Iterable<String> ids) {
            this.ids = ids;
        }

        public SearchableList<League> get() {
            if(platform == null) {
                platform = Orianna.getSettings().getDefaultPlatform();
                if(platform == null) {
                    throw new IllegalStateException(
                        "No platform/region was set! Must either set a default platform/region with Orianna.setDefaultPlatform or Orianna.setDefaultRegion, or include a platform/region with the request!");
                }
            }

            final ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object> builder().put("platform", platform).put("leagueIds", ids);

            final CloseableIterator<League> result = Orianna.getSettings().getPipeline().getMany(League.class, builder.build(), streaming);
            return streaming ? SearchableLists.from(CloseableIterators.toLazyList(result)) : SearchableLists.from(CloseableIterators.toList(result));
        }

        public Builder streaming() {
            streaming = true;
            return this;
        }

        public Builder withPlatform(final Platform platform) {
            this.platform = platform;
            return this;
        }

        public Builder withRegion(final Region region) {
            platform = region.getPlatform();
            return this;
        }
    }

    public static final class SelectBuilder {
        public final class SubBuilder {
            private Platform platform;
            private final Iterable<Queue> queues;
            private boolean streaming = false;

            private SubBuilder(final Iterable<Queue> queues) {
                this.queues = queues;
            }

            public SearchableList<League> get() {
                if(platform == null) {
                    platform = Orianna.getSettings().getDefaultPlatform();
                    if(platform == null) {
                        throw new IllegalStateException(
                            "No platform/region was set! Must either set a default platform/region with Orianna.setDefaultPlatform or Orianna.setDefaultRegion, or include a platform/region with the request!");
                    }
                }

                final ImmutableMap.Builder<String, Object> builder =
                    ImmutableMap.<String, Object> builder().put("platform", platform).put("tier", tier).put("queues", queues);

                final CloseableIterator<League> result = Orianna.getSettings().getPipeline().getMany(League.class, builder.build(), streaming);
                return streaming ? SearchableLists.from(CloseableIterators.toLazyList(result)) : SearchableLists.from(CloseableIterators.toList(result));
            }

            public SubBuilder streaming() {
                streaming = true;
                return this;
            }

            public SubBuilder withPlatform(final Platform platform) {
                this.platform = platform;
                return this;
            }

            public SubBuilder withRegion(final Region region) {
                platform = region.getPlatform();
                return this;
            }
        }

        private final Tier tier;

        private SelectBuilder(final Tier tier) {
            this.tier = tier;
        }

        public SubBuilder inQueues(final Iterable<Queue> queues) {
            return new SubBuilder(queues);
        }

        public SubBuilder inQueues(final Queue... queues) {
            return new SubBuilder(Arrays.asList(queues));
        }
    }

    public static SelectBuilder.SubBuilder challengerInQueues(final Iterable<Queue> queues) {
        return new SelectBuilder(Tier.CHALLENGER).inQueues(queues);
    }

    public static SelectBuilder.SubBuilder challengerInQueues(final Queue... queues) {
        return new SelectBuilder(Tier.CHALLENGER).inQueues(queues);
    }

    public static SelectBuilder.SubBuilder grandmasterInQueues(final Iterable<Queue> queues) {
        return new SelectBuilder(Tier.GRANDMASTER).inQueues(queues);
    }

    public static SelectBuilder.SubBuilder grandmasterInQueues(final Queue... queues) {
        return new SelectBuilder(Tier.GRANDMASTER).inQueues(queues);
    }

    public static SelectBuilder.SubBuilder masterInQueues(final Iterable<Queue> queues) {
        return new SelectBuilder(Tier.MASTER).inQueues(queues);
    }

    public static SelectBuilder.SubBuilder masterInQueues(final Queue... queues) {
        return new SelectBuilder(Tier.MASTER).inQueues(queues);
    }

    public static Builder withIds(final Iterable<String> ids) {
        return new Builder(ids);
    }

    public static Builder withIds(final String... ids) {
        return new Builder(Arrays.asList(ids));
    }
}
