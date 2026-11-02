package com.carrental.models;

import java.util.Objects;

public class Car {

    private final String id;
    private final CarType type;

    public Car(String id, CarType type) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
    }

    public String getId() {
        return id;
    }

    public CarType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Car other)) {
            return false;
        }
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Car: id = " + id + ", type = " + type + ".";
    }
}
