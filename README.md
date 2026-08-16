# CodeAlpha_HotelReservationSystem
Java-based Hotel Reservation System developed for CodeAlpha Java Programming Internship Task 4 using OOP and File I/O.
# CodeAlpha Hotel Reservation System

A Java-based Hotel Reservation System developed as **Task 4** for the CodeAlpha Java Programming Internship.

The project is a console-based application that allows users to search hotel rooms, make reservations, view booking details, make simulated payments, and cancel bookings.

## Features

* View all hotel rooms
* Search available rooms by room type
* Standard, Deluxe, and Suite room categories
* Book available rooms
* Customer name and phone number validation
* Check-in and check-out date handling
* Automatic calculation of number of nights
* Automatic calculation of total booking amount
* Unique booking ID generation
* View individual booking details
* Simulated payment system
* Payment status tracking
* Cancel reservations
* View all bookings
* File I/O for saving and loading reservation data
* Room availability updates automatically

## Technologies Used

* Java
* Object-Oriented Programming (OOP)
* ArrayList
* File I/O
* Java Date and Time API
* Scanner

## Room Categories

| Room Type | Price per Night |
| --------- | --------------: |
| Standard  |           ₹1500 |
| Deluxe    |           ₹2500 |
| Suite     |           ₹4000 |

## How It Works

The system provides a menu-driven console interface.

### Main Menu

1. View Rooms
2. Search Rooms
3. Book Room
4. View Booking Details
5. Make Payment
6. Cancel Booking
7. View All Bookings
8. Exit

When a room is booked, the system calculates the total amount based on the number of nights.

**Total Amount = Price per Night × Number of Nights**

Payment is simulated within the application. A newly created booking starts with a `PENDING` payment status and can later be changed to `PAID`.

Cancelled rooms become available again.

## File Storage

The application uses a text file named `reservations.txt` to store reservation information.

The file is automatically created when required and is used to preserve booking information between program runs.

## Project Structure

```text
CodeAlpha_HotelReservationSystem
│
├── Main.java
└── README.md
```

## How to Run

### 1. Compile

```bash
javac Main.java
```

### 2. Run

```bash
java Main
```

## Example

```text
===== Hotel Reservation System =====

1. View Rooms
2. Search Rooms
3. Book Room
4. View Booking Details
5. Make Payment
6. Cancel Booking
7. View All Bookings
8. Exit
```

## Internship Task

**Program:** CodeAlpha Java Programming Internship

**Task:** Task 4 - Hotel Reservation System

**Developer:** Monalika

## Learning Outcomes

Through this project, I practiced:

* Java Object-Oriented Programming
* Classes and objects
* ArrayList collections
* File handling
* Exception handling
* Input validation
* Date calculations
* Menu-driven application development
* Basic software project organization
