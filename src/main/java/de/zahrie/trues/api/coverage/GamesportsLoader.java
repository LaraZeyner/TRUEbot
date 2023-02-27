package de.zahrie.trues.api.coverage;

import de.zahrie.trues.util.io.request.HTML;
import de.zahrie.trues.util.io.request.Request;
import de.zahrie.trues.util.io.request.URLType;

/**
 * Created by Lara on 15.02.2023 for TRUEbot
 */
public class GamesportsLoader {
  protected final int id;
  protected final HTML html;
  protected final String url;

  public GamesportsLoader(URLType urlType, Integer... ids) {
    this.id = ids[ids.length - 1];
    this.html = Request.requestHTML(urlType, ids);
    this.url = String.format(urlType.getUrlName(), ids);
  }
}
