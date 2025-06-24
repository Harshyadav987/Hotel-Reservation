package com.example.Hotel_Reservation.repository;

import com.example.Hotel_Reservation.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {}
