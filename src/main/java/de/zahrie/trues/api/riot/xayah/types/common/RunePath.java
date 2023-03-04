package de.zahrie.trues.api.riot.xayah.types.common;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum RunePath {
        DOMINATION(8100),
        INSPIRATION(8400),
        PRECISION(8000),
        RESOLVE(8300),
        SORCERY(8200);

    private static final Map<Integer, RunePath> BY_ID = getById();

    private static Map<Integer, RunePath> getById() {
        final Builder<Integer, RunePath> builder = ImmutableMap.builder();
        for(final RunePath path : values()) {
            builder.put(path.id, path);
        }
        return builder.build();
    }

    public static RunePath withId(final int id) {
        return BY_ID.get(id);
    }

    private final int id;

    RunePath(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
