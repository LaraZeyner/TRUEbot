package de.zahrie.trues.api.coverage;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.util.io.request.HTML;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Created by Lara on 16.02.2023 for TRUEbot
 */
@AllArgsConstructor
@NoArgsConstructor
public class ModelBase implements Serializable {
  @Serial
  private static final long serialVersionUID = 6069591807175482827L;

  protected HTML html;

}
