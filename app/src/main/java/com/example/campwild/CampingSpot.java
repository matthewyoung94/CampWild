package com.example.campwild;


import android.os.Parcel;
import android.os.Parcelable;

public class CampingSpot implements Parcelable {

    private String id;
    private String locationName;
    private String latitude;
    private String longitude;
    private String description;
    private String imageUri;

    private int rating;
    private int totalRatings;
    private int totalRatingValue;



    public CampingSpot() {

    }

    public CampingSpot(String locationName, String latitude, String longitude, String description) {
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.imageUri = "";
        this.rating = 0;
    }

    public CampingSpot(String id, String locationName, String latitude, String longitude, String description) {
        this.id = id;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.imageUri = "";
        this.rating = 0;
        this.totalRatings = 0;
        this.totalRatingValue = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    // Getters and setters for totalRatings and totalRatingValue
    public int getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }

    public int getTotalRatingValue() {
        return totalRatingValue;
    }

    public void setTotalRatingValue(int totalRatingValue) {
        this.totalRatingValue = totalRatingValue;
    }

    protected CampingSpot(Parcel in) {
        locationName = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        description = in.readString();
        imageUri = in.readString();
        rating = in.readInt();
        totalRatings = in.readInt();
        totalRatingValue = in.readInt();
    }

    public static final Creator<CampingSpot> CREATOR = new Creator<CampingSpot>() {
        @Override
        public CampingSpot createFromParcel(Parcel in) {
            return new CampingSpot(in);
        }

        @Override
        public CampingSpot[] newArray(int size) {
            return new CampingSpot[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(locationName);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(description);
        dest.writeString(imageUri);
        dest.writeInt(rating);
        dest.writeInt(totalRatings);
        dest.writeInt(totalRatingValue);
    }
}