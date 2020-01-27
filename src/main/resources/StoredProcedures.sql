

-- Get all replay metadata containing the number of features by pubdate,
-- 2020-01-21 23:51:01.000000	1012
-- 2020-01-21 23:46:00.000000	1012

SELECT pub_date, COUNT (pub_date) FROM public.travel_time_entity GROUP BY travel_time_entity.pub_date ORDER BY pub_date DESC;


-- Like above but get pairs of metadata by date range
SELECT pub_date, COUNT (pub_date)
FROM
    travel_time_entity
WHERE
        pub_date >= '2020-01-21 22:00:00.000000'
  AND    pub_date < '2020-01-21 23:51:01.000000'

GROUP BY travel_time_entity.pub_date ORDER BY pub_date DESC;


-- Get TravelTimeEntities by date range

SELECT *
FROM
    public.travel_time_entity
WHERE
        pub_date >= '2020-01-21 22:00:00.000000'
  AND    pub_date < '2020-01-21 23:51:01.000000'

ORDER BY pub_date DESC;


