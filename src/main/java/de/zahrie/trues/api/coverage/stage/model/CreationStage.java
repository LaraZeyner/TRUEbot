package de.zahrie.trues.api.coverage.stage.model;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@DiscriminatorValue("Auslosung")
public class CreationStage extends WaitingStage implements Serializable {
  @Serial
  private static final long serialVersionUID = -1317304037038810292L;

}
