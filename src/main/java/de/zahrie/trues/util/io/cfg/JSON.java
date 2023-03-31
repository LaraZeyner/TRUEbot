package de.zahrie.trues.util.io.cfg;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.json.JSONObject;

public final class JSON extends JSONObject {

  public static JSON fromFile(String fileName) {
    try {
      final var path = Path.of(ClassLoader.getSystemResource(fileName).toURI());
      final String content = Files.readString(path);
      return new JSON(content);
    } catch (IOException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public static void write(String fileName, String content) {
    try {
      final var path = Path.of(ClassLoader.getSystemResource(fileName).toURI());
      Files.writeString(path, content, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    } catch (IOException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private JSON(String source) {
    super(source);
  }
}
