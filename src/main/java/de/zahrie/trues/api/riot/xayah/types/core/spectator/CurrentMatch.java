package de.zahrie.trues.api.riot.xayah.types.core.spectator;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import de.zahrie.trues.api.riot.xayah.Orianna;
import de.zahrie.trues.api.riot.xayah.types.common.GameMode;
import de.zahrie.trues.api.riot.xayah.types.common.GameType;
import de.zahrie.trues.api.riot.xayah.types.common.Map;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.common.Queue;
import de.zahrie.trues.api.riot.xayah.types.common.Region;
import de.zahrie.trues.api.riot.xayah.types.common.Side;
import de.zahrie.trues.api.riot.xayah.types.core.GhostObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.Searchable;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableList;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableLists;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.Champion;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.Champions;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.ProfileIcon;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.SummonerSpell;
import de.zahrie.trues.api.riot.xayah.types.core.summoner.Summoner;

public class CurrentMatch extends GhostObject<de.zahrie.trues.api.riot.xayah.types.data.spectator.CurrentMatch> {
    public static final class Builder {
        private final Summoner summoner;

        private Builder(final Summoner summoner) {
            this.summoner = summoner;
        }

        public CurrentMatch get() {
            final ImmutableMap.Builder<String, Object> builder =
                ImmutableMap.<String, Object> builder().put("platform", summoner.getPlatform()).put("summonerId", summoner.getId());

            return Orianna.getSettings().getPipeline().get(CurrentMatch.class, builder.build());
        }
    }

    public class Player extends de.zahrie.trues.api.riot.xayah.types.core.spectator.Player {
        @Serial
        private static final long serialVersionUID = 4959827158399514603L;

        private final Supplier<Champion> champion = Suppliers.memoize(() -> {
            if(coreData.getChampionId() == 0) {
                return null;
            }
            return Champion.withId(coreData.getChampionId()).withPlatform(Platform.withTag(coreData.getPlatform())).get();
        });

        private final Supplier<List<GameCustomizationObject>> customizationObjects = Suppliers.memoize(() -> {
            if(coreData.getCustomizationObjects() == null) {
                return null;
            }
            final List<GameCustomizationObject> objects = new ArrayList<>(coreData.getCustomizationObjects().size());
            for(final de.zahrie.trues.api.riot.xayah.types.data.spectator.GameCustomizationObject object : coreData.getCustomizationObjects()) {
                objects.add(new GameCustomizationObject(object));
            }
            return Collections.unmodifiableList(objects);
        });

        private final Supplier<ProfileIcon> profileIcon = Suppliers.memoize(() -> {
            if(coreData.getProfileIconId() == -1) {
                return null;
            }
            return ProfileIcon.withId(coreData.getProfileIconId()).withPlatform(Platform.withTag(coreData.getPlatform())).get();
        });

        private final Supplier<Runes> runes = Suppliers.memoize(() -> {
            if(coreData.getRunes() == null) {
                return null;
            }
            return new Runes(coreData.getRunes());
        });

        private final Supplier<Summoner> summoner = Suppliers.memoize(() -> {
            if(coreData.getSummonerId() == null) {
                return null;
            }
            final Summoner summoner = Summoner.withId(coreData.getSummonerId()).withPlatform(Platform.withTag(coreData.getPlatform())).get();
            if(summoner.getCoreData().getName() == null) {
                summoner.getCoreData().setName(coreData.getSummonerName());
            }
            return summoner;
        });

        private final Supplier<SummonerSpell> summonerSpellD = Suppliers.memoize(() -> {
            if(coreData.getSummonerSpellDId() == 0) {
                return null;
            }
            return SummonerSpell.withId(coreData.getSummonerSpellDId()).withPlatform(Platform.withTag(coreData.getPlatform())).get();
        });

        private final Supplier<SummonerSpell> summonerSpellF = Suppliers.memoize(() -> {
            if(coreData.getSummonerSpellFId() == 0) {
                return null;
            }
            return SummonerSpell.withId(coreData.getSummonerSpellFId()).withPlatform(Platform.withTag(coreData.getPlatform())).get();
        });

        public Player(final de.zahrie.trues.api.riot.xayah.types.data.spectator.Player coreData) {
            super(coreData);
        }

