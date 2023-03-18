package de.zahrie.trues.api.riot.xayah.types.core.spectator;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.Champions;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.ProfileIcon;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.RiotChampion;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.SummonerSpell;
import de.zahrie.trues.api.riot.xayah.types.core.summoner.Summoner;
import org.joda.time.DateTime;
import org.joda.time.Duration;

public class CurrentMatch extends GhostObject<de.zahrie.trues.api.riot.xayah.types.data.spectator.CurrentMatch> {
  public static final class Builder {
    private final Summoner summoner;

    private Builder(final Summoner summoner) {
      this.summoner = summoner;
    }

    public CurrentMatch get() {
      final ImmutableMap.Builder<String, Object> builder =
          ImmutableMap.<String, Object>builder().put("platform", summoner.getPlatform()).put("summonerId", summoner.getId());

      return Orianna.getSettings().getPipeline().get(CurrentMatch.class, builder.build());
    }
  }

  public class Player extends de.zahrie.trues.api.riot.xayah.types.core.spectator.Player {
    @Serial
    private static final long serialVersionUID = 4959827158399514603L;

    private final Supplier<RiotChampion> champion = Suppliers.memoize(() ->
        coreData.getChampionId() == 0 ? null : RiotChampion.withId(coreData.getChampionId()).withPlatform(Platform.withTag(coreData.getPlatform())).get())::get;

    private final Supplier<List<GameCustomizationObject>> customizationObjects = Suppliers.memoize(() ->
        coreData.getCustomizationObjects() == null ? null : coreData.getCustomizationObjects().stream().map(GameCustomizationObject::new).toList())::get;

    private final Supplier<ProfileIcon> profileIcon = Suppliers.memoize(() ->
        coreData.getProfileIconId() == -1 ? null : ProfileIcon.withId(coreData.getProfileIconId()).withPlatform(Platform.withTag(coreData.getPlatform())).get())::get;

    private final Supplier<Runes> runes = Suppliers.memoize(() ->
        coreData.getRunes() == null ? null : new Runes(coreData.getRunes()))::get;

    private final Supplier<Summoner> summoner = Suppliers.memoize(() -> {
      if (coreData.getSummonerId() == null) {
        return null;
      }
      final Summoner summoner = Summoner.withId(coreData.getSummonerId()).withPlatform(Platform.withTag(coreData.getPlatform())).get();
      if (summoner.getCoreData().getName() == null) {
        summoner.getCoreData().setName(coreData.getSummonerName());
      }
      return summoner;
    })::get;

    private final Supplier<SummonerSpell> summonerSpellD = Suppliers.memoize(() ->
        coreData.getSummonerSpellDId() == 0 ? null : SummonerSpell.withId(coreData.getSummonerSpellDId()).withPlatform(Platform.withTag(coreData.getPlatform())).get())::get;

    private final Supplier<SummonerSpell> summonerSpellF = Suppliers.memoize(() ->
        coreData.getSummonerSpellFId() == 0 ? null : SummonerSpell.withId(coreData.getSummonerSpellFId()).withPlatform(Platform.withTag(coreData.getPlatform())).get())::get;

    public Player(final de.zahrie.trues.api.riot.xayah.types.data.spectator.Player coreData) {
      super(coreData);
    }

    @Override
    @Searchable({RiotChampion.class, String.class, int.class})
    public RiotChampion getChampion() {
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
    public CurrentMatchTeam getTeam() {
      return coreData.getTeam() == Side.BLUE.getId() ? blueTeam.get() : redTeam.get();
    }

    @Override
    public boolean isBot() {
      return coreData.isBot();
    }
  }

  public class Team extends CurrentMatchTeam {
    @Serial
    private static final long serialVersionUID = 6283190060704502909L;

    private final Supplier<SearchableList<RiotChampion>> bans = Suppliers.memoize(() ->
        coreData.getBans() == null ? null : SearchableLists.unmodifiableFrom(Champions.withIds(coreData.getBans()).withPlatform(Platform.withTag(coreData.getPlatform())).get()))::get;

