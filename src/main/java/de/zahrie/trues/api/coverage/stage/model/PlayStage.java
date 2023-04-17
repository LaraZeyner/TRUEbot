package de.zahrie.trues.api.coverage.stage.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import de.zahrie.trues.api.coverage.league.model.League;
import de.zahrie.trues.api.coverage.playday.config.PlaydayConfig;
import de.zahrie.trues.api.coverage.stage.Betable;
import de.zahrie.trues.api.coverage.stage.IdAble;
import de.zahrie.trues.api.database.QueryBuilder;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class PlayStage extends Stage implements Betable, IdAble, Playable, Serializable {
  @Serial
  private static final long serialVersionUID = 7394534903088339480L;


  public List<League> leagues() {
    return QueryBuilder.hql(League.class, "FROM League WHERE stage = :stage").addParameter("stage", this).list();
  }

  @Override
  public Integer pageId() {
    return null;
  }

  @Override
  public PlaydayConfig playdayConfig() {
    return null;
  }
}
