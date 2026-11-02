package com.carrental.repositories;

import com.carrental.models.Car;
import com.carrental.models.Reservation;

import java.util.ArrayList;
import java.util.List;

public class InMemoryReservationRepository implements ReservationRepository {

    private final List<Reservation> reservations = new ArrayList<>();

    @Override
    public void save(Reservation reservation) {
        reservations.add(reservation);
    }

    @Override
    public List<Reservation> findByCar(Car car) {
        return reservations.stream()
                .filter(reservation -> reservation.getCar().equals(car))
                .toList();
    }

    @Override
    public List<Reservation> findAll() {
        return List.copyOf(reservations);
    }
}
