package com.dynacore.livemap.traveltime.domain;

import org.locationtech.jts.geom.Geometry;

public record FeatureLocation(String id, Geometry geom) { }
