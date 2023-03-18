package de.zahrie.trues.api.riot.xayah.types.core.match;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import de.zahrie.trues.api.riot.xayah.Orianna;
import de.zahrie.trues.api.riot.xayah.types.common.AscensionType;
import de.zahrie.trues.api.riot.xayah.types.common.BuildingType;
import de.zahrie.trues.api.riot.xayah.types.common.EventType;
import de.zahrie.trues.api.riot.xayah.types.common.LaneType;
import de.zahrie.trues.api.riot.xayah.types.common.LevelUpType;
import de.zahrie.trues.api.riot.xayah.types.common.MonsterSubType;
import de.zahrie.trues.api.riot.xayah.types.common.MonsterType;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.common.Point;
import de.zahrie.trues.api.riot.xayah.types.common.Region;
import de.zahrie.trues.api.riot.xayah.types.common.Side;
import de.zahrie.trues.api.riot.xayah.types.common.Skill;
import de.zahrie.trues.api.riot.xayah.types.common.TurretType;
import de.zahrie.trues.api.riot.xayah.types.common.WardType;
import de.zahrie.trues.api.riot.xayah.types.core.GhostObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableList;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableLists;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.Item;
import org.joda.time.Duration;

public class Timeline extends GhostObject.ListProxy<Frame, de.zahrie.trues.api.riot.xayah.types.data.match.Frame, de.zahrie.trues.api.riot.xayah.types.data.match.Timeline> {
  public static final class Builder {
    private final long id;
    private Platform platform;

    private Builder(final long id) {
      this.id = id;
    }

