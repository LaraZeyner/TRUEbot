package de.zahrie.trues.api.riot.xayah.types.core.match;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.merakianalytics.datapipelines.iterators.CloseableIterator;
import com.merakianalytics.datapipelines.iterators.LazyList;
import de.zahrie.trues.api.riot.xayah.Orianna;
import de.zahrie.trues.api.riot.xayah.types.common.GameMode;
import de.zahrie.trues.api.riot.xayah.types.common.GameType;
import de.zahrie.trues.api.riot.xayah.types.common.Lane;
import de.zahrie.trues.api.riot.xayah.types.common.Map;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.common.Queue;
import de.zahrie.trues.api.riot.xayah.types.common.Region;
import de.zahrie.trues.api.riot.xayah.types.common.Role;
import de.zahrie.trues.api.riot.xayah.types.common.RunePath;
import de.zahrie.trues.api.riot.xayah.types.common.Season;
import de.zahrie.trues.api.riot.xayah.types.common.Side;
import de.zahrie.trues.api.riot.xayah.types.common.Tier;
import de.zahrie.trues.api.riot.xayah.types.core.GhostObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.Searchable;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableList;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableLists;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.Champion;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.Champions;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.Item;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.Items;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.ProfileIcon;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.ReforgedRune;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.SummonerSpell;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.Versions;
import de.zahrie.trues.api.riot.xayah.types.core.summoner.Summoner;
import de.zahrie.trues.api.riot.xayah.types.data.match.MatchReference;

public class Match extends GhostObject<de.zahrie.trues.api.riot.xayah.types.data.match.Match> {
    public static final class Builder {
        private final long id;
        private Platform platform;
        private String tournamentCode;

        private Builder(final long id) {
            this.id = id;
        }

        public Match get() {
            if(platform == null) {
                platform = Orianna.getSettings().getDefaultPlatform();
                if(platform == null) {
                    throw new IllegalStateException(
                        "No platform/region was set! Must either set a default platform/region with Orianna.setDefaultPlatform or Orianna.setDefaultRegion, or include a platform/region with the request!");
                }
            }

            final ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object> builder().put("platform", platform).put("matchId", id);
            if(tournamentCode != null) {
                builder.put("tournamentCode", tournamentCode);
            }

            return Orianna.getSettings().getPipeline().get(Match.class, builder.build());
        }

        public Builder withPlatform(final Platform platform) {
            this.platform = platform;
            return this;
        }

        public Builder withRegion(final Region region) {
            platform = region.getPlatform();
            return this;
        }

        public Builder withTournamentCode(final String tournamentCode) {
            this.tournamentCode = tournamentCode;
            return this;
        }
    }

    public final class Participant extends de.zahrie.trues.api.riot.xayah.types.core.match.Participant {
        @Serial
        private static final long serialVersionUID = -4802669460954679635L;

        private final Supplier<Champion> champion = Suppliers.memoize(() -> {
            if(coreData.getChampionId() == 0) {
                return null;
            }
            Champion.Builder builder = Champion.withId(coreData.getChampionId()).withPlatform(Platform.withTag(coreData.getCurrentPlatform()));
            if(coreData.getVersion() != null) {
                final String version = Versions.withPlatform(Platform.withTag(coreData.getCurrentPlatform())).get().getBestMatch(coreData.getVersion());
              builder.withVersion(version);
            }
            return builder.get();
        });

        private final Supplier<SearchableList<Item>> items = Suppliers.memoize(() -> {
            load(MATCH_LOAD_GROUP);
            if(coreData.getItems() == null) {
                return null;
            }
            final String version = Versions.withPlatform(Platform.withTag(coreData.getCurrentPlatform())).get().getBestMatch(coreData.getVersion());
            return SearchableLists.unmodifiableFrom(
                Items.withIds(coreData.getItems()).withPlatform(Platform.withTag(coreData.getCurrentPlatform())).withVersion(version).get());
        });

