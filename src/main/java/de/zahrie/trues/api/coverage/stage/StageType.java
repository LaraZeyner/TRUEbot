package de.zahrie.trues.api.coverage.stage;

import de.zahrie.trues.api.coverage.SchedulingMode;
import de.zahrie.trues.api.coverage.season.CoverageDepartment;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public enum StageType {
  Anmeldung,
  Kalibrierungsphase,
  Auslosung_Gruppen,
  Gruppenphase,
  Playoffs;

  public SchedulingMode getSchedulingMode(CoverageDepartment department) {
    return department.equals(CoverageDepartment.Orga_Cup) || this.equals(Gruppenphase) ? SchedulingMode.REGULATED : SchedulingMode.DISABLED;
  }

}
