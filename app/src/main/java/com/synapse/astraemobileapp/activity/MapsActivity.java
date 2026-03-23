package com.synapse.astraemobileapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.http.UrlRequest;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.synapse.astraemobileapp.BuildConfig;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.databinding.ActivityMapsBinding;
import com.synapse.astraemobileapp.repository.MapRepository;
import com.synapse.astraemobileapp.util.DialogUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker markerPin;

    private MapRepository mapRepo;

    private String API_KEY;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        API_KEY = BuildConfig.PLACES_API_KEY;

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapRepo = new MapRepository(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new com.google.android.gms.location.LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 3000)
                .setMinUpdateIntervalMillis(2000)
                .build();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.astrae_map);
        mMap.setMapStyle(style);

        enableMyLocation();

        initializePlaces();

        setMyHomeLocation();

        mMap.setOnMapLongClickListener(lating -> {
            mMap.clear();

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(lating);
            markerPin = mMap.addMarker(markerOptions);

            markerPin = mMap.addMarker(markerOptions);
            if (markerPin == null) return;
            markerPin.setPosition(lating);

            double latitude = lating.latitude;
            double longitude = lating.longitude;
            saveLocation(latitude,longitude);
        });

    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            startLocationUpdate();

        } else {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void startLocationUpdate() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.i("GoogleMap", "current location: " + currentLocation);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
    }

    private void setMyHomeLocation() {
        LatLng home = new LatLng(6.0624677, 80.1813678);
        mMap.addMarker(new MarkerOptions().position(home).title("Marker in Home"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(home));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 25f));
    }


    private void initializePlaces() {

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), API_KEY);
        }

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    mMap.clear(); // Clear old markers
                    mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("MapsSearch", "An error occurred: " + status);
            }
        });
    }

    private void saveLocation(double latitude, double longitude) {
        DialogUtils.showCustomDialog(
                this,
                "Mark as Location",
                "Are you sure you want to mark this place as your location",
                "Ok",
                "Cancel",
                new DialogUtils.CustomDialogClickCallback() {
                    @Override
                    public void onConfirm() {
                        Map<String, Double> location = new HashMap<>();

                        location.put("latitude", latitude);
                        location.put("longitude", longitude);

                        mapRepo.saveLocation(location);
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        finish();
                    }
                }
        );
    }
}