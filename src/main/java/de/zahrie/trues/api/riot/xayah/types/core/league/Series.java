package de.zahrie.trues.api.riot.xayah.types.core.league;

import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class Series extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.league.Series> {
    private static final long serialVersionUID = -883134054114085873L;

    public Series(final de.zahrie.trues.api.riot.xayah.types.data.league.Series coreData) {
        super(coreData);
    }

    public int getLosses() {
        return coreData.getLosses();
    }

    public int getNotPlayed() {
        return coreData.getWinsRequired() * 2 - 1 - coreData.getWins() - coreData.getLosses();
    }

    public String getProgess() {
        return coreData.getProgress();
    }

    public int getWins() {
        return coreData.getWins();
    }

    public int getWinsRequired() {
        return coreData.getWinsRequired();
    }
}
