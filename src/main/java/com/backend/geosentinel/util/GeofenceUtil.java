package com.backend.geosentinel.util;

import java.math.BigDecimal;

public class GeofenceUtil {

    private static final double EARTH_RADIUS = 6371000;

    public static boolean isInside(
            double deviceLat,
            double deviceLng,
            double fenceLat,
            double fenceLng,
            double radius) {

        double dLat = Math.toRadians(fenceLat - deviceLat);
        double dLng = Math.toRadians(fenceLng - deviceLng);

        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(deviceLat))
                        * Math.cos(Math.toRadians(fenceLat))
                        * Math.sin(dLng / 2)
                        * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS * c;

        return distance <= radius;
    }


}