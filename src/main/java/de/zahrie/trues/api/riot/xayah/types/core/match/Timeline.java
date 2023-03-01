package de.zahrie.trues.api.riot.xayah.types.core.match;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Duration;

import com.google.common.base.Function;
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

public class Timeline extends GhostObject.ListProxy<Frame, de.zahrie.trues.api.riot.xayah.types.data.match.Frame, de.zahrie.trues.api.riot.xayah.types.data.match.Timeline> {
    public static class Builder {
        private final long id;
        private Platform platform;

        private Builder(final long id) {
            this.id = id;
        }

        public Timeline get() {
            if(platform == null) {
                platform = Orianna.getSettings().getDefaultPlatform();
                if(platform == null) {
                    throw new IllegalStateException(
                        "No platform/region was set! Must either set a default platform/region with Orianna.setDefaultPlatform or Orianna.setDefaultRegion, or include a platform/region with the request!");
                }
            }

            final ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object> builder().put("platform", platform).put("matchId", id);

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
        private static final long serialVersionUID = -245827734492071363L;

        private final Supplier<Item> after = Suppliers.memoize(() -> {
            if(coreData.getAfterId() == 0) {
                return null;
            }
            return Item.withId(coreData.getAfterId()).get();
        });

        private final Supplier<SearchableList<Participant>> assistingParticipants = Suppliers.memoize(new Supplier<SearchableList<Participant>>() {
            @Override
            public SearchableList<Participant> get() {
                if(coreData.getAssistingParticipants() == null) {
                    return null;
                }
                if(coreData.getAssistingParticipants().isEmpty()) {
                    return SearchableLists.empty();
                }
                match.get().getVersion(); // Force match load so participants have IDs
                final List<Participant> assistingParticipants = new ArrayList<>(coreData.getAssistingParticipants().size());
                for(final Participant participant : match.get().getParticipants()) {
                    if(coreData.getAssistingParticipants().contains(participant.getCoreData().getParticipantId())) {
                        assistingParticipants.add(participant);
                    }
                }
                return SearchableLists.unmodifiableFrom(assistingParticipants);
            }
        });

        private final Supplier<Item> before = Suppliers.memoize(() -> {
            if(coreData.getBeforeId() == 0) {
                return null;
            }
            return Item.withId(coreData.getBeforeId()).get();
        });

        private final Supplier<Participant> creator = Suppliers.memoize(new Supplier<Participant>() {
            @Override
            public Participant get() {
                if(coreData.getCreatorId() == 0) {
                    return null;
                }
                match.get().getVersion(); // Force match load so participants have IDs
                for(final Participant participant : match.get().getParticipants()) {
                    if(participant.getCoreData().getParticipantId() == coreData.getCreatorId()) {
                        return participant;
                    }
                }
                return null;
            }
        });

        private final Supplier<Item> item = Suppliers.memoize(() -> {
            if(coreData.getItemId() == 0) {
                return null;
            }
            return Item.withId(coreData.getItemId()).get();
        });

        private final Supplier<Participant> killer = Suppliers.memoize(new Supplier<Participant>() {
            @Override
            public Participant get() {
                if(coreData.getKillerId() == 0) {
                    return null;
                }
                match.get().getVersion(); // Force match load so participants have IDs
                for(final Participant participant : match.get().getParticipants()) {
                    if(participant.getCoreData().getParticipantId() == coreData.getKillerId()) {
                        return participant;
                    }
                }
                return null;
            }
        });

        private final Supplier<Participant> participant = Suppliers.memoize(new Supplier<Participant>() {
            @Override
            public Participant get() {
                if(coreData.getParticipantId() == 0) {
                    return null;
                }
                match.get().getVersion(); // Force match load so participants have IDs
                for(final Participant participant : match.get().getParticipants()) {
                    if(participant.getCoreData().getParticipantId() == coreData.getParticipantId()) {
                        return participant;
                    }
                }
                return null;
            }
        });

        private final Supplier<Position> position = Suppliers.memoize(() -> {
            if(coreData.getPosition() == null) {
                return null;
            }
            return new Position(coreData.getPosition());
        });

        private final Supplier<Team> team = Suppliers.memoize(new Supplier<Team>() {
            @Override
            public Team get() {
                if(coreData.getTeam() == 0) {
                    return null;
                }
                return coreData.getTeam() == Side.BLUE.getId() ? match.get().getBlueTeam() : match.get().getRedTeam();
            }
        });

        private final Supplier<Participant> victim = Suppliers.memoize(new Supplier<Participant>() {
            @Override
            public Participant get() {
                if(coreData.getVictimId() == 0) {
                    return null;
                }
                match.get().getVersion(); // Force match load so participants have IDs
                for(final Participant participant : match.get().getParticipants()) {
                    if(participant.getCoreData().getParticipantId() == coreData.getVictimId()) {
                        return participant;
                    }
                }
                return null;
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
        public SearchableList<Participant> getAssistingParticipants() {
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
        public Participant getCreator() {
            return creator.get();
        }

        @Override
        public Item getItem() {
            return item.get();
        }

        @Override
        public Participant getKiller() {
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
        public Participant getParticipant() {
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
        public Participant getVictim() {
            return victim.get();
        }

        @Override
        public WardType getWardType() {
            return WardType.valueOf(coreData.getWardType());
        }
    }

    public class Frame extends de.zahrie.trues.api.riot.xayah.types.core.match.Frame {
        private static final long serialVersionUID = -7952210236371512933L;

        private final Supplier<Map<Participant, ParticipantFrame>> participantFrames = Suppliers.memoize(new Supplier<Map<Participant, ParticipantFrame>>() {
            @Override
            public Map<Participant, ParticipantFrame> get() {
                if(coreData.getParticipantFrames() == null) {
                    return null;
                }
                if(coreData.getParticipantFrames().isEmpty()) {
                    return Collections.emptyMap();
                }
                match.get().getVersion(); // Force match load so participants have IDs
                final Map<Participant, ParticipantFrame> participantFrames = new HashMap<>(coreData.getParticipantFrames().size());
                for(final Participant participant : match.get().getParticipants()) {
                    if(coreData.getParticipantFrames().containsKey(participant.getCoreData().getParticipantId())) {
                        participantFrames.put(participant,
                            new ParticipantFrame(coreData.getParticipantFrames().get(participant.getCoreData().getParticipantId())));
                    }
                }
                return Collections.unmodifiableMap(participantFrames);
            }
        });

        public Frame(final de.zahrie.trues.api.riot.xayah.types.data.match.Frame coreData) {
            super(coreData, data -> new Event(data));
        }

        @Override
        public Map<Participant, ParticipantFrame> getParticipantFrames() {
            return participantFrames.get();
        }

        @Override
        public Duration getTimestamp() {
            return coreData.getTimestamp();
        }
    }

    private static final long serialVersionUID = -3153365065219231927L;

    public static Builder withId(final long id) {
        return new Builder(id);
    }

    private final Supplier<Match> match = Suppliers.memoize(() -> {
        if(coreData.getId() == 0L) {
            return null;
        }
        return Match.withId(coreData.getId()).withPlatform(Platform.withTag(coreData.getPlatform())).get();
    });

    public Timeline(final de.zahrie.trues.api.riot.xayah.types.data.match.Timeline coreData) {
        super(coreData, 1);
    }

    @Override
    public boolean exists() {
        if(coreData.isEmpty()) {
            load(LIST_PROXY_LOAD_GROUP);
        }
        return !coreData.isEmpty();
    }

    public Duration getInterval() {
        if(coreData.getInterval() == null) {
            load(LIST_PROXY_LOAD_GROUP);
        }
        return coreData.getInterval();
    }

    @Override
    protected List<String> getLoadGroups() {
        return Arrays.asList(new String[] {
            LIST_PROXY_LOAD_GROUP
        });
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
        ImmutableMap.Builder<String, Object> builder;
        switch(group) {
            case LIST_PROXY_LOAD_GROUP:
                builder = ImmutableMap.builder();
                if(coreData.getPlatform() != null) {
                    builder.put("platform", Platform.withTag(coreData.getPlatform()));
                }
                if(coreData.getId() != 0L) {
                    builder.put("matchId", coreData.getId());
                }
                final de.zahrie.trues.api.riot.xayah.types.data.match.Timeline data =
                    Orianna.getSettings().getPipeline().get(de.zahrie.trues.api.riot.xayah.types.data.match.Timeline.class, builder.build());
                if(data != null) {
                    coreData = data;
                }
                loadListProxyData(data1 -> new Frame(data1));
                break;
            default:
                break;
        }
    }
}
