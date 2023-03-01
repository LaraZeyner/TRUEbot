package de.zahrie.trues.util.io.cfg;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONObject;

/**
 * Created by Lara on 09.02.2023 for TRUEbot
 */
public final class JSON extends JSONObject {

  public static JSON fromFile(String fileName) {
    try {
      var path = Path.of(ClassLoader.getSystemResource(fileName).toURI());
      final String content = Files.readString(path);
      return new JSON(content);
    } catch (IOException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private JSON(String source) {
    super(source);
  }
}
