package com.dynacore.livemap.core.repository;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.Id;

@Table(value = "geometries")
public record GeometryEntity(@Id String id, String geo_type, String data_type, Geometry geom) {    }


