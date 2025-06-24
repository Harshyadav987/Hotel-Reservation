package com.example.Hotel_Reservation.service;


import com.example.Hotel_Reservation.model.Booking;
import com.example.Hotel_Reservation.model.Room;
import com.example.Hotel_Reservation.repository.BookingRepository;
import com.example.Hotel_Reservation.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

        // Try same floor
        for (var entry : roomsByFloor.entrySet()) {
            List<Room> floorRooms = entry.getValue();
            if (floorRooms.size() >= requestedCount) {
                List<Room> selected = pickBestOnFloor(floorRooms, requestedCount);
                return finalizeBooking(selected);
            }
        }

        // Across floors (simplified: pick first N rooms)
        List<Room> selected = pickBestAcrossFloors(availableRooms, requestedCount);
        return finalizeBooking(selected);

    }

    private List<Room> pickBestOnFloor(List<Room> floorRooms, int requestedCount) {
        floorRooms.sort(Comparator.comparing(Room::getRoomNumber));
        List<Room> best = null;
        int minSpan = Integer.MAX_VALUE;

        for (int i = 0; i <= floorRooms.size() - requestedCount; i++) {
            int span = floorRooms.get(i + requestedCount - 1).getRoomNumber() -
                    floorRooms.get(i).getRoomNumber();
            if (span < minSpan) {
                minSpan = span;
                best = floorRooms.subList(i, i + requestedCount);
            }
        }
        return best;
    }

    private Booking finalizeBooking(List<Room> selectedRooms) {
        selectedRooms.forEach(r -> r.setBooked(true));
        roomRepo.saveAll(selectedRooms);

        Booking booking = new Booking();
        booking.setBookingTime(LocalDateTime.now());
        booking.setBookedRoomNumbers(selectedRooms.stream()
                .map(Room::getRoomNumber)
                .collect(Collectors.toList()));
        booking.setTotalTravelTime(calculateTravelTime(selectedRooms));
        bookingRepo.save(booking);
        return booking;
    }
    private int calculateTravelTime(List<Room> rooms) {
        if (rooms.size() < 2) return 0;
        rooms.sort(Comparator.comparing(Room::getFloor).thenComparing(Room::getRoomNumber));
        Room first = rooms.get(0);
        Room last = rooms.get(rooms.size() - 1);
        int vertical = Math.abs(last.getFloor() - first.getFloor()) * 2;
        int horizontal = (first.getFloor() == last.getFloor()) ?
                Math.abs(last.getRoomNumber() - first.getRoomNumber()) : 0;
        return vertical + horizontal;
    }
    private List<List<Room>> generateCombinations(List<Room> rooms, int count) {
        List<List<Room>> result = new ArrayList<>();
        generateCombRecursive(rooms, count, 0, new ArrayList<>(), result);
        return result;
    }

    private List<Room> pickBestAcrossFloors(List<Room> rooms, int requestedCount) {
        List<List<Room>> combinations = generateCombinations(rooms, requestedCount);
        List<Room> bestCombo = null;
        int minTime = Integer.MAX_VALUE;

        for (List<Room> combo : combinations) {
            int time = calculateTravelTime(combo);
            if (time < minTime) {
                minTime = time;
                bestCombo = combo;
            }
        }
        return bestCombo;
    }


    private void generateCombRecursive(List<Room> rooms, int count, int index,
                                       List<Room> current, List<List<Room>> result) {
        if (current.size() == count) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = index; i < rooms.size(); i++) {
            current.add(rooms.get(i));
            generateCombRecursive(rooms, count, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }


}

