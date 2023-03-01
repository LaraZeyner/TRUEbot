package de.zahrie.trues.api.riot.xayah.types.common;

import java.util.Comparator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum Tier {
        BRONZE(2),
        CHALLENGER(9),
        DIAMOND(6),
        GOLD(4),
        GRANDMASTER(8),
        IRON(1),
        MASTER(7),
        PLATINUM(5),
        SILVER(3),
        UNRANKED(0);

    public static Comparator<Tier> getComparator() {
        return Comparator.comparingInt(o -> o.level);
    }

    private final int level;

    public int compare(final Tier o) {
        return Integer.compare(level, o.level);
    }

}