        private final Supplier<Summoner> preTransferSummoner = Suppliers.memoize(() -> {
            load(MATCH_LOAD_GROUP);
            if(coreData.getAccountId() == null) {
                return null;
            }
            return Summoner.withAccountId(coreData.getAccountId()).withPlatform(Platform.withTag(coreData.getPlatform())).get();
        });

        private final Supplier<ProfileIcon> profileIcon = Suppliers.memoize(() -> {
            load(MATCH_LOAD_GROUP);
            if(coreData.getProfileIconId() == -1) {
                return null;
            }
            final String version = Versions.withPlatform(Platform.withTag(coreData.getCurrentPlatform())).get().getBestMatch(coreData.getVersion());
            return ProfileIcon.withId(coreData.getProfileIconId()).withPlatform(Platform.withTag(coreData.getCurrentPlatform())).withVersion(version)
                .get();
        });

        private final Supplier<SearchableList<RuneStats>> runeStats = Suppliers.memoize(() -> {
            load(MATCH_LOAD_GROUP);
            if(coreData.getRuneStats() == null) {
                return null;
            }
            final List<RuneStats> runeStats = new ArrayList<>(coreData.getRuneStats().size());
            for(final de.zahrie.trues.api.riot.xayah.types.data.match.RuneStats stats : coreData.getRuneStats()) {
                runeStats.add(new RuneStats(stats));
            }
            return SearchableLists.unmodifiableFrom(runeStats);
        });

        private final Supplier<ParticipantStats> stats = Suppliers.memoize(() -> {
            load(MATCH_LOAD_GROUP);
            if(coreData.getStats() == null) {
                return null;
            }
            return new ParticipantStats(coreData.getStats());
        });

        private final Supplier<Summoner> summoner = Suppliers.memoize(() -> {
            if(coreData.getCurrentAccountId() == null) {
                return null;
            }
            final Summoner summoner =
                Summoner.withAccountId(coreData.getCurrentAccountId()).withPlatform(Platform.withTag(coreData.getCurrentPlatform())).get();
            if(summoner.getCoreData().getName() == null && coreData.getSummonerName() != null) {
                summoner.getCoreData().setName(coreData.getSummonerName());
            }
            if(summoner.getCoreData().getId() == null && coreData.getSummonerId() != null) {
                summoner.getCoreData().setId(coreData.getSummonerId());
            }
            return summoner;
        });

        private final Supplier<SummonerSpell> summonerSpellD = Suppliers.memoize(() -> {
            load(MATCH_LOAD_GROUP);
            if(coreData.getSummonerSpellDId() == 0) {
                return null;
            }
            final String version = Versions.withPlatform(Platform.withTag(coreData.getCurrentPlatform())).get().getBestMatch(coreData.getVersion());
            return SummonerSpell.withId(coreData.getSummonerSpellDId()).withPlatform(Platform.withTag(coreData.getCurrentPlatform())).withVersion(version)
                .get();
        });

        private final Supplier<SummonerSpell> summonerSpellF = Suppliers.memoize(() -> {
            load(MATCH_LOAD_GROUP);
            if(coreData.getSummonerSpellFId() == 0) {
                return null;
            }
            final String version = Versions.withPlatform(Platform.withTag(coreData.getCurrentPlatform())).get().getBestMatch(coreData.getVersion());
            return SummonerSpell.withId(coreData.getSummonerSpellFId()).withPlatform(Platform.withTag(coreData.getCurrentPlatform())).withVersion(version)
                .get();
        });

        private final Supplier<de.zahrie.trues.api.riot.xayah.types.core.match.Team> team =
            Suppliers.memoize(() -> {
                load(MATCH_LOAD_GROUP);
                if(coreData.getTeam() == 0) {
                    return null;
                }
                return coreData.getTeam() == Side.BLUE.getId() ? getBlueTeam() : getRedTeam();
            });

        private final Supplier<ParticipantTimeline> timeline = Suppliers.memoize(() -> {
            load(MATCH_LOAD_GROUP);
            if(coreData.getTimeline() == null) {
                return null;
            }
            return new ParticipantTimeline(coreData.getTimeline());
        });

