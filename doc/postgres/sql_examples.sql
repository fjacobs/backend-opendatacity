
-- innerjoin:
SELECT geometries.pkey FROM geometries INNER JOIN geometries on geometries.id = travel_time_entity.id;

--
SELECT geometries.id, geom from geometries INNER JOIN travel_time_entity on geometries.pkey = travel_time_entity.fk_geometry;

---
    SELECT geometries.id, st_astext(geom) from geometries INNER JOIN travel_time_entity on geometries.pkey = travel_time_entity.fk_geometry;
    SELECT geometries.id, st_astext(geom) from geometries INNER JOIN guidance_sign_entity on geometries.pkey = guidance_sign_entity.fk_geometry;
---

SELECT geometries.id, geometries.geo_type, geometries.data_type geom FROM geometries where geom && ST_MakeEnvelope(4.577447733334896,52.28670934090339,  5.22255118792474,52.45440226066263,  4326);


-- LEFT OUTER JOIN (return LEFT geometrys met ids van RIGHT)

SELECT COUNT(geometries.geom, travel_time_entity.id) FROM geometries left outer join travel_time_entity on geometries.pkey = travel_time_entity.fk_geometry;

where geom && ST_MakeEnvelope(4.577447733334896,52.28670934090339,  5.22255118792474,52.45440226066263,  4326);


SELECT COUNT(travel_time_entity.id) FROM geometries left outer join travel_time_entity on geometries.pkey = travel_time_entity.fk_geometry;

SELECT travel_time_entity.id, geom from geometries INNER JOIN travel_time_entity on geometries.pkey = travel_time_entity.fk_geometry;



SELECT count(travel_time_entity.id, geom) from geometries INNER JOIN travel_time_entity on geometries.pkey = travel_time_entity.fk_geometry where geom && ST_MakeEnvelope(4.577447733334896,52.28670934090339,  5.22255118792474,52.45440226066263,  4326);


                      -- west=4.577447733334896,  south=52.28670934090339,  east=5.22255118792474, north=52.45440226066263]
-- geometry ST_MakeEnvelope(float xmin,            float ymin,              float xmax,            float ymax, integer srid=unknown);


-- Update table from another table:

UPDATE guidance_sign_entity
SET fk_geometry = geometries.pkey
FROM geometries
WHERE guidance_sign_entity.id = geometries.id




SELECT travel_time_entity.id, geometries.geom FROM geometries inner join travel_time_entity on geometries.pkey = travel_time_entity.fk_geometry group by geometries.geom, travel_time_entity.id where geom && ST_MakeEnvelope(4.577447733334896,52.28670934090339,  5.22255118792474,52.45440226066263,  4326);

select * from (
                  SELECT id,
                         ROW_NUMBER() OVER(PARTITION BY geom ORDER BY id) AS Row
                  FROM geometries
              ) dups
where
        dups.Row > 1




-- No index
    13088.206
Time: 11898.654 ms (00:11.899) (918 rows)

SELECT distinct travel_time_entity.id, st_astext(geom) FROM geometries inner join travel_time_entity on  travel_time_entity.fk_geometry = geometries.pkey  where geom && ST_MakeEnvelope(4.577447733334896,52.28670934090339,  5.22255118792474, 52.45440226066263,  4326);




Time: 10002.233 ms (00:10.002) (1011 rows)
SELECT travel_time_entity.id, st_astext(geom) FROM geometries inner join travel_time_entity on geometries.pkey = travel_time_entity.fk_geometry where geom && ST_MakeEnvelope(4.577447733334896,52.28670934090339,  5.22255118792474, 52.45440226066263,  4326) group by travel_time_entity.id, st_astext(geom);





