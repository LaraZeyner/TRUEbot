package de.zahrie.trues.api.riot.xayah.types.common;

import java.util.Comparator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum Division {
        I(4),
        II(3),
        III(2),
        IV(1),
        V(0);

    public static Comparator<Division> getComparator() {
        return Comparator.comparingInt(o -> o.level);
    }

    private final int level;

    public int compare(final Division o) {
        return Integer.compare(level, o.level);
    }

}
