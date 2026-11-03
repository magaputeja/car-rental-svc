package com.carrental;

import com.carrental.exceptions.CarUnavailableException;
import com.carrental.models.Car;
import com.carrental.models.CarType;
import com.carrental.models.Reservation;
import com.carrental.repositories.CarRepository;
import com.carrental.repositories.InMemoryCarRepository;
import com.carrental.repositories.InMemoryReservationRepository;
import com.carrental.repositories.ReservationRepository;
import com.carrental.services.CarRentalService;
import com.carrental.services.CarRentalServiceImpl;

import java.time.LocalDateTime;

public class CarRentalDemo {

    public static void main(String[] args) {
        CarRepository carRepository = new InMemoryCarRepository();
        ReservationRepository reservationRepository = new InMemoryReservationRepository();

        carRepository.save(new Car("SEDAN-1", CarType.SEDAN));
        carRepository.save(new Car("SEDAN-2", CarType.SEDAN));
        carRepository.save(new Car("SUV-1", CarType.SUV));
        carRepository.save(new Car("VAN-1", CarType.VAN));

        CarRentalService service = new CarRentalServiceImpl(carRepository, reservationRepository);

        LocalDateTime friday = LocalDateTime.now().plusDays(3)
                .withHour(10).withMinute(0).withSecond(0).withNano(0);

        Reservation first = service.reserve(CarType.SUV, friday, 3);
        System.out.println("Booked:          " + first);

        try {
            service.reserve(CarType.SUV, friday.plusDays(1), 2);
        } catch (CarUnavailableException e) {
            System.out.println("Declined:        " + e.getMessage());
        }

        Reservation backToBack = service.reserve(CarType.SUV, friday.plusDays(3), 2);
        System.out.println("Back-to-back OK: " + backToBack);
    }
}
