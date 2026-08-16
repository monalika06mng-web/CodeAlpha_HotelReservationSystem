import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

// Represents a single hotel room
class Room {
    int number;
    String type;
    double price;
    boolean available;

    Room(int number, String type, double price) {
        this.number = number;
        this.type = type;
        this.price = price;
        this.available = true;
    }

    public String toString() {
        String status = available ? "Available" : "Booked";
        return "Room " + number + " | " + type + " | Rs." + price + "/night | " + status;
    }
}

// Represents a single reservation
class Booking {
    static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    String id, guestName, phone, paymentStatus, status; // status: CONFIRMED/CANCELLED
    int roomNumber;
    LocalDate checkIn, checkOut;
    double amount;

    Booking(String id, String guestName, String phone, int roomNumber, LocalDate checkIn,
            LocalDate checkOut, double amount, String paymentStatus, String status) {
        this.id = id;
        this.guestName = guestName;
        this.phone = phone;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.status = status;
    }

    long nights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    String toFileLine() {
        return id + "," + guestName + "," + phone + "," + roomNumber + "," + checkIn.format(FMT) + "," +
                checkOut.format(FMT) + "," + amount + "," + paymentStatus + "," + status;
    }

    static Booking fromFileLine(String line) {
        String[] p = line.split(",");
        return new Booking(p[0], p[1], p[2], Integer.parseInt(p[3]), LocalDate.parse(p[4], FMT),
                LocalDate.parse(p[5], FMT), Double.parseDouble(p[6]), p[7], p[8]);
    }

    public String toString() {
        return "ID: " + id + " | Guest: " + guestName + " | Room: " + roomNumber +
                " | " + checkIn.format(FMT) + " to " + checkOut.format(FMT) +
                " | Total: Rs." + amount + " | Payment: " + paymentStatus + " | Status: " + status;
    }
}

// Core hotel logic: rooms, bookings, and file storage
class Hotel {
    private static final String FILE_NAME = "reservations.txt";
    private List<Room> rooms = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private int counter = 100;

    Hotel() {
        setupRooms();
        loadBookings();
    }

    private void setupRooms() {
        rooms.add(new Room(101, "Standard", 1500));
        rooms.add(new Room(102, "Standard", 1500));
        rooms.add(new Room(201, "Deluxe", 2500));
        rooms.add(new Room(202, "Deluxe", 2500));
        rooms.add(new Room(301, "Suite", 4000));
        rooms.add(new Room(302, "Suite", 4000));
    }

    private Room findRoom(int number) {
        for (Room r : rooms) if (r.number == number) return r;
        return null;
    }

    private Booking findBooking(String id) {
        for (Booking b : bookings) if (b.id.equalsIgnoreCase(id)) return b;
        return null;
    }

    void showRooms() {
        System.out.println("\n--- All Rooms ---");
        for (Room r : rooms) System.out.println(r);
    }

    void searchRooms(String type) {
        if (!type.equalsIgnoreCase("Standard") && !type.equalsIgnoreCase("Deluxe") && !type.equalsIgnoreCase("Suite")) {
            System.out.println("Error: Room type must be Standard, Deluxe, or Suite.");
            return;
        }
        System.out.println("\n--- Available " + type + " Rooms ---");
        boolean found = false;
        for (Room r : rooms) {
            if (r.type.equalsIgnoreCase(type) && r.available) {
                System.out.println(r);
                found = true;
            }
        }
        if (!found) System.out.println("No available rooms of this type.");
    }

