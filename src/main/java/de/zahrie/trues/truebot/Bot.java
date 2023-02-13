package de.zahrie.trues.truebot;

import java.util.Date;

import de.zahrie.trues.truebot.dc.Nunu;
import de.zahrie.trues.truebot.handler.LoadupManager;
import de.zahrie.trues.truebot.util.logger.Logger;

public class Bot {

  public static void main(String[] args) {
    Logger.getLogger().info("Starte Bot");
    final var start = new Date();
    final var end = LoadupManager.init();
    Nunu.run();

    System.out.println(end.getTime() - start.getTime() + " Millis");
  }
}
