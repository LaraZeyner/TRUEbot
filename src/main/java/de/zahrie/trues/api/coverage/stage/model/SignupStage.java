package de.zahrie.trues.api.coverage.stage.model;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.coverage.stage.StageType;
import de.zahrie.trues.api.coverage.stage.Stageable;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@DiscriminatorValue("Anmeldung")
public class SignupStage extends WaitingStage implements Serializable {
  @Serial
  private static final long serialVersionUID = -1317304037038810292L;

  @Override
  public StageType type() {
    return StageType.Anmeldung;
  }

}
