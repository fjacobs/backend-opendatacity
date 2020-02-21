
-- innerjoin:
SELECT geometries.pkey FROM geometries INNER JOIN geometries on geometries.id = travel_time_entity.id;


-- Update table from another table:

UPDATE guidance_sign_entity
SET fk_geometry = geometries.pkey
FROM geometries
WHERE guidance_sign_entity.id = geometries.id

