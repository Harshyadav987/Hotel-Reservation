package com.example.Hotel_Reservation.controller;


import com.example.Hotel_Reservation.model.Booking;
import com.example.Hotel_Reservation.model.Room;
import com.example.Hotel_Reservation.repository.RoomRepository;
import com.example.Hotel_Reservation.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
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

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("rooms", roomRepo.findAll());
        return "index";
    }


    @PostMapping("/reset")
    public void reset() {
        List<Room> rooms = roomRepo.findAll();
        rooms.forEach(r -> r.setBooked(false));
        roomRepo.saveAll(rooms);
    }

    @PostMapping("/randomize")
    public List<Room> randomize() {
        List<Room> rooms = roomRepo.findAll();
        Random rand = new Random();
        rooms.forEach(r -> r.setBooked(rand.nextBoolean()));
        return roomRepo.saveAll(rooms);
    }

    @GetMapping("/rooms")
    public List<Room> allRooms() {
        return roomRepo.findAll();
    }
}
