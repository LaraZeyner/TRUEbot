package de.zahrie.trues;

import java.util.Date;

import de.zahrie.trues.dc.Nunu;
import de.zahrie.trues.handler.LoadupManager;
import de.zahrie.trues.util.logger.Logger;

public class Bot {

  public static void main(String[] args) {
    Logger.getLogger().info("Starte Bot");
    final var start = new Date();
    final var end = LoadupManager.init();
    Nunu.run();

    System.out.println(end.getTime() - start.getTime() + " Millis");
  }
}
