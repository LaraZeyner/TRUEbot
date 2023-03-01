package de.zahrie.trues.api.coverage.stage.model;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.coverage.stage.StageType;
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
@DiscriminatorValue("Auslosung Gruppen")
public class CreationStage extends WaitingStage implements Serializable {
  @Serial
  private static final long serialVersionUID = -1317304037038810292L;

  @Override
  public StageType type() {
    return StageType.Auslosung_Gruppen;
  }
}
