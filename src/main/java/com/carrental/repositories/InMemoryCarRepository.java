package com.carrental.repositories;

import com.carrental.models.Car;
import com.carrental.models.CarType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryCarRepository implements CarRepository {

    private final List<Car> cars = new CopyOnWriteArrayList<>();

    @Override
    public void save(Car car) {
        cars.add(car);
    }

    @Override
    public List<Car> findByType(CarType type) {
        return cars.stream()
                .filter(car -> car.getType() == type)
                .toList();
    }

    @Override
    public List<Car> findAll() {
        return List.copyOf(cars);
    }
}
