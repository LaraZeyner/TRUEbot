package de.zahrie.trues;

import lombok.extern.java.Log;

@Log
public class Bot {
  public static void main(String[] args) {
    log.info("Starte Bot ...");

    LoadupManager.getInstance().connect();
  }
}