    void bookRoom(Scanner sc) {
        System.out.print("Enter your name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) { System.out.println("Error: Name cannot be empty."); return; }

        System.out.print("Enter phone number (10 digits): ");
        String phone = sc.nextLine().trim();
        if (!phone.matches("\\d{10}")) { System.out.println("Error: Phone number must be exactly 10 digits."); return; }

        System.out.print("Enter room number: ");
        int number;
        try {
            number = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Error: Room number must be numeric.");
            return;
        }

        Room room = findRoom(number);
        if (room == null || !room.available) {
            System.out.println("Error: Room does not exist or is already booked.");
            return;
        }

        LocalDate checkIn, checkOut;
        try {
            System.out.print("Enter check-in date (dd-MM-yyyy): ");
            checkIn = LocalDate.parse(sc.nextLine().trim(), Booking.FMT);
            System.out.print("Enter check-out date (dd-MM-yyyy): ");
            checkOut = LocalDate.parse(sc.nextLine().trim(), Booking.FMT);
        } catch (DateTimeParseException e) {
            System.out.println("Error: Date must be in dd-MM-yyyy format.");
            return;
        }
        if (!checkOut.isAfter(checkIn)) { System.out.println("Error: Check-out date must be after check-in date."); return; }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double total = nights * room.price;
        System.out.println("Nights: " + nights + " | Total Amount: Rs." + total);

        String id = "BK" + (++counter);
        bookings.add(new Booking(id, name, phone, number, checkIn, checkOut, total, "PENDING", "CONFIRMED"));
        room.available = false;
        saveBookings();
        System.out.println("Booking confirmed! Your Booking ID is " + id + ". Payment is PENDING.");
    }

    void makePayment(Scanner sc) {
        System.out.print("Enter Booking ID: ");
        Booking b = findBooking(sc.nextLine().trim());
        if (b == null) { System.out.println("Error: Booking ID not found."); return; }
        if (b.status.equals("CANCELLED")) { System.out.println("Error: Cannot pay for a cancelled booking."); return; }
        if (b.paymentStatus.equals("PAID")) { System.out.println("This booking is already paid."); return; }

        System.out.print("Confirm payment of Rs." + b.amount + " (Y/N): ");
        if (sc.nextLine().trim().equalsIgnoreCase("Y")) {
            b.paymentStatus = "PAID";
            saveBookings();
            System.out.println("Payment successful. Status updated to PAID.");
        } else {
            System.out.println("Payment not confirmed. Status remains PENDING.");
        }
    }

    void cancelBooking(Scanner sc) {
        System.out.print("Enter Booking ID to cancel: ");
        Booking b = findBooking(sc.nextLine().trim());
        if (b == null) { System.out.println("Error: Booking ID not found."); return; }
        if (b.status.equals("CANCELLED")) { System.out.println("This booking is already cancelled."); return; }

        b.status = "CANCELLED";
        Room room = findRoom(b.roomNumber);
        if (room != null) room.available = true;
        saveBookings();
        System.out.println("Booking " + b.id + " cancelled successfully.");
    }

    void viewBookingDetails(Scanner sc) {
        System.out.print("Enter Booking ID: ");
        Booking b = findBooking(sc.nextLine().trim());
        if (b == null) { System.out.println("Error: Booking ID not found."); return; }

        Room room = findRoom(b.roomNumber);
        String type = room != null ? room.type : "N/A";
        String price = room != null ? "Rs." + room.price : "N/A";
        System.out.println("\n--- Booking Details ---");
        System.out.println("Booking ID: " + b.id + " | Customer: " + b.guestName + " | Phone: " + b.phone);
        System.out.println("Room: " + b.roomNumber + " (" + type + ") | Price/Night: " + price);
        System.out.println("Check-in: " + b.checkIn.format(Booking.FMT) + " | Check-out: " +
                b.checkOut.format(Booking.FMT) + " | Nights: " + b.nights());
        System.out.println("Total Amount: Rs." + b.amount + " | Payment: " + b.paymentStatus + " | Status: " + b.status);
    }

    void viewAllBookings() {
        System.out.println("\n--- All Bookings ---");
        if (bookings.isEmpty()) { System.out.println("No bookings found."); return; }
        for (Booking b : bookings) System.out.println(b);
    }

    private void saveBookings() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Booking b : bookings) pw.println(b.toFileLine());
        } catch (IOException e) {
            System.out.println("Error saving reservations: " + e.getMessage());
        }
    }

    private void loadBookings() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Booking b = Booking.fromFileLine(line);
                bookings.add(b);

                int idNum = Integer.parseInt(b.id.replace("BK", ""));
                if (idNum > counter) counter = idNum;
                if (b.status.equals("CONFIRMED")) {
                    Room room = findRoom(b.roomNumber);
                    if (room != null) room.available = false;
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading reservations: " + e.getMessage());
        }
    }
}

// Menu-driven console interface
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Hotel hotel = new Hotel();
        System.out.println("===== Hotel Reservation System =====");
        boolean running = true;

        while (running) {
            System.out.println("\n1. View Rooms\n2. Search Rooms\n3. Book Room\n4. View Booking Details" +
                    "\n5. Make Payment\n6. Cancel Booking\n7. View All Bookings\n8. Exit");
            System.out.print("Enter choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error: Enter a valid number.");
                continue;
            }

            switch (choice) {
                case 1: hotel.showRooms(); break;
                case 2:
                    System.out.print("Enter room type (Standard/Deluxe/Suite): ");
                    hotel.searchRooms(sc.nextLine().trim());
                    break;
                case 3: hotel.bookRoom(sc); break;
                case 4: hotel.viewBookingDetails(sc); break;
                case 5: hotel.makePayment(sc); break;
                case 6: hotel.cancelBooking(sc); break;
                case 7: hotel.viewAllBookings(); break;
                case 8:
                    running = false;
                    System.out.println("Thank you for using the system. Goodbye!");
                    break;
                default:
                    System.out.println("Error: Choose a number between 1 and 8.");
            }
        }
        sc.close();
    }
}
