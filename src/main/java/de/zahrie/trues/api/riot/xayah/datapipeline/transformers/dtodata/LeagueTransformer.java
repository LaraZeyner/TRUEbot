package de.zahrie.trues.api.riot.xayah.datapipeline.transformers.dtodata;

import java.util.ArrayList;
import java.util.List;

import com.merakianalytics.datapipelines.PipelineContext;
import com.merakianalytics.datapipelines.transformers.AbstractDataTransformer;
import com.merakianalytics.datapipelines.transformers.Transform;
import de.zahrie.trues.api.riot.xayah.types.data.league.League;
import de.zahrie.trues.api.riot.xayah.types.data.league.LeagueEntry;
import de.zahrie.trues.api.riot.xayah.types.data.league.LeaguePositions;
import de.zahrie.trues.api.riot.xayah.types.data.league.Series;
import de.zahrie.trues.api.riot.xayah.types.dto.league.LeagueItem;
import de.zahrie.trues.api.riot.xayah.types.dto.league.LeagueList;
import de.zahrie.trues.api.riot.xayah.types.dto.league.MiniSeries;

public class LeagueTransformer extends AbstractDataTransformer {
    @Transform(from = de.zahrie.trues.api.riot.xayah.types.dto.league.LeagueEntry.class, to = LeagueEntry.class)
    public LeagueEntry transform(final de.zahrie.trues.api.riot.xayah.types.dto.league.LeagueEntry item, final PipelineContext context) {
        final LeagueEntry entry = new LeagueEntry();
        entry.setLeagueId(item.getLeagueId());
        entry.setDivision(item.getRank());
        entry.setFreshBlood(item.isFreshBlood());
        entry.setInactive(item.isInactive());
        entry.setLeaguePoints(item.getLeaguePoints());
        entry.setLosses(item.getLosses());
        entry.setOnHotStreak(item.isHotStreak());
        entry.setPlatform(item.getPlatform());
        if(item.getMiniSeries() != null) {
            entry.setPromos(transform(item.getMiniSeries(), context));
        }
        entry.setQueue(item.getQueueType());
        entry.setSummonerId(item.getSummonerId());
        entry.setSummonerName(item.getSummonerName());
        entry.setTier(item.getTier());
        entry.setVeteran(item.isVeteran());
        entry.setWins(item.getWins());
        return entry;
    }

    @Transform(from = de.zahrie.trues.api.riot.xayah.types.dto.league.LeagueItem.class, to = LeagueEntry.class)
    public LeagueEntry transform(final de.zahrie.trues.api.riot.xayah.types.dto.league.LeagueItem item, final PipelineContext context) {
        final LeagueEntry entry = new LeagueEntry();
        entry.setLeagueId((String)context.get("leagueId"));
        entry.setDivision(item.getRank());
        entry.setFreshBlood(item.isFreshBlood());
        entry.setInactive(item.isInactive());
        entry.setLeaguePoints(item.getLeaguePoints());
        entry.setLosses(item.getLosses());
        entry.setOnHotStreak(item.isHotStreak());
        entry.setPlatform((String)context.get("platform"));
        if(item.getMiniSeries() != null) {
            entry.setPromos(transform(item.getMiniSeries(), context));
        }
        entry.setQueue((String)context.get("queue"));
        entry.setSummonerId(item.getSummonerId());
        entry.setSummonerName(item.getSummonerName());
        entry.setTier((String)context.get("tier"));
        entry.setVeteran(item.isVeteran());
        entry.setWins(item.getWins());
        return entry;
    }

    @Transform(from = de.zahrie.trues.api.riot.xayah.types.dto.league.LeaguePositions.class, to = LeaguePositions.class)
    public LeaguePositions transform(final de.zahrie.trues.api.riot.xayah.types.dto.league.LeaguePositions item, final PipelineContext context) {
        final LeaguePositions positions = new LeaguePositions(item.size());
        for(final de.zahrie.trues.api.riot.xayah.types.dto.league.LeagueEntry entry : item) {
            positions.add(transform(entry, context));
        }
        positions.setPlatform(item.getPlatform());
        positions.setSummonerId(item.getSummonerId());
        return positions;
    }

    @Transform(from = League.class, to = LeagueList.class)
    public LeagueList transform(final League item, final PipelineContext context) {
        final LeagueList list = new LeagueList();
        final List<LeagueItem> entries = new ArrayList<>(item.size());
        for(final LeagueEntry entry : item) {
            entries.add(transformToLeagueItem(entry, context));
        }
        list.setEntries(entries);
        list.setLeagueId(item.getId());
        list.setName(item.getName());
        list.setPlatform(item.getPlatform());
        list.setQueue(item.getQueue());
        list.setTier(item.getTier());
        return list;
    }