        private Participant(final de.zahrie.trues.api.riot.xayah.types.data.match.Participant coreData) {
            super(coreData);
        }

        @Override
        @Searchable({Champion.class, String.class, int.class})
        public Champion getChampion() {
            return champion.get();
        }

        @Override
        public Tier getHighestTierInSeason() {
            load(MATCH_LOAD_GROUP);
            return Tier.valueOf(coreData.getHighestTierInSeason());
        }

        @Override
        @Searchable({Item.class, String.class, int.class})
        public SearchableList<Item> getItems() {
            return items.get();
        }

        @Override
        public Lane getLane() {
            if(coreData.getLane() == null) {
                load(MATCH_LOAD_GROUP);
            }
            return Lane.valueOf(coreData.getLane());
        }

        @Override
        public Summoner getPreTransferSummoner() {
            return preTransferSummoner.get();
        }

        @Override
        public RunePath getPrimaryRunePath() {
            load(MATCH_LOAD_GROUP);
            return RunePath.withId(coreData.getPrimaryRunePath());
        }

        @Override
        public ProfileIcon getProfileIcon() {
            return profileIcon.get();
        }

        @Override
        public Role getRole() {
            if(coreData.getRole() == null) {
                load(MATCH_LOAD_GROUP);
            }
            return Role.valueOf(coreData.getRole());
        }

        @Override
        @Searchable({ReforgedRune.class, String.class, int.class})
        public SearchableList<RuneStats> getRuneStats() {
            return runeStats.get();
        }

        @Override
        public RunePath getSecondaryRunePath() {
            load(MATCH_LOAD_GROUP);
            return RunePath.withId(coreData.getSecondaryRunePath());
        }

        @Override
        public ParticipantStats getStats() {
            return stats.get();
        }

        @Override
        @Searchable({Summoner.class, String.class, long.class})
        public Summoner getSummoner() {
            return summoner.get();
        }

        @Override
        @Searchable({SummonerSpell.class, String.class, long.class})
        public SummonerSpell getSummonerSpellD() {
            return summonerSpellD.get();
        }

        @Override
        @Searchable({SummonerSpell.class, String.class, long.class})
        public SummonerSpell getSummonerSpellF() {
            return summonerSpellF.get();
        }

        @Override
        public de.zahrie.trues.api.riot.xayah.types.core.match.Team getTeam() {
            return team.get();
        }

        @Override
        public ParticipantTimeline getTimeline() {
            return timeline.get();
        }
    }

    public final class Team extends de.zahrie.trues.api.riot.xayah.types.core.match.Team {
        @Serial
        private static final long serialVersionUID = -5787154563875265507L;

        private final Supplier<SearchableList<Champion>> bans = Suppliers.memoize(() -> {
            if(coreData.getBans() == null) {
                return null;
            }
            final String version = Versions.withPlatform(Platform.withTag(coreData.getPlatform())).get().getBestMatch(coreData.getVersion());
            return SearchableLists.unmodifiableFrom(
                Champions.withIds(coreData.getBans()).withPlatform(Platform.withTag(coreData.getPlatform())).withVersion(version).get());
        });

        private final Supplier<SearchableList<de.zahrie.trues.api.riot.xayah.types.core.match.Participant>> participants =
            Suppliers.memoize(() -> {
                final List<de.zahrie.trues.api.riot.xayah.types.core.match.Participant> participants =
                    new ArrayList<>(Match.this.getParticipants().size() / 2);
                for(final de.zahrie.trues.api.riot.xayah.types.core.match.Participant participant : Match.this.getParticipants()) {
                    if(participant.getCoreData().getTeam() == coreData.getTeamId()) {
                        participants.add(participant);
                    }
                }
                return SearchableLists.unmodifiableFrom(participants);
            });

        private Team(final de.zahrie.trues.api.riot.xayah.types.data.match.Team coreData) {
            super(coreData);
        }

        @Override
        @Searchable({Champion.class, String.class, int.class})
        public SearchableList<Champion> getBans() {
            return bans.get();
        }

        @Override
        public int getBaronKills() {
            return coreData.getBaronKills();
        }

