package de.zahrie.trues.util.io.request;

import java.net.MalformedURLException;
import java.net.URL;

import de.zahrie.trues.util.logger.Logger;

/**
 * Created by Lara on 29.03.2022 for TRUES
 */
public final class Request {
  public static HTML requestHTML(URLType urlType, Object... arguments) {
    String urlString = urlType.getUrlName();
    urlString = String.format(urlString, arguments);
    return requestHTML(urlString);
  }

  public static HTML requestHTML(String urlString) {
    try {
      if (urlString.startsWith("http")) {
        final URL url = new URL(urlString);
        return new HTMLRequester(url).html();
      }
      Logger.getLogger("HTML").severe("No URL requested");
    } catch (MalformedURLException urlException) {
      Logger.getLogger("HTML").severe("Wrong url", urlException);
    }
    return new HTML();
  }

}
