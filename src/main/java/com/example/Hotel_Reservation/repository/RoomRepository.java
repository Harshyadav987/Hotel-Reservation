package com.example.Hotel_Reservation.repository;

import com.example.Hotel_Reservation.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    List<Room> findByBookedFalse();
}