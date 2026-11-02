package com.carrental.repositories;

import com.carrental.models.Car;
import com.carrental.models.CarType;

import java.util.List;

public interface CarRepository {

    void save(Car car);

    List<Car> findByType(CarType type);

    List<Car> findAll();
}