        @Override
        @Searchable({Champion.class, String.class, int.class})
        public Champion getChampion() {
            return champion.get();
        }

        @Override
        public List<GameCustomizationObject> getCustomizationObjects() {
            return customizationObjects.get();
        }

        @Override
        public ProfileIcon getProfileIcon() {
            return profileIcon.get();
        }

        @Override
        public Runes getRunes() {
            return runes.get();
        }

        @Override
        @Searchable({Summoner.class, String.class, long.class})
        public Summoner getSummoner() {
            return summoner.get();
        }

        @Override
        public SummonerSpell getSummonerSpellD() {
            return summonerSpellD.get();
        }

        @Override
        public SummonerSpell getSummonerSpellF() {
            return summonerSpellF.get();
        }

        @Override
        public de.zahrie.trues.api.riot.xayah.types.core.spectator.CurrentMatchTeam getTeam() {
            return coreData.getTeam() == Side.BLUE.getId() ? blueTeam.get() : redTeam.get();
        }

        @Override
        public boolean isBot() {
            return coreData.isBot();
        }
    }

    public class Team extends de.zahrie.trues.api.riot.xayah.types.core.spectator.CurrentMatchTeam {
        @Serial
        private static final long serialVersionUID = 6283190060704502909L;

        private final Supplier<SearchableList<Champion>> bans = Suppliers.memoize(() -> {
            if(coreData.getBans() == null) {
                return null;
            }
            return SearchableLists.unmodifiableFrom(Champions.withIds(coreData.getBans()).withPlatform(Platform.withTag(coreData.getPlatform())).get());
        });

        private final Supplier<SearchableList<de.zahrie.trues.api.riot.xayah.types.core.spectator.Player>> participants =
            Suppliers.memoize(() -> {
                final List<de.zahrie.trues.api.riot.xayah.types.core.spectator.Player> participants =
                    new ArrayList<>(CurrentMatch.this.getParticipants().size() / 2);
                for(final de.zahrie.trues.api.riot.xayah.types.core.spectator.Player participant : CurrentMatch.this.getParticipants()) {
                    if(participant.getCoreData().getTeam() == coreData.getSide()) {
                        participants.add(participant);
                    }
                }
                return SearchableLists.unmodifiableFrom(participants);
            });

        public Team(final de.zahrie.trues.api.riot.xayah.types.data.spectator.Team coreData) {
            super(coreData);
        }

        @Override
        public SearchableList<Champion> getBans() {
            return bans.get();
        }

        @Override
        public SearchableList<de.zahrie.trues.api.riot.xayah.types.core.spectator.Player> getParticipants() {
            return participants.get();
        }

        @Override
        public Side getSide() {
            return Side.withId(coreData.getSide());
        }
    }

    public static final String CURRENT_GAME_LOAD_GROUP = "current-game";
    @Serial
    private static final long serialVersionUID = 2151849959267002960L;

    public static Builder forSummoner(final Summoner summoner) {
        return new Builder(summoner);
    }

    private final Supplier<de.zahrie.trues.api.riot.xayah.types.core.spectator.CurrentMatchTeam> blueTeam =
        Suppliers.memoize(() -> {
            load(CURRENT_GAME_LOAD_GROUP);
            if(coreData.getBlueTeam() == null) {
                return null;
            }
            return new Team(coreData.getBlueTeam());
        });

    private final Supplier<SearchableList<de.zahrie.trues.api.riot.xayah.types.core.spectator.Player>> participants =
        Suppliers.memoize(() -> {
            load(CURRENT_GAME_LOAD_GROUP);
            if(coreData.getPlayers() == null) {
                return null;
            }
            final List<de.zahrie.trues.api.riot.xayah.types.core.spectator.Player> players = new ArrayList<>(coreData.getPlayers().size());
            for(final de.zahrie.trues.api.riot.xayah.types.data.spectator.Player player : coreData.getPlayers()) {
                players.add(new Player(player));
            }
            return SearchableLists.unmodifiableFrom(players);
        });

