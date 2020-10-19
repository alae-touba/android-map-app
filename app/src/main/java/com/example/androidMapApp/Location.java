package com.example.androidMapApp;


public class Location {
    public String username, description, date;
    public double latitude, longitude;
    int numberVotes, sumVotes;

    public Location(String username, String description, String date, double latitude, double longitude) {
        this.username = username;
        this.description = description;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(String username, String description, String date, double latitude, double longitude, int numberVotes,
                    int sumVotes ) {
        this.username = username;
        this.description = description;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numberVotes = numberVotes;
        this.sumVotes = sumVotes;
    }

    @Override
    public String toString() {
        return "Location{" +
                "username='" + username + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