    @Transform(from = LeagueEntry.class, to = de.zahrie.trues.api.riot.xayah.types.dto.league.LeagueEntry.class)
    public de.zahrie.trues.api.riot.xayah.types.dto.league.LeagueEntry transform(final LeagueEntry item, final PipelineContext context) {
        final de.zahrie.trues.api.riot.xayah.types.dto.league.LeagueEntry entry = new de.zahrie.trues.api.riot.xayah.types.dto.league.LeagueEntry();
        entry.setLeagueId(item.getLeagueId());
        entry.setRank(item.getDivision());
        entry.setFreshBlood(item.isFreshBlood());
        entry.setInactive(item.isInactive());
        entry.setLeaguePoints(item.getLeaguePoints());
        entry.setLosses(item.getLosses());
        entry.setHotStreak(item.isOnHotStreak());
        entry.setPlatform(item.getPlatform());
        if(item.getPromos() != null) {
            entry.setMiniSeries(transform(item.getPromos(), context));
        }
        entry.setQueueType(item.getQueue());
        entry.setSummonerId(item.getSummonerId());
        entry.setSummonerName(item.getSummonerName());
        entry.setTier(item.getTier());
        entry.setVeteran(item.isVeteran());
        entry.setWins(item.getWins());
        return entry;
    }

    @Transform(from = LeagueList.class, to = League.class)
    public League transform(final LeagueList item, final PipelineContext context) {
        final Object previousPlatform = context.put("platform", item.getPlatform());
        final Object previousQueue = context.put("queue", item.getQueue());
        final Object previousTier = context.put("tier", item.getTier());
        final Object previousLeagueId = context.put("leagueId", item.getLeagueId());
        final League league = new League(item.getEntries().size());
        for(final LeagueItem entry : item.getEntries()) {
            league.add(transform(entry, context));
        }
        league.setId(item.getLeagueId());
        league.setName(item.getName());
        league.setPlatform(item.getPlatform());
        league.setQueue(item.getQueue());
        league.setTier(item.getTier());
        context.put("platform", previousPlatform);
        context.put("queue", previousQueue);
        context.put("tier", previousTier);
        context.put("leagueId", previousLeagueId);
        return league;
    }

    @Transform(from = LeaguePositions.class, to = de.zahrie.trues.api.riot.xayah.types.dto.league.LeaguePositions.class)
    public de.zahrie.trues.api.riot.xayah.types.dto.league.LeaguePositions transform(final LeaguePositions item, final PipelineContext context) {
        final de.zahrie.trues.api.riot.xayah.types.dto.league.LeaguePositions positions = new de.zahrie.trues.api.riot.xayah.types.dto.league.LeaguePositions();
        for(final LeagueEntry position : item) {
            positions.add(transform(position, context));
        }
        positions.setPlatform(item.getPlatform());
        positions.setSummonerId(item.getSummonerId());
        return positions;
    }

    @Transform(from = MiniSeries.class, to = Series.class)
    public Series transform(final MiniSeries item, final PipelineContext context) {
        final Series series = new Series();
        series.setLosses(item.getLosses());
        series.setProgress(item.getProgress());
        series.setWinsRequired(item.getTarget());
        series.setWins(item.getWins());
        return series;
    }

    @Transform(from = Series.class, to = MiniSeries.class)
    public MiniSeries transform(final Series item, final PipelineContext context) {
        final MiniSeries series = new MiniSeries();
        series.setLosses(item.getLosses());
        series.setProgress(item.getProgress());
        series.setTarget(item.getWinsRequired());
        series.setWins(item.getWins());
        return series;
    }

    @Transform(from = LeagueEntry.class, to = LeagueItem.class)
    public LeagueItem transformToLeagueItem(final LeagueEntry item, final PipelineContext context) {
        final LeagueItem entry = new LeagueItem();
        entry.setRank(item.getDivision());
        entry.setFreshBlood(item.isFreshBlood());
        entry.setInactive(item.isInactive());
        entry.setLeaguePoints(item.getLeaguePoints());
        entry.setLosses(item.getLosses());
        entry.setHotStreak(item.isOnHotStreak());
        if(item.getPromos() != null) {
            entry.setMiniSeries(transform(item.getPromos(), context));
        }
        entry.setSummonerId(item.getSummonerId());
        entry.setSummonerName(item.getSummonerName());
        entry.setVeteran(item.isVeteran());
        entry.setWins(item.getWins());
        return entry;
    }
}