        @Override
        public int getDominionScore() {
            return coreData.getDominionScore();
        }

        @Override
        public int getDragonKills() {
            return coreData.getDragonKills();
        }

        @Override
        public int getInhibitorKills() {
            return coreData.getInhibitorKills();
        }

        @Override
        @Searchable({Summoner.class, Champion.class, Item.class, SummonerSpell.class, String.class, long.class, int.class})
        public SearchableList<de.zahrie.trues.api.riot.xayah.types.core.match.Participant> getParticipants() {
            return participants.get();
        }

        @Override
        public int getRiftHeraldKills() {
            return coreData.getRiftHeraldKills();
        }

        @Override
        public Side getSide() {
            return Side.withId(coreData.getTeamId());
        }

        @Override
        public int getTowerKills() {
            return coreData.getTowerKills();
        }

        @Override
        public int getVilemawKills() {
            return coreData.getVilemawKills();
        }

        @Override
        public boolean isFirstBaronKiller() {
            return coreData.isFirstBaronKiller();
        }

        @Override
        public boolean isFirstBloodKiller() {
            return coreData.isFirstBloodKiller();
        }

        @Override
        public boolean isFirstDragonKiller() {
            return coreData.isFirstDragonKiller();
        }

        @Override
        public boolean isFirstInhibitorKiller() {
            return coreData.isFirstInhibitorKiller();
        }

        @Override
        public boolean isFirstRiftHeraldKiller() {
            return coreData.isFirstRiftHeraldKiller();
        }

        @Override
        public boolean isFirstTowerKiller() {
            return coreData.isFirstTowerKiller();
        }

        @Override
        public boolean isWinner() {
            return coreData.isWinner();
        }
    }

    public static final String MATCH_LOAD_GROUP = "match";
    @Serial
    private static final long serialVersionUID = -9106364274355437548L;

    private static void replaceData(final de.zahrie.trues.api.riot.xayah.types.data.match.Participant from,
        final de.zahrie.trues.api.riot.xayah.types.data.match.Participant to) {
        to.setAccountId(from.getAccountId());
        to.setChampionId(from.getChampionId());
        to.setCurrentAccountId(from.getCurrentAccountId());
        to.setCurrentPlatform(from.getCurrentPlatform());
        to.setHighestTierInSeason(from.getHighestTierInSeason());
        to.setItems(from.getItems());
        to.setLane(from.getLane());
        to.setMatchHistoryURI(from.getMatchHistoryURI());
        to.setParticipantId(from.getParticipantId());
        to.setPlatform(from.getPlatform());
        to.setPrimaryRunePath(from.getPrimaryRunePath());
        to.setProfileIconId(from.getProfileIconId());
        to.setRole(from.getRole());
        to.setRuneStats(from.getRuneStats());
        to.setSecondaryRunePath(from.getSecondaryRunePath());
        to.setStats(from.getStats());
        to.setSummonerId(from.getSummonerId());
        to.setSummonerName(from.getSummonerName());
        to.setSummonerSpellDId(from.getSummonerSpellDId());
        to.setSummonerSpellFId(from.getSummonerSpellFId());
        to.setTeam(from.getTeam());
        to.setTimeline(from.getTimeline());
        to.setVersion(from.getVersion());
    }

    private static de.zahrie.trues.api.riot.xayah.types.data.match.Match toMatchData(final MatchReference reference) {
        final de.zahrie.trues.api.riot.xayah.types.data.match.Match coreData = new de.zahrie.trues.api.riot.xayah.types.data.match.Match();
        coreData.setQueue(reference.getQueue());
        coreData.setSeason(reference.getSeason());
        coreData.setCreationTime(reference.getCreationTime());
        coreData.setId(reference.getId());
        coreData.setPlatform(reference.getPlatform());

        final de.zahrie.trues.api.riot.xayah.types.data.match.Participant participant = new de.zahrie.trues.api.riot.xayah.types.data.match.Participant();
        participant.setCurrentAccountId(reference.getAccountId());
        participant.setCurrentPlatform(reference.getPlatform());
        participant.setChampionId(reference.getChampionId());
        participant.setLane(reference.getLane());
        participant.setRole(reference.getRole());

        final List<de.zahrie.trues.api.riot.xayah.types.data.match.Participant> participants = new ArrayList<>(1);
        participants.add(participant);
        coreData.setParticipants(participants);

        return coreData;
    }

