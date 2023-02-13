package de.zahrie.trues.truebot.util.io.request;

import java.io.IOException;

import de.zahrie.trues.truebot.util.logger.Logger;
import lombok.NoArgsConstructor;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
@NoArgsConstructor
public final class RequestManager {
  public static HTML requestHTML(String urlString) throws IOException {
    if (urlString.startsWith("http")) {
      final HTML html = new HTML(urlString);
      if (html.toString().contains("webmaster has already been notified")) {
        return new HTML(urlString);
      }
      return html;
    }
    Logger.getLogger("HTML").severe("No URL requested");
    return null;
  }

}
