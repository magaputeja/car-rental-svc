package com.carrental.services;

import com.carrental.models.CarType;
import com.carrental.models.Reservation;

import java.time.LocalDateTime;

public interface CarRentalService {

    Reservation reserve(CarType type, LocalDateTime startTime, int days);

    boolean isAvailable(CarType type, LocalDateTime startTime, int days);
}