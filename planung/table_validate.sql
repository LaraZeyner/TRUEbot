SELECT group_name                       as Gruppe,
       (SELECT count(*)
        FROM coverage
                 INNER JOIN coverage_group cg on coverage.coverage_group = cg.coverage_group_id
        WHERE cg.stage = 79
          AND cg.group_name = Gruppe
          AND coverage.result <> '-:-'
          AND coverage.result <> '0:0'
        ) as Closed,
       (SELECT count( DISTINCT coverage.coverage_id)
        FROM coverage
                 INNER JOIN coverage_group cg on coverage.coverage_group = cg.coverage_group_id
                 INNER JOIN game g on coverage.coverage_id = g.coverage
        WHERE cg.stage = 79
          AND cg.group_name = Gruppe
          AND coverage.result <> '-:-'
          AND coverage.result <> '0:0') as Linked,
    (SELECT Closed - Linked) as Missing
FROM coverage_group
WHERE stage = 79
HAVING Closed > 0
ORDER BY Missing DESC;