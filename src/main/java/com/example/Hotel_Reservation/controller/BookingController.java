package com.example.Hotel_Reservation.controller;


import com.example.Hotel_Reservation.model.Booking;
import com.example.Hotel_Reservation.model.Room;
import com.example.Hotel_Reservation.repository.RoomRepository;
import com.example.Hotel_Reservation.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private RoomRepository roomRepo;

    @PostMapping("/book")
    public Booking bookRooms(@RequestParam int count) {
        return bookingService.bookRooms(count);
    }

    @PostMapping("/reset")
    public void reset() {
        List<Room> rooms = roomRepo.findAll();
        rooms.forEach(r -> r.setBooked(false));
        roomRepo.saveAll(rooms);
    }

    @PostMapping("/randomize")
    public void randomize() {
        List<Room> rooms = roomRepo.findAll();
        Random rand = new Random();
        rooms.forEach(r -> r.setBooked(rand.nextBoolean()));
        roomRepo.saveAll(rooms);
    }

    @GetMapping("/rooms")
    public List<Room> allRooms() {
        return roomRepo.findAll();
    }
}
