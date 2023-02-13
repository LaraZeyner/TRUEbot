package de.zahrie.trues.truebot.models.voting;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
@Table(name = "voting")
public class Voting implements Serializable {
  @Serial
  private static final long serialVersionUID = -8019371124130507694L;


  @Id
  @Column(name = "voting_name", nullable = false, length = 25)
  private String name;

  @Column(name = "voting_question", nullable = false, length = 1000)
  private String question;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "deadline")
  private Calendar deadline;

  @Column(name = "anonym", nullable = false)
  private boolean isAnonymous = false;

  @Column(name = "voting_option_name", nullable = false, length = 100)
  private String votingOptionName;

  @Column(name = "voting_option_description", nullable = false, length = 1000)
  private String votingOptionDescription;

}