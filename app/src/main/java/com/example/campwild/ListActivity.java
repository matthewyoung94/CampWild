package com.example.campwild;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CampingSpotListAdapter adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new CampingSpotListAdapter(this, new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        retrieveCampingSpots();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.map_item:
                    case R.id.upload_item:
                        startActivity(new Intent(ListActivity.this, MapsActivity.class));
                        return true;
                    default:
                        return false;
                }
            }
        });

    }

    private void retrieveCampingSpots() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://weighty-tensor-378011-default-rtdb.europe-west1.firebasedatabase.app");
        databaseReference = database.getReference("campingSpots");
        databaseReference.orderByChild("rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<CampingSpot> campingSpots = new ArrayList<>();
                for (DataSnapshot campingSpotSnapshot : dataSnapshot.getChildren()) {
                    String id = campingSpotSnapshot.getKey();
                    String locationName = campingSpotSnapshot.child("locationName").getValue(String.class);
                    String latitudeString = campingSpotSnapshot.child("latitude").getValue(String.class);
                    String longitudeString = campingSpotSnapshot.child("longitude").getValue(String.class);
                    String description = campingSpotSnapshot.child("description").getValue(String.class);
                    String imageUri = campingSpotSnapshot.child("imageUri").getValue(String.class);
                    Integer ratingInteger = campingSpotSnapshot.child("rating").getValue(Integer.class);
                    int rating = (ratingInteger != null) ? ratingInteger.intValue() : 0;
                    if (locationName != null && latitudeString != null && longitudeString != null && description != null) {
                        try {
                            double latitude = Double.parseDouble(latitudeString);
                            double longitude = Double.parseDouble(longitudeString);
                            CampingSpot campingSpot = new CampingSpot(id, locationName, latitudeString, longitudeString, description);
                            campingSpot.setRating(rating);
                            if (imageUri != null) {
                                campingSpot.setImageUri(imageUri);
                            }
                            campingSpots.add(campingSpot);
                        } catch (NumberFormatException e) {
                            Log.e("ListActivity", "Parsing error: " + e.getMessage());
                        }
                    } else {
                        Log.e("ListActivity", "Some data is null for camping spot");
                    }
                }
                Collections.reverse(campingSpots);
                adapter.updateCampingSpots(campingSpots);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MapsActivity", "Database Error: " + databaseError.getMessage());
            }
        });
    }
}