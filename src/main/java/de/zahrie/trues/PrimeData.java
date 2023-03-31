package de.zahrie.trues;

import de.zahrie.trues.util.Connectable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Getter
@Setter
@Log
public class PrimeData implements Connectable {
  private static PrimeData primeData;

  static {
    primeData = new PrimeData();
    primeData.connect();
  }

  public static PrimeData getInstance() {
    return primeData;
  }

  @Override
  public void connect() {
  }


  @Override
  public void disconnect() {

  }
}
