SET SESSION SQL_MODE = "";
SELECT _teamperf.team                                                                                              as Team,
       concat(round(avg(if(_teamperf.team = ?1, _teamperf.win, 1 - _teamperf.win)) * 100), '%')                    as Win,
       concat(sum(if(_teamperf.team = ?1, _teamperf.win, 0)), ':', sum(if(_teamperf.team = ?1, 0, _teamperf.win))) as Score,
       concat(round(avg(if(_teamperf.team = ?1, _teamperf.kills, null))), ':',
              round(avg(if(_teamperf.team = ?1, null, _teamperf.kills))))                                          as Kills,
       sum(if(_teamperf.team = ?1, _teamperf.kills, -_teamperf.kills))                                             as KD,
       concat(round(avg(if(_teamperf.team = ?1, _teamperf.total_gold, null)) / 1000), ':',
              round(avg(if(_teamperf.team = ?1, null, _teamperf.total_gold)) / 1000))                              as Gold,
       round(sum(if(_teamperf.team = ?1, _teamperf.total_gold, -_teamperf.total_gold)) / 1000)                     as GD,
       concat(round(avg(if(_teamperf.team = ?1, _teamperf.total_damage, null)) / 1000), ':',
              round(avg(if(_teamperf.team = ?1, null, _teamperf.total_damage)) / 1000))                            as Damage,
       round(sum(if(_teamperf.team = ?1, _teamperf.total_damage, -_teamperf.total_damage)) / 1000)                 as DD,
       concat(round(avg(if(_teamperf.team = ?1, _teamperf.total_creeps, null))), ':',
              round(avg(if(_teamperf.team = ?1, null, _teamperf.total_creeps))))                                   as Creeps,
       sum(if(_teamperf.team = ?1, _teamperf.total_creeps, -_teamperf.total_creeps))                               as CD,
       concat(round(avg(if(_teamperf.team = ?1, _teamperf.total_vision, null))), ':',
              round(avg(if(_teamperf.team = ?1, null, _teamperf.total_vision))))                                   as Vision,
       sum(if(_teamperf.team = ?1, _teamperf.total_vision,-_teamperf.total_vision))                                as VD,
       sum(if(_teamperf.team = ?1, _teamperf.turrets, 0))                                                          as Towers,
       sum(if(_teamperf.team = ?1, _teamperf.drakes, 0))                                                           as Drakes,
       sum(if(_teamperf.team = ?1, _teamperf.inhibs, 0))                                                           as Inhibs,
       sum(if(_teamperf.team = ?1, _teamperf.heralds, 0))                                                          as Heralds,
       sum(if(_teamperf.team = ?1, _teamperf.barons, 0))                                                           as Barons,
       concat(floor(avg(_game.duration) / 60), ':', lpad(avg(_game.duration) % 60, 2, '0'))                        as Spielzeit,
       concat(floor(avg(nullif(if(_teamperf.team = ?1, _teamperf.win * _game.duration, 0), 0)) / 60), ':',
              lpad(avg(nullif(if(_teamperf.team = ?1, _teamperf.win * _game.duration, 0), 0)) % 60, 2, '0'))       as Wins,
       concat(floor(avg(nullif(if(_teamperf.team = ?1, 0, _teamperf.win * _game.duration), 0)) / 60), ':',
              lpad(avg(nullif(if(_teamperf.team = ?1, 0, _teamperf.win * _game.duration), 0)) % 60, 2, '0'))       as Losses
FROM team_perf as _teamperf
         INNER JOIN game _game on _teamperf.game = _game.game_id
WHERE _game.coverage IN
      (SELECT _match.coverage_id
       FROM coverage_team as _participator
                INNER JOIN coverage as _match on _participator.coverage = _match.coverage_id
       WHERE _participator.team = ?1 AND _match.coverage_group = ?2);