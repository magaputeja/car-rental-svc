package com.carrental.models;

import java.util.Objects;

import com.carrental.utils.ObjectUtils;
import com.carrental.utils.StringUtils;

public class Car {

    private final String id;
    private final CarType type;

    public Car(String id, CarType type) {
        this.id = StringUtils.requireNotNullOrEmpty(id, "id");
        this.type = ObjectUtils.requireNonNull(type, "type");
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
