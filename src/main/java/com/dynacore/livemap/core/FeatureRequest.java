package com.dynacore.livemap.core;

import java.time.OffsetDateTime;

public record FeatureRequest (Direction direction, OffsetDateTime startDate, Integer intervalMilliSec) { }

