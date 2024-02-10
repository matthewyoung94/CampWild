package com.example.campwild;

public class CampingSpot {

    private String locationName;
    private String latitude;
    private String longitude;
    private String description;

    public CampingSpot() {
        // Default constructor required for Firebase
    }

    public CampingSpot(String locationName, String latitude, String longitude, String description) {
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    // Add getters and setters as needed

    // Example:
    public String getLocationName() {
        return locationName;
    }
}
