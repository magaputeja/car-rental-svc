package com.carrental.repositories;

import com.carrental.models.Car;
import com.carrental.models.Reservation;

import java.util.List;

public interface ReservationRepository {

    void save(Reservation reservation);

    List<Reservation> findByCar(Car car);

    List<Reservation> findAll();
}