package com.example.campwild

import android.Manifest
import android.app.SearchManager
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.RatingBar
//import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.common.reflect.TypeToken
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.maps.android.PolyUtil

class MapsActivity() : AppCompatActivity(), OnMapReadyCallback, OnMarkerClickListener, GoogleMap.InfoWindowAdapter {
    private var mMap: GoogleMap? = null
    private var selectedLocationMarker: Marker? = null
    private var databaseReference: DatabaseReference? = null
    private val markersList: MutableList<Marker> = mutableListOf()
    private var searchView: SearchView? = null
    private var searchLayout: LinearLayout? = null
    private var isSearchVisible = false
    private var isSatelliteViewEnabled = false

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

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_upload -> {
                    if (selectedLocationMarker != null) {
                        val latitude = selectedLocationMarker!!.position.latitude
                        val longitude = selectedLocationMarker!!.position.longitude
                        val intent = Intent(this@MapsActivity, UploadActivity::class.java)
                        intent.putExtra("Latitude", latitude)
                        intent.putExtra("Longitude", longitude)
                        startActivity(intent)
                    } else {
                        showSnackbar("Please select a location on the map.")
                    }
                    true
                }
                R.id.action_list -> {
                    startActivity(Intent(this@MapsActivity, ListActivity::class.java))
                    true
                }
                R.id.action_toggle_satellite -> {
                    isSatelliteViewEnabled = !isSatelliteViewEnabled
                    toggleSatelliteView()
                    true
                }
                else -> false
            }

        }
    }

    private fun toggleSatelliteView() {
        if (mMap != null) {
            mMap!!.mapType = if (isSatelliteViewEnabled) GoogleMap.MAP_TYPE_SATELLITE else GoogleMap.MAP_TYPE_NORMAL
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchManager: SearchManager? = getSystemService(Context.SEARCH_SERVICE) as SearchManager?
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView?
        if (searchManager != null && searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.setIconifiedByDefault(true)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!query.isNullOrEmpty()) {
                        performSearch(query)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }
        return true
    }

    private fun performSearch(query: String) {
        mMap?.clear()
        for (marker in markersList) {
            if (marker.title?.contains(query, ignoreCase = true) == true) {
                marker.isVisible = true
            } else {
                marker.isVisible = false
            }
        }
    }
//    private fun showSearchView() {
//        val searchView = findViewById<SearchView>(R.id.searchView)
//        searchView.visibility = View.VISIBLE
//        isSearchVisible = true
//    }
//
//    private fun hideSearchView() {
//        val searchView = findViewById<SearchView>(R.id.searchView)
//        searchView.visibility = View.GONE
//        isSearchVisible = false
//    }
//    private fun showSearchView() {
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        searchLayout = findViewById(R.id.searchLayout)
//        searchLayout?.visibility = View.VISIBLE
//
//        val searchView = SearchView(toolbar.context)
//        searchLayout?.addView(searchView)
//        isSearchVisible = true
//
//        // Set up search view listener
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                // Perform search operation
//                performSearch(query)
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                // Handle text change event
//                return true
//            }
//        })
//    }
//
//    private fun hideSearchView() {
//        searchLayout?.visibility = View.GONE
//        searchLayout?.removeAllViews()
//        isSearchVisible = false
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.action_search) {
//            if (!isSearchVisible) {
//                showSearchView()
//            } else {
//                hideSearchView()
//            }
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
        popupMenu.menu.add(Menu.NONE, R.id.action_emergency, Menu.NONE, "Emergency")
        popupMenu.menu.add(Menu.NONE, R.id.action_packing, Menu.NONE, "Packing")
        popupMenu.menu.add(Menu.NONE, R.id.action_terms, Menu.NONE, "Terms & Conditions")
        popupMenu.menu.add(Menu.NONE, R.id.action_logout, Menu.NONE, "Logout")
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_information -> {
                    startActivity(Intent(this@MapsActivity, InformationActivity::class.java))
                    true
                }

                R.id.action_emergency -> {
                    startActivity(Intent(this@MapsActivity, EmergencyActivity::class.java))
                    true
                }

                R.id.action_terms -> {
                    startActivity(Intent(this@MapsActivity, TermsActivity::class.java))
                    true
                }

                R.id.action_packing -> {
                    startActivity(Intent(this@MapsActivity, PackingActivity::class.java))
                    true
                }
                R.id.action_logout -> {
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
        editor.clear()
        editor.apply()
        startActivity(Intent(this@MapsActivity, LoginActivity::class.java))
        finish()
    }
    private fun retrieveCampingSpots() {
        if (isNetworkAvailable()) {
            val database =
                FirebaseDatabase.getInstance("https://weighty-tensor-378011-default-rtdb.europe-west1.firebasedatabase.app")
            //        database.setPersistenceEnabled(true);
            databaseReference = database.getReference("campingSpots")
            databaseReference!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    markersList.clear()
                    Log.d("MapsActivity", "onDataChange called")
                    val campingSpots = mutableListOf<CampingSpot>()
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
                        val rating = ratingInteger ?: 0 
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
                                markersList.add(marker)
                                Log.d(
                                    "MapsActivity",
                                    "Marker added for Latitude: $latitude, Longitude: $longitude"
                                )
                                campingSpots.add(campingSpot)
                            } catch (e: NumberFormatException) {
                                Log.e("MapsActivity", "Parsing error: " + e.message)
                            }
                        } else {
                            Log.e("MapsActivity", "Some data is null for user: " + userSnapshot.key)
                        }
                    }
                    saveCampingSpotsToSharedPreferences(campingSpots)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("MapsActivity", "Database Error: " + databaseError.message)
                }
            })
        } else {
            retrieveCampingSpotsFromSharedPreferences()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun saveCampingSpotsToSharedPreferences(campingSpots: List<CampingSpot>) {
        val sharedPreferences = getSharedPreferences("CampingSpots", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val campingSpotsJson = gson.toJson(campingSpots)

        editor.putString("campingSpots", campingSpotsJson)
        editor.apply()
    }

    private fun retrieveCampingSpotsFromSharedPreferences() {
        val sharedPreferences = getSharedPreferences("CampingSpots", Context.MODE_PRIVATE)
        val campingSpotsJson = sharedPreferences.getString("campingSpots", null)

        if (!campingSpotsJson.isNullOrEmpty()) {
            val gson = Gson()
            val campingSpotsType = object : TypeToken<List<CampingSpot>>() {}.type
            val campingSpots = gson.fromJson<List<CampingSpot>>(campingSpotsJson, campingSpotsType)
            if (mMap != null) {
            for (campingSpot in campingSpots) {
                val marker = mMap?.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            campingSpot.latitude.toDouble(),
                            campingSpot.longitude.toDouble()
                        )
                    ).title(campingSpot.locationName)
                )
                marker?.tag = campingSpot
                marker?.snippet = campingSpot.description
                marker?.isVisible = true
                markersList.add(marker!!)
            }
        }
    }}


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.setOnMarkerClickListener(this)
        mMap?.setInfoWindowAdapter(this)
        retrieveCampingSpotsFromSharedPreferences()

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
                    showSnackbar("Please place your marker within Scotland.")
                }
            }

            private fun isLocationInScotland(location: LatLng): Boolean {
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
                val polygonOptions = PolygonOptions()
                    .addAll(scotlandBorders)
                    .strokeColor(Color.TRANSPARENT)
                    .strokeWidth(0f)

                val scotlandPolygon = mMap!!.addPolygon(polygonOptions)
                return PolyUtil.containsLocation(location, scotlandBorders, false)
            }
        })
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
//
        return false
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View? {
        val infoWindowView = layoutInflater.inflate(R.layout.custom_info_window, null)
        val titleTextView = infoWindowView.findViewById<TextView>(R.id.titleTextView)
        val descriptionTextView = infoWindowView.findViewById<TextView>(R.id.descriptionTextView)
        val imageView = infoWindowView.findViewById<ImageView>(R.id.imageView)
        val ratingBar = infoWindowView.findViewById<RatingBar>(R.id.ratingBar)

        val campingSpot = marker.tag as CampingSpot?
        titleTextView.text = marker.title
        descriptionTextView.text = marker.snippet
        ratingBar.rating = campingSpot?.rating?.toFloat() ?: 0f

        val imageUri = campingSpot?.imageUri
        if (!imageUri.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUri)
                .into(imageView)
        } else {
            imageView.visibility = View.GONE
        }

        return infoWindowView
    }

    companion object {
        private val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}