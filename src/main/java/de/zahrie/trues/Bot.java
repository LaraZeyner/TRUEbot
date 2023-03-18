package de.zahrie.trues;

import java.util.Date;

import de.zahrie.trues.discord.Nunu;
import lombok.extern.java.Log;

@Log
public class Bot {

  public static void main(String[] args) {
    log.info("Starte Bot");
    final var start = new Date();
    final var end = LoadupManager.init();
    Nunu.run();

    log.info(end.getTime() - start.getTime() + " Millis");
  }
}
