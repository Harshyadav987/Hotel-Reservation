package com.example.Hotel_Reservation.util;


import com.example.Hotel_Reservation.model.Room;
import com.example.Hotel_Reservation.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoomRepository roomRepo;

    public DataInitializer(RoomRepository roomRepo) {
        this.roomRepo = roomRepo;
    }

    @Override
    public void run(String... args) {
        if (roomRepo.count() == 0) {
            List<Room> rooms = new ArrayList<>();
            for (int floor = 1; floor <= 9; floor++) {
                for (int i = 1; i <= 10; i++) {
                    rooms.add(new Room(floor * 100 + i, floor, false));
                }
            }
            for (int i = 1; i <= 7; i++) {
                rooms.add(new Room(1000 + i, 10, false));
            }
            roomRepo.saveAll(rooms);
        }
    }
}