    public Timeline get() {
      if (platform == null) {
        platform = Orianna.getSettings().getDefaultPlatform();
        if (platform == null) {
          throw new IllegalStateException(
              "No platform/region was set! Must either set a default platform/region with Orianna.setDefaultPlatform or Orianna.setDefaultRegion, or include a platform/region with the request!");
        }
      }

      final ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object>builder().put("platform", platform).put("matchId", id);

      return Orianna.getSettings().getPipeline().get(Timeline.class, builder.build());
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

  public class Event extends de.zahrie.trues.api.riot.xayah.types.core.match.Event {
    @Serial
    private static final long serialVersionUID = -245827734492071363L;

    private final java.util.function.Supplier<Item> after = Suppliers.memoize(() ->
        coreData.getAfterId() == 0 ? null : Item.withId(coreData.getAfterId()).get())::get;

    private final Supplier<SearchableList<MatchParticipant>> assistingParticipants = Suppliers.memoize(new Supplier<>() {
      @Override
      public SearchableList<MatchParticipant> get() {
        if (coreData.getAssistingParticipants() == null) {
          return null;
        }
        if (coreData.getAssistingParticipants().isEmpty()) {
          return SearchableLists.empty();
        }
        match.get().getVersion(); // Force match load so participants have IDs
        final List<MatchParticipant> assistingMatchParticipants = match.get().getParticipants().stream().filter(matchParticipant -> coreData.getAssistingParticipants().contains(matchParticipant.getCoreData().getParticipantId())).collect(Collectors.toCollection(() -> new ArrayList<>(coreData.getAssistingParticipants().size())));
        return SearchableLists.unmodifiableFrom(assistingMatchParticipants);
      }
    });

    private final java.util.function.Supplier<Item> before = Suppliers.memoize(() ->
        coreData.getBeforeId() == 0 ? null : Item.withId(coreData.getBeforeId()).get())::get;

    private final Supplier<MatchParticipant> creator = Suppliers.memoize(new Supplier<>() {
      @Override
      public MatchParticipant get() {
        if (coreData.getCreatorId() == 0) {
          return null;
        }
        match.get().getVersion(); // Force match load so participants have IDs
        return match.get().getParticipants().stream().filter(matchParticipant -> matchParticipant.getCoreData().getParticipantId() == coreData.getCreatorId()).findFirst().orElse(null);
      }
    });

    private final java.util.function.Supplier<Item> item = Suppliers.memoize(() ->
        coreData.getItemId() == 0 ? null : Item.withId(coreData.getItemId()).get())::get;

    private final Supplier<MatchParticipant> killer = Suppliers.memoize(new Supplier<>() {
      @Override
      public MatchParticipant get() {
        if (coreData.getKillerId() == 0) {
          return null;
        }
        match.get().getVersion(); // Force match load so participants have IDs
        return match.get().getParticipants().stream().filter(matchParticipant -> matchParticipant.getCoreData().getParticipantId() == coreData.getKillerId()).findFirst().orElse(null);
      }
    });

    private final Supplier<MatchParticipant> participant = Suppliers.memoize(new Supplier<>() {
      @Override
      public MatchParticipant get() {
        if (coreData.getParticipantId() == 0) {
          return null;
        }
        match.get().getVersion(); // Force match load so participants have IDs
        return match.get().getParticipants().stream().filter(matchParticipant -> matchParticipant.getCoreData().getParticipantId() == coreData.getParticipantId()).findFirst().orElse(null);
      }
    });

    private final java.util.function.Supplier<Position> position = Suppliers.memoize(() ->
        coreData.getPosition() == null ? null : new Position(coreData.getPosition()))::get;

    private final Supplier<Team> team = Suppliers.memoize(new Supplier<>() {
      @Override
      public Team get() {
        return coreData.getTeam() == 0 ? null : coreData.getTeam() == Side.BLUE.getId() ? match.get().getBlueTeam() : match.get().getRedTeam();
      }
    });

    private final Supplier<MatchParticipant> victim = Suppliers.memoize(new Supplier<>() {
      @Override
      public MatchParticipant get() {
        if (coreData.getVictimId() == 0) {
          return null;
        }
        match.get().getVersion(); // Force match load so participants have IDs
        return match.get().getParticipants().stream().filter(matchParticipant -> matchParticipant.getCoreData().getParticipantId() == coreData.getVictimId()).findFirst().orElse(null);
      }
    });

    public Event(final de.zahrie.trues.api.riot.xayah.types.data.match.Event coreData) {
      super(coreData);
    }

    @Override
    public Item getAfter() {
      return after.get();
    }

    @Override
    public AscensionType getAscensionType() {
      return AscensionType.valueOf(coreData.getAscensionType());
    }

    @Override
    public SearchableList<MatchParticipant> getAssistingParticipants() {
      return assistingParticipants.get();
    }

    @Override
    public Item getBefore() {
      return before.get();
    }

    @Override
    public BuildingType getBuildingType() {
      return BuildingType.valueOf(coreData.getBuildingType());
    }

    @Override
    public Point getCapturedPoint() {
      return Point.valueOf(coreData.getCapturedPoint());
    }

    @Override
    public MatchParticipant getCreator() {
      return creator.get();
    }

    @Override
    public Item getItem() {
      return item.get();
    }

    @Override
    public MatchParticipant getKiller() {
      return killer.get();
    }

    @Override
    public LaneType getLaneType() {
      return LaneType.valueOf(coreData.getLaneType());
    }

    @Override
    public LevelUpType getLevelUpType() {
      return LevelUpType.valueOf(coreData.getLevelUpType());
    }

    @Override
    public MonsterSubType getMonsterSubType() {
      return MonsterSubType.valueOf(coreData.getMonsterSubType());
    }

    @Override
    public MonsterType getMonsterType() {
      return MonsterType.valueOf(coreData.getMonsterType());
    }

    @Override
    public MatchParticipant getParticipant() {
      return participant.get();
    }

    @Override
    public Position getPosition() {
      return position.get();
    }

    @Override
    public Side getSide() {
      return Side.withId(coreData.getTeam());
    }

    @Override
    public Skill getSkill() {
      return Skill.withId(coreData.getSkill());
    }

    @Override
    public Team getTeam() {
      return team.get();
    }

    @Override
    public Duration getTimestamp() {
      return coreData.getTimestamp();
    }

    @Override
    public TurretType getTurretType() {
      return TurretType.valueOf(coreData.getTurretType());
    }

    @Override
    public EventType getType() {
      return EventType.valueOf(coreData.getType());
    }

    @Override
    public MatchParticipant getVictim() {
      return victim.get();
    }

    @Override
    public WardType getWardType() {
      return WardType.valueOf(coreData.getWardType());
    }
  }

  public class Frame extends de.zahrie.trues.api.riot.xayah.types.core.match.Frame {
    @Serial
    private static final long serialVersionUID = -7952210236371512933L;

    private final Supplier<Map<MatchParticipant, ParticipantFrame>> participantFrames = Suppliers.memoize(new Supplier<>() {
      @Override
      public Map<MatchParticipant, ParticipantFrame> get() {
        if (coreData.getParticipantFrames() == null) {
          return null;
        }
        if (coreData.getParticipantFrames().isEmpty()) {
          return Collections.emptyMap();
        }
        match.get().getVersion(); // Force match load so participants have IDs
        return match.get().getParticipants().stream().filter(matchParticipant -> coreData.getParticipantFrames().containsKey(matchParticipant.getCoreData().getParticipantId())).collect(Collectors.toUnmodifiableMap(matchParticipant -> matchParticipant, matchParticipant -> new ParticipantFrame(coreData.getParticipantFrames().get(matchParticipant.getCoreData().getParticipantId())), (a, b) -> b));
      }
    });

    public Frame(final de.zahrie.trues.api.riot.xayah.types.data.match.Frame coreData) {
      super(coreData, Event::new);
    }

    @Override
    public Map<MatchParticipant, ParticipantFrame> getParticipantFrames() {
      return participantFrames.get();
    }

    @Override
    public Duration getTimestamp() {
      return coreData.getTimestamp();
    }
  }

  @Serial
  private static final long serialVersionUID = -3153365065219231927L;

  public static Builder withId(final long id) {
    return new Builder(id);
  }

  private final java.util.function.Supplier<Match> match = Suppliers.memoize(() ->
      coreData.getId() == 0L ? null : Match.withId(coreData.getId()).withPlatform(Platform.withTag(coreData.getPlatform())).get())::get;

  public Timeline(final de.zahrie.trues.api.riot.xayah.types.data.match.Timeline coreData) {
    super(coreData, 1);
  }

  @Override
  public boolean exists() {
    if (coreData.isEmpty()) {
      load(LIST_PROXY_LOAD_GROUP);
    }
    return !coreData.isEmpty();
  }

  public Duration getInterval() {
    if (coreData.getInterval() == null) {
      load(LIST_PROXY_LOAD_GROUP);
    }
    return coreData.getInterval();
  }

  @Override
  protected List<String> getLoadGroups() {
    return List.of(LIST_PROXY_LOAD_GROUP);
  }

  public Match getMatch() {
    return match.get();
  }

  public Platform getPlatform() {
    return Platform.withTag(coreData.getPlatform());
  }

  public Region getRegion() {
    return Platform.withTag(coreData.getPlatform()).getRegion();
  }

  @Override
  protected void loadCoreData(final String group) {
    final ImmutableMap.Builder<String, Object> builder;
    if (group.equals(LIST_PROXY_LOAD_GROUP)) {
      builder = ImmutableMap.builder();
      if (coreData.getPlatform() != null) {
        builder.put("platform", Platform.withTag(coreData.getPlatform()));
      }
      if (coreData.getId() != 0L) {
        builder.put("matchId", coreData.getId());
      }
      final de.zahrie.trues.api.riot.xayah.types.data.match.Timeline data =
          Orianna.getSettings().getPipeline().get(de.zahrie.trues.api.riot.xayah.types.data.match.Timeline.class, builder.build());
      if (data != null) {
        coreData = data;
      }
      loadListProxyData(Frame::new);
    }
  }
}