    private final Supplier<SearchableList<de.zahrie.trues.api.riot.xayah.types.core.spectator.Player>> participants =
        Suppliers.memoize(() ->
            SearchableLists.unmodifiableFrom(CurrentMatch.this.getParticipants().stream().filter(participant -> participant.getCoreData().getTeam() == coreData.getSide()).collect(Collectors.toCollection(() -> new ArrayList<>(CurrentMatch.this.getParticipants().size() / 2)))))::get;

    public Team(final de.zahrie.trues.api.riot.xayah.types.data.spectator.Team coreData) {
      super(coreData);
    }

    @Override
    public SearchableList<RiotChampion> getBans() {
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

  private final Supplier<CurrentMatchTeam> blueTeam = Suppliers.memoize(() -> {
    load(CURRENT_GAME_LOAD_GROUP);
    return coreData.getBlueTeam() == null ? null : new Team(coreData.getBlueTeam());
  })::get;

  private final Supplier<SearchableList<de.zahrie.trues.api.riot.xayah.types.core.spectator.Player>> participants =
      Suppliers.memoize(() -> {
        load(CURRENT_GAME_LOAD_GROUP);
        if (coreData.getPlayers() == null) {
          return null;
        }
        final List<de.zahrie.trues.api.riot.xayah.types.core.spectator.Player> players = coreData.getPlayers().stream().map(Player::new).collect(Collectors.toCollection(() -> new ArrayList<>(coreData.getPlayers().size())));
        return SearchableLists.unmodifiableFrom(players);
      })::get;

  private final Supplier<CurrentMatchTeam> redTeam = Suppliers.memoize(() -> {
    load(CURRENT_GAME_LOAD_GROUP);
    return coreData.getRedTeam() == null ? null : new Team(coreData.getRedTeam());
  })::get;

  private final Supplier<Summoner> summoner = Suppliers.memoize(() ->
      coreData.getSummonerId() == null ? null : Summoner.withId(coreData.getSummonerId()).withPlatform(Platform.withTag(coreData.getPlatform())).get())::get;

  public CurrentMatch(final de.zahrie.trues.api.riot.xayah.types.data.spectator.CurrentMatch coreData) {
    super(coreData, 1);
  }

  @Override
  public boolean exists() {
    if (coreData.getId() == 0L) {
      load(CURRENT_GAME_LOAD_GROUP);
    }
    return coreData.getId() != 0L;
  }

  public CurrentMatchTeam getBlueTeam() {
    return blueTeam.get();
  }

  public DateTime getCreationTime() {
    if (coreData.getCreationTime() == null) {
      load(CURRENT_GAME_LOAD_GROUP);
    }
    return coreData.getCreationTime();
  }

  public Duration getDuration() {
    if (coreData.getDuration() == null) {
      load(CURRENT_GAME_LOAD_GROUP);
    }
    return coreData.getDuration();
  }

  public long getId() {
    if (coreData.getId() == 0L) {
      load(CURRENT_GAME_LOAD_GROUP);
    }
    return coreData.getId();
  }

  @Override
  protected List<String> getLoadGroups() {
    return List.of(CURRENT_GAME_LOAD_GROUP);
  }

  public Map getMap() {
    if (coreData.getMap() == 0) {
      load(CURRENT_GAME_LOAD_GROUP);
    }
    return Map.withId(coreData.getMap());
  }

  public GameMode getMode() {
    if (coreData.getMode() == null) {
      load(CURRENT_GAME_LOAD_GROUP);
    }
    return GameMode.valueOf(coreData.getMode());
  }

  public String getObserverEncryptionKey() {
    if (coreData.getObserverEncryptionKey() == null) {
      load(CURRENT_GAME_LOAD_GROUP);
    }
    return coreData.getObserverEncryptionKey();
  }

  @Searchable({Summoner.class, RiotChampion.class, String.class, long.class, int.class})
  public SearchableList<de.zahrie.trues.api.riot.xayah.types.core.spectator.Player> getParticipants() {
    return participants.get();
  }

  public Platform getPlatform() {
    return Platform.withTag(coreData.getPlatform());
  }

  public Queue getQueue() {
    if (coreData.getQueue() == 0) {
      load(CURRENT_GAME_LOAD_GROUP);
    }
    return Queue.withId(coreData.getQueue());
  }

  public CurrentMatchTeam getRedTeam() {
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
    if (coreData.getType() == null) {
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
