package de.zahrie.trues.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public enum EventType {
  APPLICATION("Bewerbung"),
  AVAILABLE("anwesend"),
  BLOCKED("abwesend"),
  CALIBRATION("Kalibrierung"),
  CLASH("Clash"),
  INTERN("Internes"),
  MEETING("Besprechung"),
  PRIME_LEAGUE("PRM-Game"),
  SCRIMMAGE("Scrim"),
  TRAINING("Training");

  private final String str;

}
