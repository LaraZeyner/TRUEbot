package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import java.awt.image.BufferedImage;
import java.io.Serial;

import com.google.common.collect.ImmutableMap;
import de.zahrie.trues.api.riot.xayah.Orianna;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.Searchable;

public class Skin extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.staticdata.Skin> {
    @Serial
    private static final long serialVersionUID = 3024386482761562130L;

    public Skin(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.Skin coreData) {
        super(coreData);
    }

    @Searchable(int.class)
    public int getId() {
        return coreData.getId();
    }

    public BufferedImage getLoadingImage() {
      return Orianna.getSettings().getPipeline().get(BufferedImage.class, ImmutableMap.of("url", getLoadingImageURL()));
    }

    public String getLoadingImageURL() {
        return "http://ddragon.leagueoflegends.com/cdn/img/champion/loading/" + coreData.getChampionKey() + "_" + coreData.getNumber() + ".jpg";
    }

    @Searchable(String.class)
    public String getName() {
        return coreData.getName();
    }

    public int getNumber() {
        return coreData.getNumber();
    }

    public BufferedImage getSpashImage() {
      return Orianna.getSettings().getPipeline().get(BufferedImage.class, ImmutableMap.of("url", getSplashImageURL()));
    }

    public String getSplashImageURL() {
        return "http://ddragon.leagueoflegends.com/cdn/img/champion/splash/" + coreData.getChampionKey() + "_" + coreData.getNumber() + ".jpg";
    }
}
