package de.zahrie.trues.api.riot.xayah.types.core.thirdpartycode;

import java.io.Serial;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import de.zahrie.trues.api.riot.xayah.Orianna;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.common.Region;
import de.zahrie.trues.api.riot.xayah.types.core.GhostObject;
import de.zahrie.trues.api.riot.xayah.types.core.summoner.Summoner;

public class VerificationString extends GhostObject<de.zahrie.trues.api.riot.xayah.types.data.thirdpartycode.VerificationString> {
    public static final class Builder {
        private final Summoner summoner;

        private Builder(final Summoner summoner) {
            this.summoner = summoner;
        }

        public VerificationString get() {
            final ImmutableMap.Builder<String, Object> builder =
                ImmutableMap.<String, Object> builder().put("platform", summoner.getPlatform()).put("summonerId", summoner.getId());

            return Orianna.getSettings().getPipeline().get(VerificationString.class, builder.build());
        }
    }

    @Serial
    private static final long serialVersionUID = -7674629544327997718L;
    public static final String VERIFICATION_STRING_LOAD_GROUP = "verification-string";

    public static Builder forSummoner(final Summoner summoner) {
        return new Builder(summoner);
    }

    public VerificationString(final de.zahrie.trues.api.riot.xayah.types.data.thirdpartycode.VerificationString coreData) {
        super(coreData, 1);
    }

    @Override
    public boolean exists() {
        if(coreData.getString() == null) {
            load(VERIFICATION_STRING_LOAD_GROUP);
        }
        return coreData.getString() != null;
    }

    @Override
    protected List<String> getLoadGroups() {
        return List.of(VERIFICATION_STRING_LOAD_GROUP);
    }

    public Platform getPlatform() {
        return Platform.withTag(coreData.getPlatform());
    }

    public Region getRegion() {
        return Platform.withTag(coreData.getPlatform()).getRegion();
    }

    public String getString() {
        if(coreData.getString() == null) {
            load(VERIFICATION_STRING_LOAD_GROUP);
        }
        return coreData.getString();
    }

    @Override
    protected void loadCoreData(final String group) {
        final ImmutableMap.Builder<String, Object> builder;
      if (group.equals(VERIFICATION_STRING_LOAD_GROUP)) {
        builder = ImmutableMap.builder();
        if (coreData.getSummonerId() != null) {
          builder.put("summonerId", coreData.getSummonerId());
        }
        if (coreData.getPlatform() != null) {
          builder.put("platform", Platform.withTag(coreData.getPlatform()));
        }
        final de.zahrie.trues.api.riot.xayah.types.data.thirdpartycode.VerificationString data =
            Orianna.getSettings().getPipeline().get(de.zahrie.trues.api.riot.xayah.types.data.thirdpartycode.VerificationString.class, builder.build());
        if (data != null) {
          coreData = data;
        }
      }
    }
}
