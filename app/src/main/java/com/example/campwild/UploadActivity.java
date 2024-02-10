package com.example.campwild;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.campwild.CampingSpot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UploadActivity extends AppCompatActivity {

    private EditText editLocationName, editLatitude, editLongitude, editDescription;
    private Button btnUpload;
    private static final int IMAGE_PICKER_REQUEST = 1;

    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Reference to the "campingSpots" node (you can change this name as per your database structure)
        databaseReference = database.getReference("campingSpots");

        editLocationName = findViewById(R.id.editLocationName);
        editLatitude = findViewById(R.id.editLatitude);
        editLongitude = findViewById(R.id.editLongitude);
        editDescription = findViewById(R.id.editDescription);
        btnUpload = findViewById(R.id.btnUpload);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadLocation();
            }
            // NEED TO WORK THIS OUT.
        });
    }

    private void uploadLocation() {
        String locationName = editLocationName.getText().toString();
        String latitude = editLatitude.getText().toString();
        String longitude = editLongitude.getText().toString();
        String description = editDescription.getText().toString();

        // Create a CampingSpot object
        CampingSpot campingSpot = new CampingSpot(locationName, latitude, longitude, description);

        // Push the camping spot data to Firebase
        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(campingSpot);

        Toast.makeText(this, "Location uploaded!", Toast.LENGTH_SHORT).show();

        // You can add further logic to save the data to your database
    }
}
