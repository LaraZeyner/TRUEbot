package de.zahrie.trues.util.io.request;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import de.zahrie.trues.util.logger.Logger;

/**
 * Created by Lara on 14.02.2023 for TRUEbot
 */
public record HTMLRequester(URL url) {

  public HTML html() {
    String content = determineContent();
    if (content.contains("webmaster has already been notified")) {
      content = determineContent();
    }
    return new HTML(content);
  }

  private String determineContent() {
    try (final Scanner scanner = new Scanner(this.url.openStream(), StandardCharsets.UTF_8).useDelimiter("\\A")) {
      return scanner.hasNext() ? scanner.next() : "";
    } catch (IOException e) {
      Logger.getLogger("HTML").severe("No URL requested", e);
    }
    return "";
  }

}
