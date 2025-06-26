package com.example.Hotel_Reservation.service;

import com.example.Hotel_Reservation.model.Booking;
import com.example.Hotel_Reservation.model.Room;
import com.example.Hotel_Reservation.repository.BookingRepository;
import com.example.Hotel_Reservation.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {
    @Autowired
    private RoomRepository roomRepo;

    @Autowired
    private BookingRepository bookingRepo;

    public Booking bookRooms(int requestedCount) {
        if (requestedCount < 1 || requestedCount > 5) {
            throw new IllegalArgumentException("You can book between 1 and 5 rooms.");
        }

        List<Room> availableRooms = roomRepo.findByBookedFalse();

        Map<Integer, List<Room>> roomsByFloor = availableRooms.stream()
                .collect(Collectors.groupingBy(Room::getFloor));

        // First try: same floor, best horizontal cluster
        for (var entry : roomsByFloor.entrySet()) {
            List<Room> floorRooms = entry.getValue();
            if (floorRooms.size() >= requestedCount) {
                List<Room> selected = pickBestOnFloor(floorRooms, requestedCount);
                return finalizeBooking(selected);
            }
        }

        // Fallback: best cross-floor combination (heuristic)
        List<Room> selected = pickBestAcrossFloors(availableRooms, requestedCount);
        return finalizeBooking(selected);
    }

    private List<Room> pickBestOnFloor(List<Room> floorRooms, int requestedCount) {
        // Sort by position on floor (left to right)
        floorRooms.sort(Comparator.comparingInt(this::getPositionOnFloor));
        List<Room> best = null;
        int minSpan = Integer.MAX_VALUE;

        for (int i = 0; i <= floorRooms.size() - requestedCount; i++) {
            int span = getPositionOnFloor(floorRooms.get(i + requestedCount - 1)) -
                    getPositionOnFloor(floorRooms.get(i));
            if (span < minSpan) {
                minSpan = span;
                best = floorRooms.subList(i, i + requestedCount);
            }
        }
        return best;
    }

    private List<Room> pickBestAcrossFloors(List<Room> rooms, int requestedCount) {
        // Sort rooms by (floor, position) to favor close clusters
        rooms.sort(Comparator
                .comparingInt(Room::getFloor)
                .thenComparingInt(this::getPositionOnFloor));

        // Greedy: pick N rooms that are closest together vertically + horizontally
        List<Room> best = null;
        int minTime = Integer.MAX_VALUE;

        for (int i = 0; i <= rooms.size() - requestedCount; i++) {
            List<Room> candidate = rooms.subList(i, i + requestedCount);
            int time = calculateTravelTime(candidate);
            if (time < minTime) {
                minTime = time;
                best = new ArrayList<>(candidate);
            }
        }
        return best;
    }

    private int getPositionOnFloor(Room room) {
        // e.g. 101 -> 1 -> 0 index, 110 -> 10 -> 9 index
        int num = room.getRoomNumber() % 100;
        return num - 1;
    }

    private int calculateTravelTime(List<Room> rooms) {
        if (rooms.size() < 2) return 0;

        rooms.sort(Comparator.comparingInt(Room::getFloor)
                .thenComparingInt(this::getPositionOnFloor));

        Room first = rooms.get(0);
        Room last = rooms.get(rooms.size() - 1);

        int vertical = Math.abs(last.getFloor() - first.getFloor()) * 2;
        int horizontal = (first.getFloor() == last.getFloor())
                ? Math.abs(getPositionOnFloor(last) - getPositionOnFloor(first))
                : 0;

        return vertical + horizontal;
    }

    private Booking finalizeBooking(List<Room> selectedRooms) {
        selectedRooms.forEach(r -> r.setBooked(true));
        roomRepo.saveAll(selectedRooms);

        Booking booking = new Booking();
        booking.setBookingTime(LocalDateTime.now());
        booking.setBookedRoomNumbers(
                selectedRooms.stream()
                        .map(Room::getRoomNumber)
                        .collect(Collectors.toList())
        );
        booking.setTotalTravelTime(calculateTravelTime(selectedRooms));
        bookingRepo.save(booking);
        return booking;
    }
}
