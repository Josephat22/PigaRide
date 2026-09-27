package com.bodaboda.app.utils;

public class FareCalculator {

    private static final double BASE_FARE = 50.0;
    private static final double RATE_PER_KM = 25.0;
    private static final double MINIMUM_FARE = 80.0;

    public static double estimateFare(double distanceKm) {
        double fare = BASE_FARE + (distanceKm * RATE_PER_KM);
        return Math.max(fare, MINIMUM_FARE);
    }
}
