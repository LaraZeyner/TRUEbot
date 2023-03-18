package de.zahrie.trues.api.riot.xayah.types.core.spectator;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.base.Suppliers;
import de.zahrie.trues.api.riot.xayah.types.common.GameMode;
import de.zahrie.trues.api.riot.xayah.types.common.GameType;
import de.zahrie.trues.api.riot.xayah.types.common.Map;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.common.Queue;
import de.zahrie.trues.api.riot.xayah.types.common.Region;
import de.zahrie.trues.api.riot.xayah.types.common.Side;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;
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

public class FeaturedMatch extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.spectator.FeaturedMatch> {
  public class Participant extends de.zahrie.trues.api.riot.xayah.types.core.spectator.Participant {
    @Serial
    private static final long serialVersionUID = -9203624759697672200L;

    private final Supplier<RiotChampion> champion = Suppliers.memoize(() ->
        coreData.getChampionId() == 0 ? null : RiotChampion.withId(coreData.getChampionId()).withPlatform(Platform.withTag(coreData.getPlatform())).get())::get;

    private final Supplier<ProfileIcon> profileIcon = Suppliers.memoize(() ->
        coreData.getProfileIconId() == -1 ? null : ProfileIcon.withId(coreData.getProfileIconId()).withPlatform(Platform.withTag(coreData.getPlatform())).get())::get;

    private final Supplier<Summoner> summoner = Suppliers.memoize(() ->
        coreData.getSummonerName() == null ? null : Summoner.named(coreData.getSummonerName()).withPlatform(Platform.withTag(coreData.getPlatform())).get())::get;

    private final Supplier<SummonerSpell> summonerSpellD = Suppliers.memoize(() ->
        coreData.getSummonerSpellDId() == 0 ? null : SummonerSpell.withId(coreData.getSummonerSpellDId()).withPlatform(Platform.withTag(coreData.getPlatform())).get())::get;

    private final Supplier<SummonerSpell> summonerSpellF = Suppliers.memoize(() ->
        coreData.getSummonerSpellFId() == 0 ? null : SummonerSpell.withId(coreData.getSummonerSpellFId()).withPlatform(Platform.withTag(coreData.getPlatform())).get())::get;

    public Participant(final de.zahrie.trues.api.riot.xayah.types.data.spectator.Participant coreData) {
      super(coreData);
    }

    @Override
    public RiotChampion getChampion() {
      return champion.get();
    }

    @Override
    public ProfileIcon getProfileIcon() {
      return profileIcon.get();
    }

    @Override
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
    public FeaturedMatchTeam getTeam() {
      return coreData.getTeam() == Side.BLUE.getId() ? blueTeam.get() : redTeam.get();
    }

    @Override
    public boolean isBot() {
      return coreData.isBot();
    }
  }

  public class Team extends FeaturedMatchTeam {
    @Serial
    private static final long serialVersionUID = -3789921485244822620L;

    private final Supplier<SearchableList<RiotChampion>> bans = Suppliers.memoize(() ->
        coreData.getBans() == null ? null : SearchableLists.unmodifiableFrom(Champions.withIds(coreData.getBans()).withPlatform(Platform.withTag(coreData.getPlatform())).get()))::get;

    private final Supplier<SearchableList<de.zahrie.trues.api.riot.xayah.types.core.spectator.Participant>> participants = Suppliers.memoize(() ->
        SearchableLists.unmodifiableFrom(FeaturedMatch.this.getParticipants().stream().filter(participant -> participant.getCoreData().getTeam() == coreData.getSide()).collect(Collectors.toCollection(() -> new ArrayList<>(FeaturedMatch.this.getParticipants().size() / 2)))))::get;

    public Team(final de.zahrie.trues.api.riot.xayah.types.data.spectator.Team coreData) {
      super(coreData);
    }

    @Override
    public SearchableList<RiotChampion> getBans() {
      return bans.get();
    }

    @Override
    public SearchableList<de.zahrie.trues.api.riot.xayah.types.core.spectator.Participant> getParticipants() {
      return participants.get();
    }

    @Override
    public Side getSide() {
      return Side.withId(coreData.getSide());
    }
  }

  @Serial
  private static final long serialVersionUID = 1986854843022789219L;

  private final Supplier<FeaturedMatchTeam> blueTeam = Suppliers.memoize(() ->
      coreData.getBlueTeam() == null ? null : new Team(coreData.getBlueTeam()))::get;

  private final Supplier<SearchableList<de.zahrie.trues.api.riot.xayah.types.core.spectator.Participant>> participants = Suppliers.memoize(() -> {
    if (coreData.getPlayers() == null) {
      return null;
    }
    final List<de.zahrie.trues.api.riot.xayah.types.core.spectator.Participant> players = coreData.getPlayers().stream().map(Participant::new).collect(Collectors.toCollection(() -> new ArrayList<>(coreData.getPlayers().size())));
    return SearchableLists.unmodifiableFrom(players);
  })::get;

  private final Supplier<FeaturedMatchTeam> redTeam = Suppliers.memoize(() ->
      coreData.getRedTeam() == null ? null : new Team(coreData.getRedTeam()))::get;

  public FeaturedMatch(final de.zahrie.trues.api.riot.xayah.types.data.spectator.FeaturedMatch coreData) {
    super(coreData);
  }

  public FeaturedMatchTeam getBlueTeam() {
    return blueTeam.get();
  }

  public DateTime getCreationTime() {
    return coreData.getCreationTime();
  }

  public Duration getDuration() {
    return coreData.getDuration();
  }

  public long getId() {
    return coreData.getId();
  }

  public Map getMap() {
    return Map.withId(coreData.getMap());
  }

  public GameMode getMode() {
    return GameMode.valueOf(coreData.getMode());
  }

  public String getObserverEncryptionKey() {
    return coreData.getObserverEncryptionKey();
  }

  @Searchable({Summoner.class, RiotChampion.class, String.class, long.class, int.class})
  public SearchableList<de.zahrie.trues.api.riot.xayah.types.core.spectator.Participant> getParticipants() {
    return participants.get();
  }

  public Platform getPlatform() {
    return Platform.withTag(coreData.getPlatform());
  }

  public Queue getQueue() {
    return Queue.withId(coreData.getQueue());
  }

  public FeaturedMatchTeam getRedTeam() {
    return redTeam.get();
  }

  public Region getRegion() {
    return Platform.withTag(coreData.getPlatform()).getRegion();
  }

  public GameType getType() {
    return GameType.valueOf(coreData.getType());
  }
}
