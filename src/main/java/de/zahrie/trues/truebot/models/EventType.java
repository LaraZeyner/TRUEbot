package de.zahrie.trues.truebot.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public enum EventType {
  application("Bewerbung"),
  available("anwesend"),
  blocked("abwesend"),
  calibration("Kalibrierung"),
  clash("Clash"),
  intern("Internes"),
  meeting("Besprechung"),
  prime_league("PRM-Game"),
  scrimmage("Scrim"),
  training("Training");

  private final String str;

}
