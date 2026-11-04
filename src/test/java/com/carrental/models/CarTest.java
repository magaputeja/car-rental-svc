package com.carrental.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CarTest {

    @Test
    void carsAreEqualWhenTheirIdsMatch() {
        assertEquals(new Car("A", CarType.SEDAN), new Car("A", CarType.SEDAN));
        assertNotEquals(new Car("A", CarType.SEDAN), new Car("B", CarType.SEDAN));
    }

    @Test
    void rejectsBlankIdAndNullType() {
        assertThrows(IllegalArgumentException.class, () -> new Car(" ", CarType.SEDAN));
        assertThrows(IllegalArgumentException.class, () -> new Car(null, CarType.SEDAN));
        assertThrows(NullPointerException.class, () -> new Car("A", null));
    }
}
