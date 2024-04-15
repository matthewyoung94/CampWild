package com.example.campwild;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.campwild.CampingSpot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class UploadActivity extends AppCompatActivity {

    private EditText editLocationName, editLatitude, editLongitude, editDescription;
    private Button btnUpload, btnPickImage;
    private static final int IMAGE_PICKER_REQUEST = 1;

    private ImageView imagePreview;

    private DatabaseReference databaseReference;

    private StorageReference storageReference;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://weighty-tensor-378011-default-rtdb.europe-west1.firebasedatabase.app");

        databaseReference = database.getReference("campingSpots");

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://weighty-tensor-378011.appspot.com");
        storageReference = storage.getReference();

        imagePreview = findViewById(R.id.imagePreview);
        editLocationName = findViewById(R.id.editLocationName);
        editLatitude = findViewById(R.id.editLatitude);
        editLongitude = findViewById(R.id.editLongitude);
        editDescription = findViewById(R.id.editDescription);
        btnUpload = findViewById(R.id.btnUpload);
        btnPickImage = findViewById(R.id.btnPickImage);
        double latitude = getIntent().getDoubleExtra("Latitude", 0.0);
        double longitude = getIntent().getDoubleExtra("Longitude", 0.0);
        editLatitude.setText(String.valueOf(latitude));
        editLongitude.setText(String.valueOf(longitude));

        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadLocation();
            }
        });
    }

    private void uploadLocation() {
        Log.d("UploadActivity", "Uploading location...");
        String locationName = editLocationName.getText().toString();
        String latitude = editLatitude.getText().toString();
        String longitude = editLongitude.getText().toString();
        String description = editDescription.getText().toString();

        CampingSpot campingSpot = new CampingSpot(locationName, latitude, longitude, description);

        String key = databaseReference.push().getKey();
        if (key != null) {
            if (selectedImageUri != null) {
                // Get reference to the location where the image will be stored in Firebase Storage
                StorageReference imageRef = storageReference.child("images/" + key + ".jpg");

                // Upload the image to Firebase Storage
                imageRef.putFile(selectedImageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Image uploaded successfully, get the download URL
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                // Save the download URL to the CampingSpot object
                                campingSpot.setImageUri(uri.toString());

                                // Save the CampingSpot object to Firebase Realtime Database
                                databaseReference.child(key).setValue(campingSpot)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("UploadActivity", "Location uploaded successfully");
                                            Toast.makeText(this, "Location uploaded!", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("UploadActivity", "Error uploading location", e);
                                            Toast.makeText(this, "Error uploading location", Toast.LENGTH_SHORT).show();
                                        });
                            });
                        })
                        .addOnFailureListener(e -> {
                            Log.e("UploadActivity", "Error uploading image", e);
                            Toast.makeText(this, "Error uploading image", Toast.LENGTH_SHORT).show();
                        });
            } else {
                // No image selected
                databaseReference.child(key).setValue(campingSpot)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("UploadActivity", "Location uploaded successfully");
                            Toast.makeText(this, "Location uploaded!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("UploadActivity", "Error uploading location", e);
                            Toast.makeText(this, "Error uploading location", Toast.LENGTH_SHORT).show();
                        });
            }
        } else {
            Log.e("UploadActivity", "Error generating key for location");
            Toast.makeText(this, "Error uploading location", Toast.LENGTH_SHORT).show();
        }
    }


    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICKER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imagePreview.setVisibility(View.VISIBLE);
            imagePreview.setImageURI(selectedImageUri);
            Toast.makeText(this, "Image selected!", Toast.LENGTH_SHORT).show();
        }
    }
}