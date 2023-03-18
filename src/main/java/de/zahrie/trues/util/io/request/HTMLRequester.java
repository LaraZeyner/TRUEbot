package de.zahrie.trues.util.io.request;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import de.zahrie.trues.api.datatypes.symbol.Chain;
import lombok.extern.java.Log;

@Log
public record HTMLRequester(URL url) {

  public HTML html() {
    String content = determineContent();
    if (content.contains("webmaster has already been notified")) {
      content = determineContent();
    }
    return new HTML(Chain.of(content));
  }

  private String determineContent() {
    try (final Scanner scanner = new Scanner(this.url.openStream(), StandardCharsets.UTF_8).useDelimiter("\\A")) {
      return scanner.hasNext() ? scanner.next() : "";
    } catch (IOException e) {
      log.severe("No URL requested");
      log.throwing(getClass().getName(), "determineContent", e);
    }
    return "";
  }

}
