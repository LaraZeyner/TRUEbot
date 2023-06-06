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

