package com.example.campwild

import android.Manifest
import android.app.SearchManager
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RatingBar
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.campwild.databinding.ActivityMapsBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.maps.android.PolyUtil

class MapsActivity() : AppCompatActivity(), OnMapReadyCallback, OnMarkerClickListener {
    private var mMap: GoogleMap? = null
    private var selectedLocationMarker: Marker? = null
    private var databaseReference: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        try {
//            FirebaseApp.initializeApp(this);
//            Log.d("MapsActivity", "Firebase initialized successfully");
//        } catch (Exception e) {
//            Log.e("MapsActivity", "Firebase initialization failed", e);
//        }
        val binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        retrieveCampingSpots()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        val uploadButton = findViewById<Button>(R.id.uploadButton)
        uploadButton.setOnClickListener {
            if (selectedLocationMarker != null) {
                val latitude = selectedLocationMarker!!.position.latitude
                val longitude = selectedLocationMarker!!.position.longitude
                val intent = Intent(this@MapsActivity, UploadActivity::class.java)
                intent.putExtra("Latitude", latitude)
                intent.putExtra("Longitude", longitude)
                startActivity(intent)
            } else {
                Toast.makeText(
                    this@MapsActivity,
                    "Please select a location on the map.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        val listButton = findViewById<Button>(R.id.listButton)
        listButton.setOnClickListener {
            startActivity(
                Intent(
                    this@MapsActivity,
                    ListActivity::class.java
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchManager: SearchManager? = getSystemService(SEARCH_SERVICE) as SearchManager
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView?
        if (searchManager != null && searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.isIconifiedByDefault = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.action_search) {
            Log.d("MenuItemClick", "Search item clicked")
            onSearchRequested()
            return true
        } else if (itemId == R.id.action_menu) {
            Log.d("MenuItemClick", "Menu item clicked")
            val view = findViewById<View>(R.id.action_menu)
            showPopupMenu(view)
            return true
        } else {
            Log.d("MenuItemClick", "Unhandled item clicked")
            return super.onOptionsItemSelected(item)
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menu.add(Menu.NONE, R.id.action_information, Menu.NONE, "Information")
        popupMenu.menu.add(Menu.NONE, R.id.action_logout, Menu.NONE, "Logout")
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            // Handle popup menu item clicks here
            when (item.itemId) {
                R.id.action_information -> {
                    // Handle information action
                    startActivity(Intent(this@MapsActivity, InformationActivity::class.java))
                    true
                }

                R.id.action_logout -> {
                    // Handle logout action
                    logoutUser()
                    true
                }

                else -> false
            }
        })
        popupMenu.show()
    }

    override fun onSearchRequested(): Boolean {
        return super.onSearchRequested()
    }

    private fun logoutUser() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = preferences.edit()
        editor.clear() // Clear all stored preferences
        editor.apply()
        startActivity(Intent(this@MapsActivity, LoginActivity::class.java))
        finish()
    }

    private fun retrieveCampingSpots() {
        val database =
            FirebaseDatabase.getInstance("https://weighty-tensor-378011-default-rtdb.europe-west1.firebasedatabase.app")
        //        database.setPersistenceEnabled(true);
        databaseReference = database.getReference("campingSpots")
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("MapsActivity", "onDataChange called")
                for (userSnapshot: DataSnapshot in dataSnapshot.children) {
                    Log.d("MapsActivity", "User Snapshot: " + userSnapshot.key)
                    val locationName = userSnapshot.child("locationName").getValue(
                        String::class.java
                    )
                    val latitudeString = userSnapshot.child("latitude").getValue(
                        String::class.java
                    )
                    val longitudeString = userSnapshot.child("longitude").getValue(
                        String::class.java
                    )
                    val description = userSnapshot.child("description").getValue(
                        String::class.java
                    )
                    val imageUri = userSnapshot.child("imageUri").getValue(
                        String::class.java
                    )
                    val ratingInteger = userSnapshot.child("rating").getValue(Int::class.java)
                    val rating = ratingInteger ?: 0 // Default value if rating is null
                    if ((locationName != null) && (latitudeString != null) && (longitudeString != null) && (description != null)) {
                        try {
                            val latitude = latitudeString.toDouble()
                            val longitude = longitudeString.toDouble()
                            val campingSpot = CampingSpot(
                                locationName,
                                latitudeString,
                                longitudeString,
                                description
                            )
                            campingSpot.rating = rating
                            val marker = mMap!!.addMarker(
                                MarkerOptions().position(
                                    LatLng(
                                        latitude,
                                        longitude
                                    )
                                ).title(locationName)
                            )
                            assert(marker != null)
                            marker!!.tag = campingSpot
                            marker.snippet = description
                            Log.d(
                                "MapsActivity",
                                "Marker added for Latitude: $latitude, Longitude: $longitude"
                            )
                        } catch (e: NumberFormatException) {
                            Log.e("MapsActivity", "Parsing error: " + e.message)
                        }
                    } else {
                        Log.e("MapsActivity", "Some data is null for user: " + userSnapshot.key)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("MapsActivity", "Database Error: " + databaseError.message)
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap!!.isMyLocationEnabled = true
            mMap!!.uiSettings.isMyLocationButtonEnabled = true
            mMap!!.setOnMyLocationButtonClickListener({ false })
            val locationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            locationProviderClient.lastLocation.addOnSuccessListener({ location: Location? ->
                if (location != null) {
                    val userLocation: LatLng =
                        LatLng(location.getLatitude(), location.getLongitude())
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15.0f))
                }
            })
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
        mMap!!.setOnMapLoadedCallback({
            val scotlandBounds: LatLngBounds = LatLngBounds(
                LatLng(54.2, -7.7), LatLng(59.6, -1.5)
            )
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(scotlandBounds, 0))
        })
        mMap!!.setOnMapClickListener(object : OnMapClickListener {
            override fun onMapClick(latLng: LatLng) {
                if (isLocationInScotland(latLng)) {
                    if (selectedLocationMarker != null) {
                        selectedLocationMarker!!.remove()
                    }
                    selectedLocationMarker = mMap!!.addMarker(
                        MarkerOptions().position(latLng).title("Selected Location")
                    )
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                } else {
                    Toast.makeText(
                        this@MapsActivity,
                        "Please place a marker within Scotland.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            private fun isLocationInScotland(location: LatLng): Boolean {
                // Define the coordinates for the borders of Scotland
                val scotlandBorders: MutableList<LatLng> = ArrayList()
                scotlandBorders.add(LatLng(54.692467, -5.201933))
                scotlandBorders.add(LatLng(54.533414, -5.168974))
                scotlandBorders.add(LatLng(54.609837, -4.246123))
                scotlandBorders.add(LatLng(54.857224, -3.422148))
                scotlandBorders.add(LatLng(54.996110, -3.092558))
                scotlandBorders.add(LatLng(55.378700, -2.400420))
                scotlandBorders.add(LatLng(55.608976, -2.257597))
                scotlandBorders.add(LatLng(55.819396, -1.971953))
                scotlandBorders.add(LatLng(56.995482, -1.367705))
                scotlandBorders.add(LatLng(59.106785, -1.357988))
                scotlandBorders.add(LatLng(60.124108, -3.381973))
                scotlandBorders.add(LatLng(58.090027, -8.633952))
                scotlandBorders.add(LatLng(55.912840, -7.535320))
                scotlandBorders.add(LatLng(55.367200, -6.041179))
                scotlandBorders.add(LatLng(54.699818, -5.272136))

                // Define a polygon representing the borders of Scotland
                val polygonOptions = PolygonOptions()
                    .addAll(scotlandBorders)
                    .strokeColor(Color.TRANSPARENT) // Set border color
                    .strokeWidth(0f) // Set border width

                // Add the polygon to the map
                val scotlandPolygon = mMap!!.addPolygon(polygonOptions)

                // Check if the location is within the polygon
                return PolyUtil.containsLocation(location, scotlandBorders, false)
            }
        })
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val title = marker.title
        val snippet = marker.snippet
        val campingSpot = marker.tag as CampingSpot?
        val infoWindowView = layoutInflater.inflate(R.layout.custom_info_window, null)
        val titleTextView = infoWindowView.findViewById<TextView>(R.id.titleTextView)
        titleTextView.text = title
        val descriptionTextView = infoWindowView.findViewById<TextView>(R.id.descriptionTextView)
        descriptionTextView.text = snippet

        // Get reference to ImageView
        val imageView = infoWindowView.findViewById<ImageView>(R.id.imageView)


        // Get image URI from CampingSpot object
        val imageUri = campingSpot!!.imageUri
        Log.d(
            "Image URI",
            "Image URI for " + campingSpot.locationName + ": " + campingSpot.imageUri
        )
        // Load image from Firebase Storage
        if (imageUri != null && !imageUri.isEmpty()) {
            // Create a reference to the image in Firebase Storage
            val storageReference =
                FirebaseStorage.getInstance("gs://weighty-tensor-378011.appspot.com/images")
                    .getReferenceFromUrl(imageUri)

            // Download the image and set it to the ImageView
            storageReference.downloadUrl.addOnSuccessListener({ uri: Uri? ->
                Glide.with(this)
                    .load(uri)
                    .into(imageView)
            }).addOnFailureListener({ exception: Exception ->
                // Handle any errors
                Log.e(ContentValues.TAG, "Error downloading image: " + exception.message)
                imageView.setVisibility(View.GONE) // Hide ImageView on failure
            })
        } else {
            // If imageUri is null or empty, hide the ImageView
            imageView.visibility = View.GONE
            Log.d(ContentValues.TAG, "No image URI provided")
        }
        val ratingBar = infoWindowView.findViewById<RatingBar>(R.id.ratingBar)
        assert(campingSpot != null)
        ratingBar.rating = campingSpot.rating.toFloat()
        val builder = AlertDialog.Builder(this)
        builder.setView(infoWindowView)
            .setPositiveButton("OK", { dialog: DialogInterface, which: Int -> dialog.dismiss() })
            .show()
        return true
    }

    companion object {
        private val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}