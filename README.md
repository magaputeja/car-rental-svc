# car-rental-svc

A simulated car rental system built with Java 17, Maven and JUnit 5.

A customer reserves a car **of a given type** (sedan, SUV or van) at a **desired date and time** for a
**number of days**. The number of cars of each type is limited by how many `Car` objects exist in the
car repository, and the system never books the same car for overlapping periods.

## Build, test and run

Prerequisites: JDK 17+ and Maven 3.8+.

```bash
mvn test                                           # compile and run all unit tests
mvn package                                        # build the jar
java -cp target/classes com.carrental.CarRentalDemo   # run the demo scenario
```

## Usage

```java
CarRepository carRepository = new InMemoryCarRepository();
carRepository.save(new Car("SEDAN-1", CarType.SEDAN));
carRepository.save(new Car("SEDAN-2", CarType.SEDAN));
carRepository.save(new Car("SUV-1",   CarType.SUV));
carRepository.save(new Car("VAN-1",   CarType.VAN));

CarRentalService service =
        new CarRentalServiceImpl(carRepository, new InMemoryReservationRepository());

LocalDateTime pickup = LocalDateTime.of(2030, 6, 1, 10, 0);

Reservation r = service.reserve(CarType.SUV, pickup, 3);   // [Jun 1 10:00, Jun 4 10:00)
service.isAvailable(CarType.SUV, pickup, 3);               // false: the only SUV is taken
service.reserve(CarType.SUV, pickup.plusDays(3), 2);       // OK: back-to-back with the first
```

| Situation                                                    | Exception                    |
|--------------------------------------------------------------|------------------------------|
| No car of the type is free for the whole period              | `CarUnavailableException`    |
| Null type or start time, `days < 1`, start time in the past  | `InvalidReservationException`|

## Behaviour

* **Half-open periods.** A rental occupies `[start, start + days)`. A car returned at 10:00 can be
  picked up by someone else at 10:00.
* **Limited inventory.** Each type has exactly as many cars as are registered in the `CarRepository`.
  Types are independent of each other.
* **Car reuse.** A car is free for a new rental if none of its existing reservations overlaps it.
  The first free car of the requested type is assigned.
* **Thread-safe.** `reserve` and `isAvailable` are synchronized and the in-memory repositories use
  thread-safe lists, so concurrent requests cannot overbook (a 32-thread test covers this).

## Project layout

```
src/main/java/com/carrental
  CarRentalDemo.java                    runnable example
  models/        Car, CarType, Reservation
  repositories/  CarRepository, ReservationRepository + in-memory implementations
  services/      CarRentalService (interface), CarRentalServiceImpl
  exceptions/    CarUnavailableException, InvalidReservationException
  utils/         ObjectUtils, StringUtils (argument checks)
src/test/java/com/carrental
  services/CarRentalServiceTest.java    booking rules, overlaps, limits, validation, concurrency
  models/ReservationTest.java           interval maths and validation
  models/CarTest.java                   equality and validation
docs/DESIGN.md                          design document
```

## Known limitations

See [`docs/DESIGN.md`](docs/DESIGN.md) section 5. In short: a car is chosen at booking time and never
re-assigned, so in rare orderings a request can be declined even though a re-shuffle of earlier
bookings would have fit it. There is also no cancellation, pricing, or time-zone handling.
