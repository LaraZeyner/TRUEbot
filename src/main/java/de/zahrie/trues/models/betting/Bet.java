package de.zahrie.trues.models.betting;

import java.io.Serial;
import java.io.Serializable;

import de.zahrie.trues.api.coverage.match.model.Match;
import de.zahrie.trues.api.discord.member.DiscordMember;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "Bet")
@Table(name = "bet", indexes = {
    @Index(name = "idx_bet", columnList = "coverage, discord_user", unique = true)})
public class Bet implements Serializable {

  @Serial
  private static final long serialVersionUID = 1002640658346268833L;


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bet_id", nullable = false)
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "coverage", nullable = false)
  @ToString.Exclude
  private Match coverage;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "discord_user", nullable = false)
  private DiscordMember member;

  @Column(name = "bet_outcome", nullable = false, length = 300)
  private String outcome;

  @Column(name = "bet_amount", nullable = false)
  private int amount;

}