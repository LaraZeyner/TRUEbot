package de.zahrie.trues.util.io.request;

import java.net.MalformedURLException;
import java.net.URL;

import lombok.extern.java.Log;

@Log
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
      log.severe("No URL requested");
    } catch (MalformedURLException urlException) {
      log.severe("Wrong url");
      log.throwing("Request", "requestHTML(String): HTML", urlException);
    }
    return new HTML();
  }

}
