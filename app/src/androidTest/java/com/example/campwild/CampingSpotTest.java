package com.example.campwild;

import static junit.framework.TestCase.assertEquals;

import android.os.Parcel;

import org.junit.Test;

public class CampingSpotTest {

    @Test
    public void testConstructorWithParameters() {
        CampingSpot campingSpot = new CampingSpot("Location", "12.345", "67.890", "Description");
        assertEquals("Location", campingSpot.getLocationName());
        assertEquals("12.345", campingSpot.getLatitude());
        assertEquals("67.890", campingSpot.getLongitude());
        assertEquals("Description", campingSpot.getDescription());
        assertEquals("", campingSpot.getImageUri());
        assertEquals(0, campingSpot.getRating());
    }

    @Test
    public void testConstructorWithIdParameter() {
        CampingSpot campingSpot = new CampingSpot("ID123", "Location", "12.345", "67.890", "Description");
        assertEquals("ID123", campingSpot.getId());
        assertEquals("Location", campingSpot.getLocationName());
        assertEquals("12.345", campingSpot.getLatitude());
        assertEquals("67.890", campingSpot.getLongitude());
        assertEquals("Description", campingSpot.getDescription());
        assertEquals("", campingSpot.getImageUri());
        assertEquals(0, campingSpot.getRating());
        assertEquals(0, campingSpot.getTotalRatings());
        assertEquals(0, campingSpot.getTotalRatingValue());
    }

    @Test
    public void testParceling() {
        CampingSpot original = new CampingSpot("Location", "12.345", "67.890", "Description");
        original.setId("ID123");
        original.setImageUri("uri");
        original.setRating(5);
        original.setTotalRatings(10);
        original.setTotalRatingValue(50);
        Parcel parcel = Parcel.obtain();
        original.writeToParcel(parcel, original.describeContents());
        parcel.setDataPosition(0);
        CampingSpot unparceled = CampingSpot.CREATOR.createFromParcel(parcel);
        assertEquals(original.getId(), unparceled.getId());
        assertEquals(original.getLocationName(), unparceled.getLocationName());
        assertEquals(original.getLatitude(), unparceled.getLatitude());
        assertEquals(original.getLongitude(), unparceled.getLongitude());
        assertEquals(original.getDescription(), unparceled.getDescription());
        assertEquals(original.getImageUri(), unparceled.getImageUri());
        assertEquals(original.getRating(), unparceled.getRating());
        assertEquals(original.getTotalRatings(), unparceled.getTotalRatings());
        assertEquals(original.getTotalRatingValue(), unparceled.getTotalRatingValue());
    }
}