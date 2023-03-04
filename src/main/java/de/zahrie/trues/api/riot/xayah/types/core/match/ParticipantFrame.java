package de.zahrie.trues.api.riot.xayah.types.core.match;

import java.io.Serial;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class ParticipantFrame extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.match.ParticipantFrame> {
    @Serial
    private static final long serialVersionUID = -8233781937537156206L;

    private final Supplier<Position> position = Suppliers.memoize(() -> {
        if(coreData.getPosition() == null) {
            return null;
        }
        return new Position(coreData.getPosition());
    });

    public ParticipantFrame(final de.zahrie.trues.api.riot.xayah.types.data.match.ParticipantFrame coreData) {
        super(coreData);
    }

    public int getCreepScore() {
        return coreData.getCreepScore();
    }

    public int getDominionScore() {
        return coreData.getDominionScore();
    }

    public int getExperience() {
        return coreData.getExperience();
    }

    public int getGold() {
        return coreData.getGold();
    }

    public int getGoldEarned() {
        return coreData.getGoldEarned();
    }

    public int getLevel() {
        return coreData.getLevel();
    }

    public int getNeutralMinionsKilled() {
        return coreData.getNeutralMinionsKilled();
    }

    public Position getPosition() {
        return position.get();
    }

    public int getTeamScore() {
        return coreData.getTeamScore();
    }
}
