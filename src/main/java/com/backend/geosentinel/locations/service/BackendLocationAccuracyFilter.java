package com.backend.geosentinel.locations.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

/**
 * Optional backend-side second line of defense.
 *
 * Use this before persisting/broadcasting an IoT location.
 * It deliberately does not depend on your existing JPA entity/repository
 * classes, so it can be adapted without changing your database model.
 */
@Service
public class BackendLocationAccuracyFilter {

    private static final int WINDOW_SIZE = 5;
    private static final int MIN_SATELLITES = 5;
    private static final double MAX_HDOP = 4.0;
    private static final double STATIONARY_SPEED_KMPH = 3.0;
    private static final double STATIONARY_RADIUS_METERS = 25.0;
    private static final double MAX_JUMP_METERS = 150.0;
    private static final int MOVEMENT_CONFIRMATIONS = 3;

    private final Map<String, State> states = new ConcurrentHashMap<>();

    public Result process(
            String devicePublicId,
            double latitude,
            double longitude,
            double speedKmph,
            Integer satellites,
            Double hdop,
            Long gpsAgeMs) {

        if (!Double.isFinite(latitude) || !Double.isFinite(longitude)) {
            return Result.rejected("INVALID_COORDINATES");
        }

        if (satellites != null && satellites < MIN_SATELLITES) {
            return Result.rejected("LOW_SATELLITE_COUNT");
        }

        if (hdop != null && (!Double.isFinite(hdop) || hdop > MAX_HDOP)) {
            return Result.rejected("HIGH_HDOP");
        }

        if (gpsAgeMs != null && gpsAgeMs > 5000) {
            return Result.rejected("STALE_GPS_FIX");
        }

        State state = states.computeIfAbsent(devicePublicId, ignored -> new State());

        synchronized (state) {
            state.samples.addLast(new Point(latitude, longitude));
            while (state.samples.size() > WINDOW_SIZE) {
                state.samples.removeFirst();
            }

            Point filtered = medianPoint(state.samples);

            if (state.lastAccepted == null) {
                state.lastAccepted = filtered;
                if (speedKmph < STATIONARY_SPEED_KMPH) {
                    state.stationary = true;
                    state.anchor = filtered;
                }
                return Result.accepted(filtered, true, "FIRST_FIX");
            }

            if (speedKmph >= STATIONARY_SPEED_KMPH) {
                state.stationary = false;
                state.movementConfirmations = 0;
            }

            if (state.stationary && speedKmph < STATIONARY_SPEED_KMPH) {
                double anchorDistance = distanceMeters(state.anchor, filtered);

                if (anchorDistance <= STATIONARY_RADIUS_METERS) {
                    return Result.accepted(state.lastAccepted, true, "STATIONARY_LOCK");
                }

                state.movementConfirmations++;

                if (state.movementConfirmations < MOVEMENT_CONFIRMATIONS) {
                    return Result.accepted(state.lastAccepted, true, "WAITING_FOR_MOVEMENT_CONFIRMATION");
                }

                state.stationary = false;
                state.movementConfirmations = 0;
            }

            double jump = distanceMeters(state.lastAccepted, filtered);

            if (jump > MAX_JUMP_METERS) {
                return Result.rejected("GPS_JUMP_" + Math.round(jump) + "M");
            }

            state.lastAccepted = filtered;

            if (speedKmph < STATIONARY_SPEED_KMPH && jump < STATIONARY_RADIUS_METERS) {
                state.stationary = true;
                state.anchor = filtered;
            }

            return Result.accepted(filtered, true, "FILTERED_FIX");
        }
    }

    private static Point medianPoint(Deque<Point> points) {
        ArrayList<Double> lats = new ArrayList<>(points.size());
        ArrayList<Double> lons = new ArrayList<>(points.size());

        for (Point point : points) {
            lats.add(point.latitude());
            lons.add(point.longitude());
        }

        lats.sort(Comparator.naturalOrder());
        lons.sort(Comparator.naturalOrder());

        return new Point(median(lats), median(lons));
    }

    private static double median(ArrayList<Double> values) {
        int middle = values.size() / 2;
        if (values.size() % 2 == 1) {
            return values.get(middle);
        }
        return (values.get(middle - 1) + values.get(middle)) / 2.0;
    }

    private static double distanceMeters(Point a, Point b) {
        final double earthRadius = 6_371_000.0;
        final double lat1 = Math.toRadians(a.latitude());
        final double lat2 = Math.toRadians(b.latitude());
        final double dLat = lat2 - lat1;
        final double dLon = Math.toRadians(b.longitude() - a.longitude());

        double h = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return 2 * earthRadius * Math.asin(Math.sqrt(h));
    }

    private static final class State {
        private final Deque<Point> samples = new ArrayDeque<>();
        private Point lastAccepted;
        private Point anchor;
        private boolean stationary;
        private int movementConfirmations;
    }

    public record Point(double latitude, double longitude) {}

    public record Result(
            boolean accepted,
            Point point,
            boolean filtered,
            String reason) {

        public static Result accepted(Point point, boolean filtered, String reason) {
            return new Result(true, point, filtered, reason);
        }

        public static Result rejected(String reason) {
            return new Result(false, null, true, reason);
        }
    }
}
