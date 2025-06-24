package com.example.Hotel_Reservation.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Room {
    @Id
    private int roomNumber;
    private int floor;
    private boolean booked;

    public Room() {}

    public Room(int roomNumber, int floor, boolean booked) {
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.booked = booked;
    }

    public int getRoomNumber() {
        return roomNumber; }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber; }

    public int getFloor() {
        return floor; }

    public void setFloor(int floor) {
        this.floor = floor; }

    public boolean isBooked() {
        return booked; }

    public void setBooked(boolean booked) {
        this.booked = booked; }



}