    public static Builder withId(final long id) {
        return new Builder(id);
    }

    private final Supplier<de.zahrie.trues.api.riot.xayah.types.core.match.Team> blueTeam =
        Suppliers.memoize(() -> {
            load(MATCH_LOAD_GROUP);
            if(coreData.getBlueTeam() == null) {
                return null;
            }
            return new Team(coreData.getBlueTeam());
        });

    private final boolean fromReference;

    private final Supplier<SearchableList<de.zahrie.trues.api.riot.xayah.types.core.match.Participant>> participants =
        Suppliers.memoize(new Supplier<>() {
          @Override
          public SearchableList<de.zahrie.trues.api.riot.xayah.types.core.match.Participant> get() {
            if (!fromReference) {
              load(MATCH_LOAD_GROUP);
              if (coreData.getParticipants() == null) {
                return null;
              }
              final List<de.zahrie.trues.api.riot.xayah.types.core.match.Participant> participants = new ArrayList<>(coreData.getParticipants().size());
              for (final de.zahrie.trues.api.riot.xayah.types.data.match.Participant participant : coreData.getParticipants()) {
                participants.add(new Participant(participant));
              }
              return SearchableLists.unmodifiableFrom(participants);
            } else {
              if (coreData.getParticipants() == null) {
                return null;
              }
              final CloseableIterator<de.zahrie.trues.api.riot.xayah.types.core.match.Participant> iterator =
                  new CloseableIterator<>() {
                    private ListIterator<de.zahrie.trues.api.riot.xayah.types.data.match.Participant> iterator = coreData.getParticipants().listIterator();

                    @Override
                    public void close() {
                    }

                    @Override
                    public boolean hasNext() {
                      if (iterator.hasNext()) {
                        return true;
                      }
                      load(MATCH_LOAD_GROUP);
                      iterator = coreData.getParticipants().listIterator(iterator.nextIndex());
                      return iterator.hasNext();
                    }

                    @Override
                    public Participant next() {
                      if (!hasNext()) {
                        return null;
                      }
                      return new Participant(iterator.next());
                    }

                    @Override
                    public void remove() {
                      throw new UnsupportedOperationException();
                    }
                  };
              return SearchableLists.unmodifiableFrom(new LazyList<>(iterator));
            }
          }
        });

    private final Supplier<de.zahrie.trues.api.riot.xayah.types.core.match.Team> redTeam =
        Suppliers.memoize(() -> {
            load(MATCH_LOAD_GROUP);
            if(coreData.getRedTeam() == null) {
                return null;
            }
            return new Team(coreData.getRedTeam());
        });

    private final Supplier<Timeline> timeline = Suppliers.memoize(() -> {
        if(coreData.getId() == 0L) {
            return null;
        }
        return Timeline.withId(coreData.getId()).withPlatform(Platform.withTag(coreData.getPlatform())).get();
    });

    public Match(final de.zahrie.trues.api.riot.xayah.types.data.match.Match coreData) {
        super(coreData, 1);
        fromReference = false;
    }

    public Match(final MatchReference reference) {
        super(toMatchData(reference), 1);
        fromReference = true;
    }

    @Override
    public boolean exists() {
        if(coreData.getType() == null) {
            load(MATCH_LOAD_GROUP);
        }
        return coreData.getType() != null;
    }

    public de.zahrie.trues.api.riot.xayah.types.core.match.Team getBlueTeam() {
        return blueTeam.get();
    }

    public DateTime getCreationTime() {
        if(coreData.getCreationTime() == null) {
            load(MATCH_LOAD_GROUP);
        }
        return coreData.getCreationTime();
    }

