package de.zahrie.trues.api.riot.xayah.datapipeline.transformers.dtodata;

import org.joda.time.DateTime;

import com.merakianalytics.datapipelines.PipelineContext;
import com.merakianalytics.datapipelines.transformers.AbstractDataTransformer;
import com.merakianalytics.datapipelines.transformers.Transform;
import de.zahrie.trues.api.riot.xayah.types.data.summoner.Summoner;

public class SummonerTransformer extends AbstractDataTransformer {
    @Transform(from = de.zahrie.trues.api.riot.xayah.types.dto.summoner.Summoner.class, to = Summoner.class)
    public Summoner transformer(final de.zahrie.trues.api.riot.xayah.types.dto.summoner.Summoner item, final PipelineContext context) {
        final Summoner summoner = new Summoner();
        summoner.setPuuid(item.getPuuid());
        summoner.setAccountId(item.getAccountId());
        summoner.setId(item.getId());
        summoner.setLevel((int)item.getSummonerLevel());
        summoner.setName(item.getName());
        summoner.setPlatform(item.getPlatform());
        summoner.setProfileIconId(item.getProfileIconId());
        summoner.setUpdated(new DateTime(item.getRevisionDate()));
        return summoner;
    }

    @Transform(from = Summoner.class, to = de.zahrie.trues.api.riot.xayah.types.dto.summoner.Summoner.class)
    public de.zahrie.trues.api.riot.xayah.types.dto.summoner.Summoner transformer(final Summoner item, final PipelineContext context) {
        final de.zahrie.trues.api.riot.xayah.types.dto.summoner.Summoner summoner = new de.zahrie.trues.api.riot.xayah.types.dto.summoner.Summoner();
        summoner.setPuuid(item.getPuuid());
        summoner.setAccountId(item.getAccountId());
        summoner.setId(item.getId());
        summoner.setSummonerLevel(item.getLevel());
        summoner.setName(item.getName());
        summoner.setPlatform(item.getPlatform());
        summoner.setProfileIconId(item.getProfileIconId());
        summoner.setRevisionDate(item.getUpdated().getMillis());
        return summoner;
    }
}
