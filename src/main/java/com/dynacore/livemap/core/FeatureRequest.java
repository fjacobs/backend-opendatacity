package com.dynacore.livemap.core;

import java.time.Duration;
import java.time.OffsetDateTime;

public record FeatureRequest (Direction direction, OffsetDateTime startDate, Duration interval) { }

