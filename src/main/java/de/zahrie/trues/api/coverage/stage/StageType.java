package de.zahrie.trues.api.coverage.stage;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public enum StageType {
  SIGNUP,
  CALIBRATION,
  CREATION,
  GROUPS,
  PLAYOFFS

}
