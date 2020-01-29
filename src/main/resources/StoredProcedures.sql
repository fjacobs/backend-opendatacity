

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

-- compare values between previous and next date: (for testing)
-- (see: https://fle.github.io/detect-value-changes-between-successive-lines-with-postgresql.html)
SELECT
    id,
    name,
    velocity,
    pub_date,
    lead(velocity) OVER (ORDER BY pub_date  ) as prev_velocity,
    lag(velocity) OVER (ORDER BY pub_date ) as next_velocity,
    lead(travel_time) OVER (ORDER BY travel_time  ) as prev_traveltime,
    lag(travel_time) OVER (ORDER BY travel_time ) as next_traveltime
FROM
    public.travel_time_entity
WHERE
        id = 'RWS01_MONIBAS_0101hrr0113ra0'
ORDER BY
    pub_date;

-- ONLY changed velocity for a specific ID
SELECT
    id, w1.pub_date, w1.velocity, w1.length, w1.travel_time, prev_velocity
FROM
    (SELECT
         w2.id,
         w2.pub_date,
         w2.velocity,
         w2.length,
         w2.travel_time,

         lead(w2.velocity) OVER (ORDER BY w2.pub_date) as prev_velocity
     FROM
         public.travel_time_entity w2 WHERE id = 'RWS01_MONIBAS_0101hrr0113ra0'
     ORDER BY
         w2.pub_date) as w1
WHERE
    (w1.velocity IS DISTINCT FROM prev_velocity) AND (id = 'RWS01_MONIBAS_0101hrr0113ra0')

ORDER BY
    w1.pub_date;


SELECT
     w1.pub_date, w1.velocity, prev_velocity
FROM
    (SELECT
         w2.id,
         w2.pub_date,
         w2.velocity,
         lead(w2.velocity) OVER (ORDER BY w2.pub_date) as prev_velocity
     FROM
         public.travel_time_entity w2
     ORDER BY
         w2.pub_date) as w1
WHERE
    (w1.velocity IS DISTINCT FROM prev_velocity)

ORDER BY
    w1.pub_date;



