package com.example.Hotel_Reservation.controller;

import com.example.Hotel_Reservation.model.Room;
import com.example.Hotel_Reservation.repository.RoomRepository;
import com.example.Hotel_Reservation.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Random;

@Controller
public class UIController {

    @Autowired
    private RoomRepository roomRepo;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("rooms", roomRepo.findAll());
        return "index";
    }

    @PostMapping("/book")
    public String book(@RequestParam int count, Model model) {
        bookingService.bookRooms(count);
        model.addAttribute("rooms", roomRepo.findAll());
        return "index";
    }

    @PostMapping("/reset")
    public String reset(Model model) {
        List<Room> rooms = roomRepo.findAll();
        rooms.forEach(r -> r.setBooked(false));
        roomRepo.saveAll(rooms);
        model.addAttribute("rooms", rooms);
        return "index";
    }

    @PostMapping("/randomize")
    public String randomize(Model model) {
        List<Room> rooms = roomRepo.findAll();
        Random rand = new Random();
        rooms.forEach(r -> r.setBooked(rand.nextBoolean()));
        roomRepo.saveAll(rooms);
        model.addAttribute("rooms", rooms);
        return "index";
    }
}