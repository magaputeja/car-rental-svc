package com.carrental.services;

import com.carrental.exceptions.CarUnavailableException;
import com.carrental.exceptions.InvalidReservationException;
import com.carrental.models.Car;
import com.carrental.models.CarType;
import com.carrental.models.Reservation;
import com.carrental.repositories.CarRepository;
import com.carrental.repositories.InMemoryCarRepository;
import com.carrental.repositories.InMemoryReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CarRentalServiceTest {

    private CarRentalService service;
    private LocalDateTime start;

    @BeforeEach
    void setUp() {
        CarRepository cars = new InMemoryCarRepository();
        cars.save(new Car("SEDAN-1", CarType.SEDAN));
        cars.save(new Car("SEDAN-2", CarType.SEDAN));
        cars.save(new Car("SUV-1", CarType.SUV));

        service = new CarRentalServiceImpl(cars, new InMemoryReservationRepository());
        start = LocalDateTime.now().plusDays(7)
                .withHour(10).withMinute(0).withSecond(0).withNano(0);
    }

    @Test
    void reserveSucceedsWhenCarIsAvailable() {
        Reservation reservation = service.reserve(CarType.SEDAN, start, 3);

        assertEquals(CarType.SEDAN, reservation.getCar().getType());
        assertEquals(start, reservation.getStartTime());
        assertEquals(start.plusDays(3), reservation.getEndTime());
    }

    @Test
    void overlappingRentalsGetDifferentCars() {
        Reservation first = service.reserve(CarType.SEDAN, start, 3);
        Reservation second = service.reserve(CarType.SEDAN, start, 3);

        assertNotEquals(first.getCar(), second.getCar());
    }

    @Test
    void reserveThrowsWhenTypeIsExhausted() {
        service.reserve(CarType.SUV, start, 3);

        assertThrows(CarUnavailableException.class,
                () -> service.reserve(CarType.SUV, start, 3));
    }

    @Test
    void backToBackRentalStartingWhenAnotherEndsIsAllowed() {
        service.reserve(CarType.SUV, start, 3);

        Reservation next = service.reserve(CarType.SUV, start.plusDays(3), 2);

        assertEquals(start.plusDays(3), next.getStartTime());
    }

    @Test
    void partialOverlapIsRejected() {
        service.reserve(CarType.SUV, start, 3);

        assertThrows(CarUnavailableException.class,
                () -> service.reserve(CarType.SUV, start.plusDays(2), 3));
    }

    @Test
    void rentalContainingExistingBookingIsRejected() {
        service.reserve(CarType.SUV, start, 3);

        assertThrows(CarUnavailableException.class,
                () -> service.reserve(CarType.SUV, start.minusDays(1), 6));
    }

    @Test
    void exhaustedTypeDoesNotBlockOtherTypes() {
        service.reserve(CarType.SUV, start, 3);

        Reservation sedan = service.reserve(CarType.SEDAN, start, 3);

        assertEquals(CarType.SEDAN, sedan.getCar().getType());
    }

    @Test
    void reserveRejectsDaysLessThanOne() {
        assertThrows(InvalidReservationException.class,
                () -> service.reserve(CarType.SEDAN, start, 0));
    }

    @Test
    void reserveRejectsStartInThePast() {
        assertThrows(InvalidReservationException.class,
                () -> service.reserve(CarType.SEDAN, LocalDateTime.now().minusDays(1), 2));
    }

    @Test
    void isAvailableReflectsExistingBookings() {
        assertTrue(service.isAvailable(CarType.SUV, start, 3));

        service.reserve(CarType.SUV, start, 3);

        assertFalse(service.isAvailable(CarType.SUV, start, 3));
        assertTrue(service.isAvailable(CarType.SUV, start.plusDays(3), 3));
    }

    @Test
    void typeCanBeBookedUpToItsLimitThenIsExhausted() {
        service.reserve(CarType.SEDAN, start, 3);
        service.reserve(CarType.SEDAN, start, 3);

        assertFalse(service.isAvailable(CarType.SEDAN, start, 3));
        assertThrows(CarUnavailableException.class,
                () -> service.reserve(CarType.SEDAN, start, 3));
    }

    @Test
    void typeWithNoCarsInFleetCanNeverBeReserved() {
        assertFalse(service.isAvailable(CarType.VAN, start, 1));
        assertThrows(CarUnavailableException.class,
                () -> service.reserve(CarType.VAN, start, 1));
    }

    @Test
    void rentalEndingExactlyWhenAnotherStartsIsAllowed() {
        service.reserve(CarType.SUV, start.plusDays(3), 2);

        Reservation earlier = service.reserve(CarType.SUV, start, 3);

        assertEquals(start.plusDays(3), earlier.getEndTime());
    }

    @Test
    void rentalOneMinuteIntoAnotherIsRejected() {
        service.reserve(CarType.SUV, start, 3);

        assertThrows(CarUnavailableException.class,
                () -> service.reserve(CarType.SUV, start.plusDays(3).minusMinutes(1), 1));
    }

    @Test
    void rentalFullyInsideAnExistingOneIsRejected() {
        service.reserve(CarType.SUV, start, 10);

        assertThrows(CarUnavailableException.class,
                () -> service.reserve(CarType.SUV, start.plusDays(4), 1));
    }

    @Test
    void sameCarIsReusedForNonOverlappingRentals() {
        Reservation first = service.reserve(CarType.SUV, start, 2);
        Reservation second = service.reserve(CarType.SUV, start.plusDays(5), 2);

        assertEquals(first.getCar(), second.getCar());
    }

    @Test
    void failedReservationDoesNotConsumeACar() {
        service.reserve(CarType.SUV, start, 3);
        assertThrows(CarUnavailableException.class,
                () -> service.reserve(CarType.SUV, start, 3));

        assertTrue(service.isAvailable(CarType.SUV, start.plusDays(3), 1));
    }

    @Test
    void invalidRequestDoesNotConsumeACar() {
        assertThrows(InvalidReservationException.class,
                () -> service.reserve(CarType.SUV, start, 0));

        assertTrue(service.isAvailable(CarType.SUV, start, 3));
    }

    @Test
    void reserveRejectsNullTypeAndNullStartTime() {
        assertThrows(InvalidReservationException.class,
                () -> service.reserve(null, start, 1));
        assertThrows(InvalidReservationException.class,
                () -> service.reserve(CarType.SEDAN, null, 1));
    }

    @Test
    void isAvailableValidatesItsArguments() {
        assertThrows(InvalidReservationException.class,
                () -> service.isAvailable(CarType.SEDAN, start, 0));
        assertThrows(InvalidReservationException.class,
                () -> service.isAvailable(CarType.SEDAN, LocalDateTime.now().minusDays(1), 1));
    }

    @Test
    void concurrentRequestsNeverOverbookATypeWithTwoCars() throws Exception {
        final int threads = 32;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch startGun = new CountDownLatch(1);
        List<Future<Boolean>> results = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            results.add(pool.submit(() -> {
                startGun.await();
                try {
                    service.reserve(CarType.SEDAN, start, 2);   // only 2 sedans exist
                    return true;
                } catch (CarUnavailableException e) {
                    return false;
                }
            }));
        }
        startGun.countDown();

        int successes = 0;
        for (Future<Boolean> result : results) {
            if (result.get()) {
                successes++;
            }
        }
        pool.shutdown();

        assertEquals(2, successes);
        assertFalse(service.isAvailable(CarType.SEDAN, start, 2));
    }
}
