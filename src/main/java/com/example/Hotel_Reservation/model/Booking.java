package com.example.Hotel_Reservation.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime bookingTime;

    @ElementCollection
    private List<Integer> bookedRoomNumbers;

    private int totalTravelTime;

    public Booking() {}

    // getters & setters
    public Long getId() { return id; }

    public LocalDateTime getBookingTime() { return bookingTime; }

    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    public List<Integer> getBookedRoomNumbers() { return bookedRoomNumbers; }
    public void setBookedRoomNumbers(List<Integer> bookedRoomNumbers) { this.bookedRoomNumbers = bookedRoomNumbers; }

    public int getTotalTravelTime() { return totalTravelTime; }

    public void setTotalTravelTime(int totalTravelTime) { this.totalTravelTime = totalTravelTime; }





}
