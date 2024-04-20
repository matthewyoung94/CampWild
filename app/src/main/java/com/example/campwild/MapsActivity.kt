package com.example.campwild;

import static android.content.ContentValues.TAG;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.campwild.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Marker selectedLocationMarker;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        try {
            FirebaseApp.initializeApp(this);
            Log.d("MapsActivity", "Firebase initialized successfully");
        } catch (Exception e) {
            Log.e("MapsActivity", "Firebase initialization failed", e);
        }

        com.example.campwild.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        retrieveCampingSpots();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        Button uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedLocationMarker != null) {
                    double latitude = selectedLocationMarker.getPosition().latitude;
                    double longitude = selectedLocationMarker.getPosition().longitude;
                    Intent intent = new Intent(MapsActivity.this, UploadActivity.class);
                    intent.putExtra("Latitude", latitude);
                    intent.putExtra("Longitude", longitude);
                    startActivity(intent);
                } else {
                    Toast.makeText(MapsActivity.this, "Please select a location on the map.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button listButton = findViewById(R.id.listButton);
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, ListActivity.class));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchManager != null && searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_search) {
            Log.d("MenuItemClick", "Search item clicked");
            onSearchRequested();
            return true;
        } else if (itemId == R.id.action_menu) {
            Log.d("MenuItemClick", "Menu item clicked");
            onSearchRequested();
            return true;
        } else if (itemId == R.id.action_information) {
            Log.d("MenuItemClick", "Information item clicked");
            startActivity(new Intent(MapsActivity.this, InformationActivity.class));
            return true;
        } else if (itemId == R.id.action_logout) {
            Log.d("MenuItemClick", "Logout item clicked");
//            logoutUser();
            return true;
        } else {
            Log.d("MenuItemClick", "Unhandled item clicked");
            return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onSearchRequested() {
        return super.onSearchRequested();
    }
//    private void logoutUser() {
//        FirebaseAuth.getInstance().signOut();
//        startActivity(new Intent(MapsActivity.this, SplashActivity.class));
//        finish(); // Finish the current activity to prevent the user from navigating back to it using the back button
//    }


    private void retrieveCampingSpots() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://weighty-tensor-378011-default-rtdb.europe-west1.firebasedatabase.app");
        database.setPersistenceEnabled(true);

        databaseReference = database.getReference("campingSpots");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("MapsActivity", "onDataChange called");
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Log.d("MapsActivity", "User Snapshot: " + userSnapshot.getKey());
                    String locationName = userSnapshot.child("locationName").getValue(String.class);
                    String latitudeString = userSnapshot.child("latitude").getValue(String.class);
                    String longitudeString = userSnapshot.child("longitude").getValue(String.class);
                    String description = userSnapshot.child("description").getValue(String.class);
                    String imageUri = userSnapshot.child("imageUri").getValue(String.class);
                    Integer ratingInteger = userSnapshot.child("rating").getValue(Integer.class);
                    int rating = (ratingInteger != null) ? ratingInteger : 0; // Default value if rating is null

                    if (locationName != null && latitudeString != null && longitudeString != null && description != null) {
                        try {
                            double latitude = Double.parseDouble(latitudeString);
                            double longitude = Double.parseDouble(longitudeString);
                            CampingSpot campingSpot = new CampingSpot(locationName, latitudeString, longitudeString, description);
                            campingSpot.setRating(rating);
                            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(locationName));
                            assert marker != null;
                            marker.setTag(campingSpot);
                            marker.setSnippet(description);
                            Log.d("MapsActivity", "Marker added for Latitude: " + latitude + ", Longitude: " + longitude);
                        } catch (NumberFormatException e) {
                            Log.e("MapsActivity", "Parsing error: " + e.getMessage());
                        }
                    } else {
                        Log.e("MapsActivity", "Some data is null for user: " + userSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MapsActivity", "Database Error: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setOnMyLocationButtonClickListener(() -> {
                return false;
            });
            FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            locationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15.0f));
                }
            });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        mMap.setOnMapLoadedCallback(() -> {
            LatLngBounds scotlandBounds = new LatLngBounds(
                    new LatLng(54.2, -7.7), new LatLng(59.6, -1.5));
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(scotlandBounds, 0));
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (isLocationInScotland(latLng)) {
                    if (selectedLocationMarker != null) {
                        selectedLocationMarker.remove();
                    }
                    selectedLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                } else {
                    Toast.makeText(MapsActivity.this, "Please place a marker within Scotland.", Toast.LENGTH_SHORT).show();
                }
            }

            private boolean isLocationInScotland(LatLng location) {
                // Define the coordinates for the borders of Scotland
                List<LatLng> scotlandBorders = new ArrayList<>();
                scotlandBorders.add(new LatLng(54.692467, -5.201933));
                scotlandBorders.add(new LatLng(54.533414, -5.168974));
                scotlandBorders.add(new LatLng(54.609837, -4.246123));
                scotlandBorders.add(new LatLng(54.857224, -3.422148));
                scotlandBorders.add(new LatLng(54.996110, -3.092558));
                scotlandBorders.add(new LatLng(55.378700, -2.400420));
                scotlandBorders.add(new LatLng(55.608976, -2.257597));
                scotlandBorders.add(new LatLng(55.819396, -1.971953));
                scotlandBorders.add(new LatLng(56.995482, -1.367705));
                scotlandBorders.add(new LatLng(59.106785, -1.357988));
                scotlandBorders.add(new LatLng(60.124108, -3.381973));
                scotlandBorders.add(new LatLng(58.090027, -8.633952));
                scotlandBorders.add(new LatLng(55.912840, -7.535320));
                scotlandBorders.add(new LatLng(55.367200, -6.041179));
                scotlandBorders.add(new LatLng(54.699818, -5.272136));

                // Define a polygon representing the borders of Scotland
                PolygonOptions polygonOptions = new PolygonOptions()
                        .addAll(scotlandBorders)
                        .strokeColor(Color.TRANSPARENT)  // Set border color
                        .strokeWidth(0);         // Set border width

                // Add the polygon to the map
                Polygon scotlandPolygon = mMap.addPolygon(polygonOptions);

                // Check if the location is within the polygon
                return PolyUtil.containsLocation(location, scotlandBorders, false);
            }


        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String title = marker.getTitle();
        String snippet = marker.getSnippet();
        CampingSpot campingSpot = (CampingSpot) marker.getTag();
        View infoWindowView = getLayoutInflater().inflate(R.layout.custom_info_window, null);

        TextView titleTextView = infoWindowView.findViewById(R.id.titleTextView);
        titleTextView.setText(title);

        TextView descriptionTextView = infoWindowView.findViewById(R.id.descriptionTextView);
        descriptionTextView.setText(snippet);

        // Get reference to ImageView
        ImageView imageView = infoWindowView.findViewById(R.id.imageView);

        // Get image URI from CampingSpot object
        String imageUri = campingSpot.getImageUri();

        // Load image from Firebase Storage
        if (imageUri != null && !imageUri.isEmpty()) {
            // Create a reference to the image in Firebase Storage
            StorageReference storageReference = FirebaseStorage.getInstance("gs://weighty-tensor-378011.appspot.com/images").getReferenceFromUrl(imageUri);

            // Download the image and set it to the ImageView
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(this)
                        .load(uri)
                        .into(imageView);
            }).addOnFailureListener(exception -> {
                // Handle any errors
                Log.e(TAG, "Error downloading image: " + exception.getMessage());
                imageView.setVisibility(View.GONE); // Hide ImageView on failure
            });
        } else {
            // If imageUri is null or empty, hide the ImageView
            imageView.setVisibility(View.GONE);
            Log.d(TAG, "No image URI provided");
        }

        RatingBar ratingBar = infoWindowView.findViewById(R.id.ratingBar);
        assert campingSpot != null;
        ratingBar.setRating(campingSpot.getRating());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(infoWindowView)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();

        return true;
    }
}