    private final Supplier<de.zahrie.trues.api.riot.xayah.types.core.spectator.CurrentMatchTeam> redTeam =
        Suppliers.memoize(() -> {
            load(CURRENT_GAME_LOAD_GROUP);
            if(coreData.getRedTeam() == null) {
                return null;
            }
            return new Team(coreData.getRedTeam());
        });

    private final Supplier<Summoner> summoner = Suppliers.memoize(() -> {
        if(coreData.getSummonerId() == null) {
            return null;
        }
        return Summoner.withId(coreData.getSummonerId()).withPlatform(Platform.withTag(coreData.getPlatform())).get();
    });

    public CurrentMatch(final de.zahrie.trues.api.riot.xayah.types.data.spectator.CurrentMatch coreData) {
        super(coreData, 1);
    }

    @Override
    public boolean exists() {
        if(coreData.getId() == 0L) {
            load(CURRENT_GAME_LOAD_GROUP);
        }
        return coreData.getId() != 0L;
    }

    public de.zahrie.trues.api.riot.xayah.types.core.spectator.CurrentMatchTeam getBlueTeam() {
        return blueTeam.get();
    }

    public DateTime getCreationTime() {
        if(coreData.getCreationTime() == null) {
            load(CURRENT_GAME_LOAD_GROUP);
        }
        return coreData.getCreationTime();
    }

    public Duration getDuration() {
        if(coreData.getDuration() == null) {
            load(CURRENT_GAME_LOAD_GROUP);
        }
        return coreData.getDuration();
    }

    public long getId() {
        if(coreData.getId() == 0L) {
            load(CURRENT_GAME_LOAD_GROUP);
        }
        return coreData.getId();
    }

    @Override
    protected List<String> getLoadGroups() {
        return List.of(CURRENT_GAME_LOAD_GROUP);
    }

    public Map getMap() {
        if(coreData.getMap() == 0) {
            load(CURRENT_GAME_LOAD_GROUP);
        }
        return Map.withId(coreData.getMap());
    }

    public GameMode getMode() {
        if(coreData.getMode() == null) {
            load(CURRENT_GAME_LOAD_GROUP);
        }
        return GameMode.valueOf(coreData.getMode());
    }

    public String getObserverEncryptionKey() {
        if(coreData.getObserverEncryptionKey() == null) {
            load(CURRENT_GAME_LOAD_GROUP);
        }
        return coreData.getObserverEncryptionKey();
    }

    @Searchable({Summoner.class, Champion.class, String.class, long.class, int.class})
    public SearchableList<de.zahrie.trues.api.riot.xayah.types.core.spectator.Player> getParticipants() {
        return participants.get();
    }

    public Platform getPlatform() {
        return Platform.withTag(coreData.getPlatform());
    }

    public Queue getQueue() {
        if(coreData.getQueue() == 0) {
            load(CURRENT_GAME_LOAD_GROUP);
        }
        return Queue.withId(coreData.getQueue());
    }

    public de.zahrie.trues.api.riot.xayah.types.core.spectator.CurrentMatchTeam getRedTeam() {
        return redTeam.get();
    }

    public Region getRegion() {
        return Platform.withTag(coreData.getPlatform()).getRegion();
    }

    @Searchable({Summoner.class, String.class, long.class})
    public Summoner getSummoner() {
        return summoner.get();
    }

    public GameType getType() {
        if(coreData.getType() == null) {
            load(CURRENT_GAME_LOAD_GROUP);
        }
        return GameType.valueOf(coreData.getType());
    }

    @Override
    protected void loadCoreData(final String group) {
        final ImmutableMap.Builder<String, Object> builder;
      if (group.equals(CURRENT_GAME_LOAD_GROUP)) {
        builder = ImmutableMap.builder();
        if (coreData.getPlatform() != null) {
          builder.put("platform", Platform.withTag(coreData.getPlatform()));
        }
        if (coreData.getSummonerId() != null) {
          builder.put("summonerId", coreData.getSummonerId());
        }
        final de.zahrie.trues.api.riot.xayah.types.data.spectator.CurrentMatch data =
            Orianna.getSettings().getPipeline().get(de.zahrie.trues.api.riot.xayah.types.data.spectator.CurrentMatch.class, builder.build());
        if (data != null) {
          coreData = data;
        }
      }
    }
}
