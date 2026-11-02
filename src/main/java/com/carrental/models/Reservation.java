package com.carrental.models;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Reservation {

    private final Car car;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public Reservation(Car car, LocalDateTime startTime, int days) {
        this.car = Objects.requireNonNull(car, "car");
        this.startTime = Objects.requireNonNull(startTime, "startTime");
        if (days < 1) {
            throw new IllegalArgumentException("The minimum reservation is one (1) day");
        }
        this.endTime = startTime.plusDays(days);
    }

    public Car getCar() {
        return car;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public long getDays() {
        return ChronoUnit.DAYS.between(startTime, endTime);
    }

    /**
     * Half-open interval overlap: [startTime, endTime).
     * A rental that starts exactly when this one ends does not overlap.
     */
    public boolean overlaps(LocalDateTime start, LocalDateTime end) {
        return start.isBefore(endTime) && startTime.isBefore(end);
    }

    @Override
    public String toString() {
        return "Reservation information: Car = " + car + ", start = " + startTime + ", end = " + endTime + ".";
    }
}
