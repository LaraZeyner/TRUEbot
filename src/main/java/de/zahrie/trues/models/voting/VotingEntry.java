package de.zahrie.trues.models.voting;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
@Table(name = "voting_entry", indexes = {
        @Index(name = "idx_voting_entry", columnList = "voting, voting_option, discord_id",
                unique = true) })
@IdClass(VotingEntryId.class)
public class VotingEntry implements Serializable {
  @Serial
  private static final long serialVersionUID = -4217008587779272171L;

  @Id
  @Column(name = "voting", nullable = false, length = 25)
  private String voting;

  @Id
  @Column(name = "voting_option", nullable = false, length = 100)
  private String votingOption;

  @Id
  @Column(name = "discord_id", nullable = false)
  private long discordId;

  @Column(name = "voting_value", nullable = false)
  private byte votingValue;

}