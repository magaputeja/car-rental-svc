package com.carrental.services;

import com.carrental.exceptions.CarUnavailableException;
import com.carrental.exceptions.InvalidReservationException;
import com.carrental.models.Car;
import com.carrental.models.CarType;
import com.carrental.models.Reservation;
import com.carrental.repositories.CarRepository;
import com.carrental.repositories.ReservationRepository;
import com.carrental.utils.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Optional;

public class CarRentalServiceImpl implements CarRentalService {

    private final CarRepository carRepository;
    private final ReservationRepository reservationRepository;

    public CarRentalServiceImpl(CarRepository carRepository,
                                ReservationRepository reservationRepository) {
        this.carRepository = carRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public synchronized Reservation reserve(CarType type, LocalDateTime startTime, int days) {
        validate(type, startTime, days);
        LocalDateTime endTime = startTime.plusDays(days);

        Car car = findAvailableCar(type, startTime, endTime)
                .orElseThrow(() -> new CarUnavailableException(
                        "No " + type + " available from " + startTime
                                + " for " + days + " day(s)"));

        Reservation reservation = new Reservation(car, startTime, days);
        reservationRepository.save(reservation);
        return reservation;
    }

    @Override
    public synchronized boolean isAvailable(CarType type, LocalDateTime startTime, int days) {
        validate(type, startTime, days);
        LocalDateTime endTime = startTime.plusDays(days);
        return findAvailableCar(type, startTime, endTime).isPresent();
    }

    private Optional<Car> findAvailableCar(CarType type, LocalDateTime start, LocalDateTime end) {
        return carRepository.findByType(type).stream()
                .filter(car -> reservationRepository.findByCar(car).stream()
                        .noneMatch(existing -> existing.overlaps(start, end)))
                .findFirst();
    }

    private void validate(CarType type, LocalDateTime startTime, int days) {
        if (ObjectUtils.isNull(type)) {
            throw new InvalidReservationException("Car type is required");
        }
        if (ObjectUtils.isNull(startTime)) {
            throw new InvalidReservationException("Start time is required");
        }
        if (days < 1) {
            throw new InvalidReservationException("Days must be at least 1");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new InvalidReservationException("Start time cannot be in the past");
        }
    }
}
