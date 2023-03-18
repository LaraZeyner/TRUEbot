package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import java.awt.image.BufferedImage;
import java.io.Serial;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import de.zahrie.trues.api.riot.xayah.Orianna;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class Image extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.staticdata.Image> {
  @Serial
  private static final long serialVersionUID = -6752564052626945287L;

  private final Supplier<BufferedImage> image = Suppliers.memoize(() ->
      coreData.getVersion() == null || coreData.getGroup() == null || coreData.getFull() == null ? null : Orianna.getSettings().getPipeline().get(BufferedImage.class, ImmutableMap.of("url", getURL())))::get;

  private final Supplier<Sprite> sprite = Suppliers.memoize(() ->
      coreData.getSprite() == null ? null : new Sprite(coreData.getSprite()))::get;

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
