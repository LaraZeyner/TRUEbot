package de.zahrie.trues.api.riot.xayah.types.core.match;

import org.joda.time.Duration;

import de.zahrie.trues.api.riot.xayah.types.common.AscensionType;
import de.zahrie.trues.api.riot.xayah.types.common.BuildingType;
import de.zahrie.trues.api.riot.xayah.types.common.EventType;
import de.zahrie.trues.api.riot.xayah.types.common.LaneType;
import de.zahrie.trues.api.riot.xayah.types.common.LevelUpType;
import de.zahrie.trues.api.riot.xayah.types.common.MonsterSubType;
import de.zahrie.trues.api.riot.xayah.types.common.MonsterType;
import de.zahrie.trues.api.riot.xayah.types.common.Point;
import de.zahrie.trues.api.riot.xayah.types.common.Side;
import de.zahrie.trues.api.riot.xayah.types.common.Skill;
import de.zahrie.trues.api.riot.xayah.types.common.TurretType;
import de.zahrie.trues.api.riot.xayah.types.common.WardType;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableList;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.Item;

public abstract class Event extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.match.Event> {
    private static final long serialVersionUID = -2437642335207262614L;

    public Event(final de.zahrie.trues.api.riot.xayah.types.data.match.Event coreData) {
        super(coreData);
    }

    public abstract Item getAfter();

    public abstract AscensionType getAscensionType();

    public abstract SearchableList<Participant> getAssistingParticipants();

    public abstract Item getBefore();

    public abstract BuildingType getBuildingType();

    public abstract Point getCapturedPoint();

    public abstract Participant getCreator();

    public abstract Item getItem();

    public abstract Participant getKiller();

    public abstract LaneType getLaneType();

    public abstract LevelUpType getLevelUpType();

    public abstract MonsterSubType getMonsterSubType();

    public abstract MonsterType getMonsterType();

    public abstract Participant getParticipant();

    public abstract Position getPosition();

    public abstract Side getSide();

    public abstract Skill getSkill();

    public abstract Team getTeam();

    public abstract Duration getTimestamp();

    public abstract TurretType getTurretType();

    public abstract EventType getType();

    public abstract Participant getVictim();

    public abstract WardType getWardType();
}
