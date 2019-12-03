
CREATE TABLE travel_time_entity
(
    pkey SERIAL PRIMARY KEY,
    id                         VARCHAR(50),
    name                       VARCHAR(50),
    pub_date                   TIMESTAMP NOT NULL,
    retrieved_from_third_party TIMESTAMP NOT NULL,
    type                       VARCHAR(50),
    length                     SMALLINT CHECK (length >= -1),
    velocity                   SMALLINT CHECK (velocity >= -1),
    travel_time                SMALLINT CHECK (travel_time >= -1),
    unique (id, pub_date)
);