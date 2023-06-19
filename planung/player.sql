(SELECT *
 FROM (SELECT _player.player_id                                                               PId,
              _player.lol_name                                                                Summoner,
              t.team_name                                                                     Team,
              (SELECT count(*) FROM performance _performance WHERE _performance.player = PId) Games,
              _player.updated
       FROM league_team _leagueteam
                JOIN player _player on _leagueteam.team = _player.team
                JOIN team t on _leagueteam.team = t.team_id
                LEFT JOIN orga_team ot on t.team_id = ot.team
       WHERE league IN ((SELECT league
                         FROM league_team _leagueteam
                                  JOIN orga_team _orgateam on _leagueteam.team = _orgateam.team
                                  JOIN coverage_group _league on _leagueteam.league = _league.coverage_group_id
                                  JOIN coverage_stage _stage on _league.stage = _stage.coverage_stage_id
                         WHERE _stage.season = 15))
         AND ot.orga_team_id is null
         AND _leagueteam.team IN (SELECT _participator.team FROM coverage_team _participator
                                  WHERE _participator.coverage IN (SELECT * FROM (SELECT _match.coverage_id
                                                                                  FROM coverage_team as `_participator`
                                                                                           INNER JOIN `coverage` as `_match` ON `_participator`.`coverage` = `_match`.`coverage_id`
                                                                                           INNER JOIN `team` as `_team` ON _participator.team = `_team`.`team_id`
                                                                                           INNER JOIN `orga_team` as `_orgateam` ON `_team`.`team_id` = `_orgateam`.`team`
                                                                                  WHERE (_match.result = '-:-')
                                                                                    and (_match.coverage_start <= '2023-06-16 10:51:00')
                                                                                  ORDER BY `_match`.`coverage_start`
                                                                                  LIMIT 1000) as `pmtom.*`)
       )) as lptopi);

SET SESSION SQL_MODE = "";
SELECT _teamperf.team                                                                              as Team,
       concat(round(avg(if(Team = ?1, _teamperf.win, 1 - _teamperf.win)) * 100), '%')              as Win,
       concat(sum(if(Team = ?1, _teamperf.win, 0)), ':', sum(if(Team = ?1, 0, _teamperf.win)))     as Score,
       concat(round(avg(if(Team = ?1, _teamperf.kills, null))), ':',
              round(avg(if(Team = ?1, null, _teamperf.kills))))                                    as Kills,
       sum(if(Team = ?1, _teamperf.kills, -_teamperf.kills))                                       as KD,
       concat(round(avg(if(Team = ?1, _teamperf.total_gold, null)) / 1000), ':',
              round(avg(if(Team = ?1, null, _teamperf.total_gold)) / 1000))                        as Gold,
       round(sum(if(Team = ?1, _teamperf.total_gold, -_teamperf.total_gold)) / 1000)               as GD,
       concat(round(avg(if(Team = ?1, _teamperf.total_damage, null)) / 1000), ':',
              round(avg(if(Team = ?1, null, _teamperf.total_damage)) / 1000))                      as Damage,
       round(sum(if(Team = ?1, _teamperf.total_damage, -_teamperf.total_damage)) / 1000)           as DD,
       concat(round(avg(if(Team = ?1, _teamperf.total_creeps, null))), ':',
              round(avg(if(Team = ?1, null, _teamperf.total_creeps))))                             as Creeps,
       sum(if(Team = ?1, _teamperf.total_creeps, -_teamperf.total_creeps))                         as CD,
       concat(round(avg(if(Team = ?1, _teamperf.total_vision, null))), ':',
              round(avg(if(Team = ?1, null, _teamperf.total_vision))))                             as Vision,
       sum(if(Team = ?1, _teamperf.total_vision, -_teamperf.total_vision))                         as VD,
       sum(if(Team = ?1, _teamperf.turrets, 0))                                                    as Towers,
       sum(if(Team = ?1, _teamperf.drakes, 0))                                                     as Drakes,
       sum(if(Team = ?1, _teamperf.inhibs, 0))                                                     as Inhibs,
       sum(if(Team = ?1, _teamperf.heralds, 0))                                                    as Heralds,
       sum(if(Team = ?1, _teamperf.barons, 0))                                                     as Barons,
       concat(floor(avg(_game.duration) / 60), ':', lpad(avg(_game.duration) % 60, 2, '0'))        as Spielzeit,
       concat(floor(avg(nullif(if(Team = ?1, _teamperf.win * _game.duration, 0), 0)) / 60), ':',
              lpad(avg(nullif(if(Team = ?1, _teamperf.win * _game.duration, 0), 0)) % 60, 2, '0')) as Wins,
       concat(floor(avg(nullif(if(Team = ?1, 0, _teamperf.win * _game.duration), 0)) / 60), ':',
              lpad(avg(nullif(if(Team = ?1, 0, _teamperf.win * _game.duration), 0)) % 60, 2, '0')) as Losses
FROM team_perf as _teamperf
         INNER JOIN game _game on _teamperf.game = _game.game_id
WHERE _game.coverage IN
      (SELECT _match.coverage_id
       FROM coverage_team as _participator
                INNER JOIN coverage as _match on _participator.coverage = _match.coverage_id
       WHERE team = ?1
         AND _match.coverage_group = 77)