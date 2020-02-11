
create table travel_time_entity
(
    pkey                       serial                   not null
        constraint travel_time_entity_pkey
            primary key,
    id                         varchar(200),
    name                       varchar(200),
    pub_date                   timestamp with time zone not null,
    our_retrieval timestamp with time zone not null,
    type                       varchar(50),
    length                     smallint
        constraint travel_time_entity_length_check
            check (length >= '-1'::integer),
    velocity                   smallint
        constraint travel_time_entity_velocity_check
            check (velocity >= '-1'::integer),
    travel_time                smallint
        constraint travel_time_entity_travel_time_check
            check (travel_time >= '-1'::integer),
    constraint travel_time_entity_id_pub_date_key
        unique (id, pub_date)
);
