package de.zahrie.trues.api.riot.xayah.types.core.match;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class ParticipantTimeline extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.match.ParticipantTimeline> {
    private static final long serialVersionUID = -9013789487241071327L;

    private final Supplier<StatTotals> creepScore = Suppliers.memoize(() -> {
        if(coreData.getCreepScore() == null) {
            return null;
        }
        return new StatTotals(coreData.getCreepScore());
    });

    private final Supplier<StatTotals> creepScoreDifference = Suppliers.memoize(() -> {
        if(coreData.getCreepScoreDifference() == null) {
            return null;
        }
        return new StatTotals(coreData.getCreepScoreDifference());
    });

    private final Supplier<StatTotals> damageTaken = Suppliers.memoize(() -> {
        if(coreData.getDamageTaken() == null) {
            return null;
        }
        return new StatTotals(coreData.getDamageTaken());
    });

    private final Supplier<StatTotals> damageTakenDifference = Suppliers.memoize(() -> {
        if(coreData.getDamageTakenDifference() == null) {
            return null;
        }
        return new StatTotals(coreData.getDamageTakenDifference());
    });

    private final Supplier<StatTotals> experience = Suppliers.memoize(() -> {
        if(coreData.getExperience() == null) {
            return null;
        }
        return new StatTotals(coreData.getExperience());
    });

    private final Supplier<StatTotals> experienceDifference = Suppliers.memoize(() -> {
        if(coreData.getExperienceDifference() == null) {
            return null;
        }
        return new StatTotals(coreData.getExperienceDifference());
    });

    private final Supplier<StatTotals> gold = Suppliers.memoize(() -> {
        if(coreData.getGold() == null) {
            return null;
        }
        return new StatTotals(coreData.getGold());
    });

    public ParticipantTimeline(final de.zahrie.trues.api.riot.xayah.types.data.match.ParticipantTimeline coreData) {
        super(coreData);
    }

    public StatTotals getCreepScore() {
        return creepScore.get();
    }

    public StatTotals getCreepScoreDifference() {
        return creepScoreDifference.get();
    }

    public StatTotals getDamageTaken() {
        return damageTaken.get();
    }

    public StatTotals getDamageTakenDifference() {
        return damageTakenDifference.get();
    }

    public StatTotals getExperience() {
        return experience.get();
    }

    public StatTotals getExperienceDifference() {
        return experienceDifference.get();
    }

    public StatTotals getGold() {
        return gold.get();
    }
}
