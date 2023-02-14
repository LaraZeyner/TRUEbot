package de.zahrie.trues.models.voting;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

@Data
public class VotingEntryId implements Serializable {
  @Serial
  private static final long serialVersionUID = 1921259619270617209L;

  private String voting;

  private String votingOption;

  private long discordId;

}