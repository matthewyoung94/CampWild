package com.example.campwild;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CampingSpotListAdapter extends RecyclerView.Adapter<CampingSpotListAdapter.ViewHolder> {

    private List<CampingSpot> campingSpots;
    private Context context;


    public CampingSpotListAdapter(Context context, List<CampingSpot> campingSpots) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.campingSpots = campingSpots;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_camping_spot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CampingSpot campingSpot = campingSpots.get(position);
        holder.titleTextView.setText(campingSpot.getLocationName());
        holder.descriptionTextView.setText(campingSpot.getDescription());
        Log.d("Rating", "Rating for " + campingSpot.getLocationName() + ": " + campingSpot.getRating()); // Add this line for logging

            if (campingSpot.getImageUri() != null && !campingSpot.getImageUri().isEmpty()) {
                try {
                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(campingSpot.getImageUri());
                    storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            Glide.with(context)
                                    .load(bitmap)
                                    .into(holder.imageView);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Glide.with(context)
                                    .load(R.drawable.campwildlogo) // Use your placeholder image here
                                    .into(holder.imageView);
                            // Handle any errors
                            Log.e("CampingSpotListAdapter", "Failed to download image: " + exception.getMessage());
                        }
                    });
                } catch (IllegalArgumentException e) {
                    // Handle the case where the imageUri cannot be parsed correctly
                    Log.e("CampingSpotListAdapter", "Failed to parse imageUri: " + e.getMessage());
                    // Load a placeholder image
                    Glide.with(context)
                            .load(R.drawable.campwildlogo) // Use your placeholder image here
                            .into(holder.imageView);
                }
            }
        float averageRating = calculateAverageRating(campingSpot);
        holder.ratingBar.setRating(campingSpot.getRating());
        holder.rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog(campingSpot);
            }
        });
    }

    @Override
    public int getItemCount() {
        return campingSpots.size();
    }

    public void updateCampingSpots(List<CampingSpot> newCampingSpots) {
        campingSpots.clear();
        campingSpots.addAll(newCampingSpots);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView descriptionTextView;
        public Button rateButton;
        public RatingBar ratingBar;

        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            rateButton = itemView.findViewById(R.id.rateButton);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    private void showRatingDialog(CampingSpot campingSpot) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Rate Camping Spot");

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_rating, null);
        builder.setView(view);

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                float ratingValue = ratingBar.getRating();
                saveRatingToDatabase(campingSpot, ratingValue);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private float calculateAverageRating(CampingSpot campingSpot) {
        int totalRatings = campingSpot.getTotalRatings();
        int totalRatingValue = campingSpot.getTotalRatingValue();
        if (totalRatings != 0) {
            return (float) totalRatingValue / totalRatings;
        } else {
            return 0; // Return 0 if no ratings available
        }
    }

    private void saveRatingToDatabase(CampingSpot campingSpot, float ratingValue) {
        String campingSpotId = campingSpot.getId();
        if (campingSpotId != null) {
            DatabaseReference campingSpotRef = FirebaseDatabase.getInstance("https://weighty-tensor-378011-default-rtdb.europe-west1.firebasedatabase.app")
                    .getReference("campingSpots")
                    .child(campingSpotId);

            campingSpotRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    CampingSpot updatedCampingSpot = dataSnapshot.getValue(CampingSpot.class);
                    if (updatedCampingSpot != null) {
                        int totalRatings = updatedCampingSpot.getTotalRatings() + 1;
                        int totalRatingValue = updatedCampingSpot.getTotalRatingValue() + (int) ratingValue;
                        float averageRating = (float) totalRatingValue / totalRatings;

                        campingSpotRef.child("totalRatings").setValue(totalRatings);
                        campingSpotRef.child("totalRatingValue").setValue(totalRatingValue);
                        campingSpotRef.child("rating").setValue(averageRating);
                        campingSpot.setRating((int) averageRating);

                        Toast.makeText(context, "Rating submitted successfully", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    } else {
                        Log.e("SaveRating", "Failed to retrieve camping spot data from the database");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("SaveRating", "Database error: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e("SaveRating", "Camping spot ID is null");
        }
    }
}