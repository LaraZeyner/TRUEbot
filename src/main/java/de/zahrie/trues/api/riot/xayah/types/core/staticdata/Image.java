package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import java.awt.image.BufferedImage;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import de.zahrie.trues.api.riot.xayah.Orianna;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class Image extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.staticdata.Image> {
    private static final long serialVersionUID = -6752564052626945287L;

    private final Supplier<BufferedImage> image = Suppliers.memoize(() -> {
        if(coreData.getVersion() == null || coreData.getGroup() == null || coreData.getFull() == null) {
            return null;
        }
        return Orianna.getSettings().getPipeline().get(BufferedImage.class, ImmutableMap.<String, Object> of("url", getURL()));
    });

    private final Supplier<Sprite> sprite = Suppliers.memoize(() -> {
        if(coreData.getSprite() == null) {
            return null;
        }
        return new Sprite(coreData.getSprite());
    });

    public Image(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.Image coreData) {
        super(coreData);
    }

    public BufferedImage get() {
        return image.get();
    }

    public String getFull() {
        return coreData.getFull();
    }

    public String getGroup() {
        return coreData.getGroup();
    }

    public Sprite getSprite() {
        return sprite.get();
    }

    public String getURL() {
        return "http://ddragon.leagueoflegends.com/cdn/" + coreData.getVersion() + "/img/" + coreData.getGroup() + "/" + coreData.getFull();
    }
}
