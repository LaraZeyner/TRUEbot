package de.zahrie.trues.models.riot.rank;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum RankTier {
  Unranked(0),
  Iron(100),
  Bronze(500),
  Silver(900),
  Gold(1300),
  Platinum(1700),
  Diamond(2100),
  Master(2500),
  Grandmaster(2500),
  Challenger(2500);

  final int mmr;

}
