package com.carrental.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservationTest {

    private static final LocalDateTime START = LocalDateTime.of(2030, 3, 1, 12, 0);
    private final Car car = new Car("SUV-1", CarType.SUV);
    private final Reservation reservation = new Reservation(car, START, 2); // [Mar 1 12:00, Mar 3 12:00)

    @Test
    void endTimeIsStartPlusDays() {
        assertEquals(START.plusDays(2), reservation.getEndTime());
        assertEquals(2L, reservation.getDays());
    }

    @Test
    void overlapIsHalfOpen() {
        assertTrue(reservation.overlaps(START.minusDays(1), START.plusSeconds(1)));
        assertTrue(reservation.overlaps(START.plusHours(1), START.plusHours(2)));
        assertFalse(reservation.overlaps(START.minusDays(1), START));            // touches only at start
        assertFalse(reservation.overlaps(START.plusDays(2), START.plusDays(3))); // touches only at end
    }

    @Test
    void rejectsInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> new Reservation(car, START, 0));
        assertThrows(NullPointerException.class, () -> new Reservation(null, START, 1));
        assertThrows(NullPointerException.class, () -> new Reservation(car, null, 1));
    }
}