    public Duration getDuration() {
        if(coreData.getDuration() == null) {
            load(MATCH_LOAD_GROUP);
        }
        return coreData.getDuration();
    }

    public long getId() {
        return coreData.getId();
    }

    @Override
    protected List<String> getLoadGroups() {
        return List.of(MATCH_LOAD_GROUP);
    }

    public Map getMap() {
        if(coreData.getMap() == 0) {
            load(MATCH_LOAD_GROUP);
        }
        return Map.withId(coreData.getMap());
    }

    public GameMode getMode() {
        if(coreData.getMode() == null) {
            load(MATCH_LOAD_GROUP);
        }
        return GameMode.valueOf(coreData.getMode());
    }

    @Searchable({Summoner.class, Champion.class, Item.class, String.class, long.class, int.class})
    public SearchableList<de.zahrie.trues.api.riot.xayah.types.core.match.Participant> getParticipants() {
        return participants.get();
    }

    public Platform getPlatform() {
        return Platform.withTag(coreData.getPlatform());
    }

    public Queue getQueue() {
        if(coreData.getQueue() == 0) {
            load(MATCH_LOAD_GROUP);
        }
        return Queue.withId(coreData.getQueue());
    }

    public de.zahrie.trues.api.riot.xayah.types.core.match.Team getRedTeam() {
        return redTeam.get();
    }

    public Region getRegion() {
        return Platform.withTag(coreData.getPlatform()).getRegion();
    }

    public Season getSeason() {
        if(coreData.getSeason() == 0) {
            load(MATCH_LOAD_GROUP);
        }
        return Season.withId(coreData.getSeason());
    }

    public Timeline getTimeline() {
        return timeline.get();
    }

    public String getTournamentCode() {
        return coreData.getTournamentCode();
    }

    public GameType getType() {
        if(coreData.getType() == null) {
            load(MATCH_LOAD_GROUP);
        }
        return GameType.valueOf(coreData.getType());
    }

    public String getVersion() {
        if(coreData.getVersion() == null) {
            load(MATCH_LOAD_GROUP);
        }
        return coreData.getVersion();
    }

    public boolean isRemake() {
        if(coreData.getDuration() == null) {
            load(MATCH_LOAD_GROUP);
        }
        return coreData.getDuration().isShorterThan(Duration.standardMinutes(5));
    }

    @Override
    protected void loadCoreData(final String group) {
        final ImmutableMap.Builder<String, Object> builder;
      if (group.equals(MATCH_LOAD_GROUP)) {
        builder = ImmutableMap.builder();
        if (coreData.getPlatform() != null) {
          builder.put("platform", Platform.withTag(coreData.getPlatform()));
        }
        if (coreData.getId() != 0L) {
          builder.put("matchId", coreData.getId());
        }
        if (coreData.getTournamentCode() != null) {
          builder.put("tournamentCode", coreData.getTournamentCode());
        }
        if (!fromReference) {
          final de.zahrie.trues.api.riot.xayah.types.data.match.Match data =
              Orianna.getSettings().getPipeline().get(de.zahrie.trues.api.riot.xayah.types.data.match.Match.class, builder.build());
          if (data != null) {
            coreData = data;
          }
        } else {
          final de.zahrie.trues.api.riot.xayah.types.data.match.Match data =
              Orianna.getSettings().getPipeline().get(de.zahrie.trues.api.riot.xayah.types.data.match.Match.class, builder.build());
          if (data != null) {
            final de.zahrie.trues.api.riot.xayah.types.data.match.Participant fromReference = coreData.getParticipants().get(0);
            final Iterator<de.zahrie.trues.api.riot.xayah.types.data.match.Participant> iterator = data.getParticipants().iterator();
            while (iterator.hasNext()) {
              final de.zahrie.trues.api.riot.xayah.types.data.match.Participant participant = iterator.next();
              if (participant.getCurrentAccountId().equals(fromReference.getCurrentAccountId())) {
                replaceData(participant, fromReference);
                iterator.remove();
                break;
              }
            }
            data.getParticipants().add(0, fromReference);
            coreData = data;
          }
        }
      }
    }
}
