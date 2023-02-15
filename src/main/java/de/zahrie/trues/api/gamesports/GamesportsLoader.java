package de.zahrie.trues.api.gamesports;

import de.zahrie.trues.util.io.request.HTML;
import de.zahrie.trues.util.io.request.Request;
import de.zahrie.trues.util.io.request.URLType;

/**
 * Created by Lara on 15.02.2023 for TRUEbot
 */
public class GamesportsLoader {
  protected final int id;
  protected final HTML html;

  public GamesportsLoader(URLType urlType, int id) {
    this.id = id;
    this.html = Request.requestHTML(urlType, id);
  }
}
